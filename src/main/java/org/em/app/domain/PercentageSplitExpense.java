package org.em.app.domain;

import lombok.Getter;
import org.em.app.constant.ExpenseConstant;

@Getter
public class PercentageSplitExpense implements IExpense {

	private final String category;

	private final double finalAmount;

	private final int percentage;

	private final User user;

	@Override
	public String getType() {
		return ExpenseConstant.PERCENTAGE_SPLIT;
	}

	public PercentageSplitExpense(User user, String category, double amount, int percentage) {
		this.category = category;
		this.finalAmount = amount;
		this.percentage = percentage;
		this.user = user;
	}
}
