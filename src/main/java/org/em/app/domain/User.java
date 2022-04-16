package org.em.app.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class User {

	private final String name;

	public User(String name) {
		this.name = name;
	}
}
