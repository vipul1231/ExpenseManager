package org.em.app;

import org.em.app.domain.CreateExpense;
import org.em.app.domain.Group;
import org.em.app.domain.User;
import org.em.app.manager.GroupManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AppStartTest {

	private GroupManager groupManager;

	private Group group;

	@BeforeEach
	public void before() {
		groupManager = new GroupManager();
		group = new Group("tripName");
		groupManager.registerGroup(group);
		//GroupExpenseProcessor groupExpenseProcessor = new GroupExpenseProcessor(group);
		System.out.println("Trip "+group.getGroupName()+" created successfully");
		//Create Participants

		User user = new User("Deepak");
		group.registerUser(user);
		//groupExpenseProcessor.createExpenseListForUser(user);
		System.out.println("Participant "+user.getName()+" created");

		user = new User("Jai");
		group.registerUser(user);
		//groupExpenseProcessor.createExpenseListForUser(user);
		System.out.println("Participant "+user.getName()+" created");

		//Create category
		group = groupManager.getGroupOnName(group.getGroupName());
		group.registerCategory("Food");
		System.out.println("Category Food created");

		group = groupManager.getGroupOnName(group.getGroupName());
		group.registerCategory("Travel");
		System.out.println("Category Travel created");
	}

	@Test
	public void testApplicationInWhichUserOnlyDoEqualSplit() {

		CreateExpense expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.EQUAL)
				.category("Travel").finalAmount(2000).expenseShareList(Collections.singletonList(CreateExpense.ExpenseShare.builder()
						.userName("Deepak").build())).build();
		group.getExpenseProcessor().processExpense(expense);

		expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.EQUAL)
				.category("Mis").finalAmount(2000).expenseShareList(Collections.singletonList(CreateExpense.ExpenseShare.builder()
						.userName("Jai").build())).build();
		group.getExpenseProcessor().processExpense(expense);

		Assertions.assertEquals(getResponseText2(), group.getExpenseProcessor().getExpenseSummaryForAllUsersInGroup());
		Assertions.assertEquals("", group.getExpenseProcessor().showTransitiveSettlement());
	}

	@Test
	public void testApplicationInWhichUserOnlyDoUnEqualSplit() {

		List<CreateExpense.ExpenseShare> expenseShareList = new ArrayList<>();
		expenseShareList.add(CreateExpense.ExpenseShare.builder().userName("Jai").percentage(60).build());
		expenseShareList.add(CreateExpense.ExpenseShare.builder().userName("Deepak").percentage(40).build());

		CreateExpense expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.UNEQUAL)
				.category("Travel").finalAmount(4000).expenseShareList(expenseShareList).primaryUserName("Jai").build();
		group.getExpenseProcessor().processExpense(expense);

		Assertions.assertEquals(getResponseText3(), group.getExpenseProcessor().getExpenseSummaryForAllUsersInGroup());
		Assertions.assertEquals("Jai owes Deepak 1600.00 INR\n", group.getExpenseProcessor().showTransitiveSettlement());
	}

	@Test
	public void testApplicationWithMixOfEqualAndUnEqualExpense() {

		//Create group
		CreateExpense expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.EQUAL)
				.category("Food").finalAmount(2000).expenseShareList(Collections.singletonList(CreateExpense.ExpenseShare.builder()
						.userName("Deepak").build())).build();
		group.getExpenseProcessor().processExpense(expense);

		expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.EQUAL)
				.category("Food").finalAmount(5000).expenseShareList(Collections.singletonList(CreateExpense.ExpenseShare.builder()
						.userName("Jai").build())).build();
		group.getExpenseProcessor().processExpense(expense);

		List<CreateExpense.ExpenseShare> expenseShareList = new ArrayList<>();
		expenseShareList.add(CreateExpense.ExpenseShare.builder().userName("Jai").percentage(75).build());
		expenseShareList.add(CreateExpense.ExpenseShare.builder().userName("Deepak").percentage(25).build());

		expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.UNEQUAL)
				.category("Travel").finalAmount(4000).expenseShareList(expenseShareList).primaryUserName("Jai").build();
		group.getExpenseProcessor().processExpense(expense);

		Assertions.assertEquals(getResponseText1(), group.getExpenseProcessor().getExpenseSummaryForAllUsersInGroup());

		Assertions.assertEquals("Jai owes Deepak 2500.00 INR\n", group.getExpenseProcessor().showTransitiveSettlement());
	}

	@Test
	public void testScenarioWithThreeUsers() {
		// Jai Deepak Ankita
		User user = new User("Ankita");
		group.registerUser(user);
		System.out.println("Participant "+user.getName()+" created");

		CreateExpense expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.EQUAL)
				.category("Travel").finalAmount(2500).expenseShareList(Collections.singletonList(CreateExpense.ExpenseShare.builder()
						.userName("Jai").build())).build();
		group.getExpenseProcessor().processExpense(expense);

		expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.EQUAL)
				.category("Mis").finalAmount(4200).expenseShareList(Collections.singletonList(CreateExpense.ExpenseShare.builder()
						.userName("Deepak").build())).build();
		group.getExpenseProcessor().processExpense(expense);


		expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.EQUAL)
				.category("Food").finalAmount(2900).expenseShareList(Collections.singletonList(CreateExpense.ExpenseShare.builder()
						.userName("Ankita").build())).build();
		group.getExpenseProcessor().processExpense(expense);

		List<CreateExpense.ExpenseShare> expenseShareList = new ArrayList<>();
		expenseShareList.add(CreateExpense.ExpenseShare.builder().userName("Deepak").percentage(50).build());
		expenseShareList.add(CreateExpense.ExpenseShare.builder().userName("Ankita").percentage(50).build());

		expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.UNEQUAL)
				.category("Travel").finalAmount(1000).expenseShareList(expenseShareList).primaryUserName("Deepak").build();
		group.getExpenseProcessor().processExpense(expense);

		Assertions.assertEquals(getResponseText4(), group.getExpenseProcessor().getExpenseSummaryForAllUsersInGroup());

		Assertions.assertEquals(getTransitiveSummary(), group.getExpenseProcessor().showTransitiveSettlement());
	}

	private String getTransitiveSummary() {
		return "Deepak owes Jai 566.67 INR\n" +
				"Deepak owes Ankita 933.33 INR\n" +
				"Ankita owes Jai 133.33 INR\n";
	}

	private String getResponseText4() {
		return "Total Expense Made: 10600\n" +
				"mis: 4200\n" +
				"travel: 3500\n" +
				"food: 2900\n" +
				"Deepak owes 1799 and is owned 3300\n" +
				"Jai owes 2366 and is owned 1666\n" +
				"Ankita owes 2733 and is owned 1932\n";
	}

	private String getResponseText3() {
		return "Total Expense Made: 4000\n" +
				"travel: 4000\n" +
				"food: 0\n" +
				"Deepak owes 1600 and is owned 0\n" +
				"Jai owes 0 and is owned 1600\n";
	}

	private String getResponseText2() {
		return "Total Expense Made: 4000\n" +
				"mis: 2000\n"+
				"travel: 2000\n" +
				"food: 0\n" +
				"Deepak owes 1000 and is owned 1000\n" +
				"Jai owes 1000 and is owned 1000\n";
	}

	private String getResponseText1() {
		return "Total Expense Made: 11000\n" +
				"travel: 4000\n" +
				"food: 7000\n" +
				"Deepak owes 3500 and is owned 1000\n" +
				"Jai owes 1000 and is owned 3500\n";
	}

}
