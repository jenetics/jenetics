package io.jenetics.incubator.beans.description;

import java.lang.reflect.Type;

public record RecordType(Class<?> type) {

	public static Object of(final Type type) {
		if (type instanceof Class<?> cls && cls.isRecord()) {
			return new RecordType(cls);
		} else {
			return null;
		}
	}

}
