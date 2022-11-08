package io.jenetics.incubator.property;

import java.util.List;

public record DataClass(Class<?> type) {

	List<Property> properties() {
		if (type == null) {
			return List.of();
		}
		return null;
	}

}
