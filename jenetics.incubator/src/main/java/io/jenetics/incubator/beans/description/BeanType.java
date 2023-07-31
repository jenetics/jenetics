package io.jenetics.incubator.beans.description;

import java.lang.reflect.Type;

public record BeanType(Class<?> type) {

	public static Object of(final Type type) {
		if (type instanceof Class<?> cls) {
			return new BeanType(cls);
		} else {
			return null;
		}
	}

}
