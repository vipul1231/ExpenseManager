package org.em.app.processor;

import org.em.app.constant.ExpenseConstant;
import org.em.app.domain.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Group expense processor. This class is individual for its respective groups.
 */
public class GroupExpenseProcessor {

	private final List<List<IExpense>> userExpenses;

	private final Map<String, Integer> userToIndexMap;

	private final Group group;

	/**
	 * Constructor
	 * TODO: provided group in constructor
	 */
	public GroupExpenseProcessor(Group group) {
		this.userExpenses = new ArrayList<>();
		userToIndexMap = new HashMap<>();
		this.group = group;
	}

	/**
	 * Create expenseList for used and map the index in map.
	 *
	 * @param user
	 */
	public void createExpenseListForUser(User user) {
		userExpenses.add(new ArrayList<>());
		int index = userExpenses.size() - 1;
		userToIndexMap.put(user.getName(), index);
	}

	/**
	 * This method will process the expenses of group
	 *
	 * @param expense
	 */
	public void processExpense(CreateExpense expense) {

		if (expense == null || expense.getExpenseShareList().size() == 0) {
			return;
		}

		group.addValueToCounter(ExpenseConstant.TOTAL_EXPENSE, (long) expense.getFinalAmount());
		group.addValueToCounter(ExpenseConstant.CATEGORY + group.getCategory(expense.getCategory().toLowerCase()), (long) expense.getFinalAmount());
		String userName = expense.getExpenseShareList().get(0).getUserName();
		Optional<User> userOptional = group.getUser(userName);

		if (userOptional.isPresent()) {
			if (expense.getExpenseType().equals(CreateExpense.ExpenseType.EQUAL)) {

				group.addValueToCounter(ExpenseConstant.USER + userName, (long) expense.getFinalAmount());
				List<User> userList = group.getListOfUserExcludingGivenUser(userName);
				double amount = getShareFromGivenAmount(expense.getFinalAmount(), 0);

				for (User user : userList) {
					IExpense exp = new EqualSplitExpense(userOptional.get(), group.getCategory(expense.getCategory()), amount);
					getUserExpenseList(user).add(exp);
				}
			} else if (expense.getExpenseType().equals(CreateExpense.ExpenseType.UNEQUAL)) {
				List<CreateExpense.ExpenseShare> expenseShareList = expense.getExpenseShareList();
				double amount = expense.getFinalAmount();
				String primaryExpenseBearer = expense.getPrimaryUserName();

				group.addValueToCounter(ExpenseConstant.USER + userName, (long) expense.getFinalAmount());
				for (CreateExpense.ExpenseShare expenseShare : expenseShareList) {
					String expUserName = expenseShare.getUserName();
					int percentage = expenseShare.getPercentage();
					Optional<User> optionalUser = group.getUser(expUserName);

					if (percentage != 0 && optionalUser.isPresent()) {
						double amt = getShareFromGivenAmount(amount, percentage);
						List<IExpense> expenseList = getUserExpenseList(optionalUser.get());

						if (!primaryExpenseBearer.equals(optionalUser.get().getName())) {
							IExpense exp = new PercentageSplitExpense(userOptional.get(), group.getCategory(expense.getCategory()), amt, percentage);
							expenseList.add(exp);
						}
					}
				}
			}
		}
	}

	/**
	 * Return expense list for the user.
	 */
	public List<IExpense> getUserExpenseList(User user) {
		return userExpenses.get(userToIndexMap.get(user.getName()));
	}

	/**
	 * Populate summary for all users.
	 *
	 * @return
	 */
	public String getExpenseSummaryForAllUsersInGroup() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Total Expense Made: ").append(group.getValueOfCounter(ExpenseConstant.TOTAL_EXPENSE))
				.append("\n");

		for (String cat : group.getCategory()) {
			if(group.isCounterPresent(ExpenseConstant.CATEGORY+cat)) {
				stringBuilder.append(cat).append(": ").append(group.getValueOfCounter(ExpenseConstant.CATEGORY+cat)).append("\n");
			}
		}

		List<User> usersList = group.getUsers();
		for (User user : usersList) {
			List<IExpense> allExpense = getUserExpenseList(user);
			for (IExpense expense : allExpense) {
				User giveToUser = expense.getUser();
				double amt = expense.getFinalAmount();
				group.addValueToCounter(ExpenseConstant.USER_OWES+user.getName(), (long) amt);
				group.addValueToCounter(ExpenseConstant.USER_OWED+giveToUser.getName(), (long) amt);
			}
		}


		for (User user : usersList) {
			stringBuilder.append(user.getName())
					.append(" owes ")
					.append(group.getValueOfCounter(ExpenseConstant.USER_OWES + user.getName())).append(" and is owned ")
					.append(group.getValueOfCounter(ExpenseConstant.USER_OWED + user.getName())).append("\n");
		}
		return stringBuilder.toString();
	}

	/**
	 * Show transitive settlement between users.
	 *
	 */
	public String showTransitiveSettlement() {
		final DecimalFormat df = new DecimalFormat("0.00");
		List<User> userList = group.getUsers();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0;i<userList.size();i++) {
			for (int j=i;j<userList.size();j++) {
				if (i != j) {
					User user1 = userList.get(i);
					User user2 = userList.get(j);

					List<IExpense> expense1 = getUserExpenseList(user1);
					List<IExpense> expense2 = getUserExpenseList(user2);

					double expAmount1 = expense1.stream().filter(k -> user2.getName().equals(k.getUser().getName())).map(IExpense::getFinalAmount).reduce(0d, Double::sum);

					double expAmount2 = expense2.stream().filter(k -> user1.getName().equals(k.getUser().getName())).map(IExpense::getFinalAmount).reduce(0d, Double::sum);

					if (expAmount2 > expAmount1) {
						stringBuilder.append(user1.getName()).append(" owes ").append(user2.getName()).append(" ").append(df.format(expAmount2 - expAmount1)).append(" INR\n");
					}
					else if (expAmount2 < expAmount1) {
						stringBuilder.append(user2.getName()).append(" owes ").append(user1.getName()).append(" ").append(df.format(expAmount1 - expAmount2)).append(" INR\n");
					}
				}
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Get amount value from the share
	 *
	 * @param amount
	 * @param percentage
	 * @return
	 */
	public double getShareFromGivenAmount(double amount, int percentage) {
		if (percentage == 0) {
			return amount / group.getTotalUsersInGroup();
		} else {
			return amount * (percentage / 100d);
		}
	}
}
