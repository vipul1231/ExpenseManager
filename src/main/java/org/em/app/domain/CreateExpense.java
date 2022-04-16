package org.em.app.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpense {

	private String category;

	private double finalAmount;

	private List<ExpenseShare> expenseShareList;

	private ExpenseType expenseType;

	//Will be used in case of percentage split expense.
	private String primaryUserName;

	@Builder
	@Getter
	public static class ExpenseShare {
		String userName;

		int percentage;
	}

	public enum ExpenseType {
		EQUAL, UNEQUAL
	}

	public CreateExpense(ExpenseType expenseType, List<ExpenseShare> expenseShareList, double finalAmount, String category) {
		this.category = category.toLowerCase();
		this.finalAmount = finalAmount;
		this.expenseShareList = expenseShareList;
		this.expenseType = expenseType;
	}
}
