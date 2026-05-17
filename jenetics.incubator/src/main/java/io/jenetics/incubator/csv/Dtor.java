package io.jenetics.incubator.csv;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;

@FunctionalInterface
public interface Dtor<T> {

	Object[] unapply(T value);

	default String[] format(T value, StringFormat format) {
		return format.format(unapply(value));
	}

	default String[] format(T value) {
		return format(value, StringFormats.DEFAULT);
	}

	static <T extends Record> Dtor<T> record(final Class<T> type) {
		final var components = type.getRecordComponents();

		return record -> {
			final Object[] values = new Object[components.length];
			for (int i = 0; i < components.length; ++i) {
				values[i] = get(components[i], record);
			}
			return values;
		};
	}

	private static Object get(RecordComponent component, Object record) {
		try {
			return component.getAccessor().invoke(record);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}

}
