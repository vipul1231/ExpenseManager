package org.em.app.domain;

import lombok.Getter;
import lombok.Setter;
import org.em.app.constant.ExpenseConstant;
import org.em.app.processor.GroupExpenseProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class Group {

	private final String groupName;

	private List<User> users;

	private Set<String> category;

	private GroupExpenseProcessor expenseProcessor;

	private Map<String, Counters> groupGlobalCounters;

	enum PreDefinedCategory {
		FOOD("food"), ACTIVITY("activity"), TRIP("trip"), MISCELLANEOUS("misc");

		final String code;

		PreDefinedCategory(String code) {
			this.code = code;
		}
	}

	public Group(String groupName) {
		this.groupName = groupName;
		users = new ArrayList<>();
		category = new HashSet<>();
		expenseProcessor = new GroupExpenseProcessor(this);
		for (PreDefinedCategory preDefinedCategory : PreDefinedCategory.values()) {
			this.category.add(preDefinedCategory.code.toLowerCase());
		}
		groupGlobalCounters = new ConcurrentHashMap<>();
		//Register Counter
		registerCounter(ExpenseConstant.TOTAL_EXPENSE, new Counters(Counters.CounterType.TOTAL));
	}

	public void registerUser(User user) {
		users.add(user);
		expenseProcessor.createExpenseListForUser(user);
		registerCounter(ExpenseConstant.USER+user.getName(), new Counters(Counters.CounterType.TOTAL));
		registerCounter(ExpenseConstant.USER_OWES +user.getName(), new Counters(Counters.CounterType.TOTAL));
		registerCounter(ExpenseConstant.USER_OWED +user.getName(), new Counters(Counters.CounterType.TOTAL));
	}

	public void registerCategory(String categoryName){
		category.add(categoryName.toLowerCase());
		registerCounter(ExpenseConstant.CATEGORY+categoryName.toLowerCase(), new Counters(Counters.CounterType.TOTAL));
	}

	public void registerCounter(String name, Counters counters) {
		groupGlobalCounters.put(name, counters);
	}

	public String getCategory(String category) {
		if (!this.category.contains(category.toLowerCase())) {
			registerCategory(category);
		}
		return category;
	}

	public Optional<User> getUser(String userName) {
		return users.stream().filter(i -> i.getName().equals(userName)).findFirst();
	}

	public int getTotalUsersInGroup() {
		return users.size();
	}

	public List<User> getListOfUserExcludingGivenUser(String user) {
		return users.stream().filter(i -> !i.getName().equals(user)).collect(Collectors.toList());
	}

	public void addValueToCounter(String name, long value) {
		Counters counters = groupGlobalCounters.get(name);
		counters.addValue(value);
	}

	public long getValueOfCounter(String name) {
		return groupGlobalCounters.get(name).getValue();
	}

	public boolean isCounterPresent(String name) {
		return groupGlobalCounters.containsKey(name);
	}
}
