package org.em.app.domain;

import lombok.Getter;
import org.em.app.constant.ExpenseConstant;

@Getter
public class EqualSplitExpense implements IExpense{

	private final String category;

	private final double finalAmount;

	private final User user;

	public EqualSplitExpense(User user, String category, double finalAmount) {
		this.category = category;
		this.user = user;
		this.finalAmount = finalAmount;
	}

	@Override
	public String getType() {
		return ExpenseConstant.EQUAL_SPLIT;
	}
}
