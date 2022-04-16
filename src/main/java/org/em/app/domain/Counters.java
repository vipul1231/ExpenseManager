package org.em.app.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
public class Counters {

	public CounterType type;

	public AtomicLong value;

	public enum CounterType {
		TOTAL, COUNT
	}

	public Counters(CounterType counterType) {
		value = new AtomicLong(0);
		type = counterType;
	}

	public void addValue(long val) {
		value.getAndAdd(val);
	}

	public long getValue() {
		return value.get();
	}
}
