package io.jenetics.incubator.csv;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;

@FunctionalInterface
public interface RowFormatter<T> {

	Row format(T value);


	static <T extends Record> RowFormatter<T>
	record(final Class<T> type) {
		final var components = type.getRecordComponents();

		return record -> {
			final Object[] values = new String[components.length];
			for (int i = 0; i < components.length; ++i) {
				values[i] = get(components[i], record);
			}
			return Row.of(values);
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
