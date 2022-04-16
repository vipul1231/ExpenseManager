package org.em.app;

import org.em.app.domain.CreateExpense;
import org.em.app.domain.Group;
import org.em.app.domain.User;
import org.em.app.manager.GroupManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class AppStart {

	private final GroupManager groupManager;


	public AppStart() {
		groupManager = new GroupManager();
	}


	public void menuDisplay() {
		Scanner scanner = new Scanner(System.in);

		boolean running = true;
		while (running) {
			System.out.println("Select options :\n" +
					"1. Create Trip.\n" +
					"2. Create Participant.\n" +
					"3. Create Category.\n" +
					"4. Note down expense.\n" +
					"5. Note unequal expense.\n" +
					"6. Show summary.\n" +
					"7. Calculate transitive payment.\n"+
					"8. Close Application.");

			System.out.print("Enter your choice: ");

			try {
				int input = Integer.parseInt(scanner.nextLine());
				switch (input) {

					case 1:
						System.out.print("Enter trip name: ");
						String tripName = scanner.nextLine();
						Group  group = new Group(tripName);
						groupManager.registerGroup(group);
						System.out.println("Trip created successfully");
						break;
					case 2:
						System.out.print("Enter trip name: ");
						tripName = scanner.nextLine();
						group = groupManager.getGroupOnName(tripName);

						System.out.print("Enter participant name: ");
						String userName = scanner.nextLine();
						User user = new User(userName);
						group.registerUser(user);
						System.out.print("Participant "+userName+" created");
						break;
					case 3:
						System.out.print("Enter trip name: ");
						tripName = scanner.nextLine();
						System.out.print("Enter category name: ");
						String category = scanner.nextLine();
						group = groupManager.getGroupOnName(tripName);
						group.registerCategory(category);
						System.out.println("Category "+category+" created");
						break;
					case 4:
						System.out.print("Enter trip name: ");
						tripName = scanner.nextLine();
						System.out.print("Enter category name: ");
						category = scanner.nextLine();
						System.out.print("Enter amount: ");
						int amount = Integer.parseInt(scanner.nextLine());
						System.out.print("Expense made by: ");
						userName = scanner.nextLine();

						group = groupManager.getGroupOnName(tripName);
						CreateExpense expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.EQUAL)
								.category(category).finalAmount(amount).expenseShareList(Collections.singletonList(CreateExpense.ExpenseShare.builder()
										.userName(userName).build())).build();
						group.getExpenseProcessor().processExpense(expense);
						System.out.println("INR " + amount + " expense made by " + userName + " for " + tripName + " trip split equally");
						break;
					case 5:
						System.out.print("Enter trip name: ");
						tripName = scanner.nextLine();
						System.out.print("Enter category name: ");
						category = scanner.nextLine();
						System.out.print("Enter amount: ");
						amount = Integer.parseInt(scanner.nextLine());
						System.out.print("Expense made by: ");
						String primaryUserName = scanner.nextLine();

						group = groupManager.getGroupOnName(tripName);

						List<CreateExpense.ExpenseShare> expenseShareList = new ArrayList<>();
						for(User user1 :group.getUsers()) {
							System.out.println(user1.getName()+"'s Percentage of share: ");
							int perc = Integer.parseInt(scanner.nextLine());
							expenseShareList.add(CreateExpense.ExpenseShare.builder().userName(user1.getName()).percentage(perc).build());
						}

						expense = CreateExpense.builder().expenseType(CreateExpense.ExpenseType.UNEQUAL)
								.category(category).finalAmount(amount).expenseShareList(expenseShareList).primaryUserName(primaryUserName).build();
						group.getExpenseProcessor().processExpense(expense);
						System.out.println("INR "+amount+" expense made by "+primaryUserName+" for "+tripName+" trip split unequally");
						break;
					case 6:
						System.out.print("Enter trip name: ");
						tripName = scanner.nextLine();
						group = groupManager.getGroupOnName(tripName);
						System.out.println(group.getExpenseProcessor().getExpenseSummaryForAllUsersInGroup());
						break;
					case 7:
						System.out.print("Enter trip name: ");
						tripName = scanner.nextLine();
						group = groupManager.getGroupOnName(tripName);
						System.out.println(group.getExpenseProcessor().showTransitiveSettlement());
						break;
					default:
						running = false;
						break;
				}
			}
			catch (Exception e) {
				System.out.println("Invalid input. Try Again!!");
			}
		}

		scanner.close();
	}

	public static void main(String[] args) {

		AppStart appStart = new AppStart();
		appStart.menuDisplay();
	}
}
