package org.em.app.manager;

import lombok.Getter;
import org.em.app.domain.Group;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GroupManager {

	private final Map<String, Group> groupMap;

	public GroupManager() {
		groupMap = new HashMap<>();
	}

	public void registerGroup(Group group) {
		groupMap.put(group.getGroupName(), group);
	}

	public Group getGroupOnName(String name) {
		return groupMap.get(name);
	}
}
