package io.jenetics.incubator.csv;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FunctionalInterface
public interface RowParser<T> {

	/**
	 * Converts a CSV row to an object.
	 *
	 * @param row the CSV row
	 * @return the stored data object
	 */
	T parse(final Row row);

	default <U> RowParser<U> map(final Function<? super T, ? extends U> fn) {
		requireNonNull(fn);
		return row -> fn.apply(parse(row));
	}

	default <U> RowParser<U>
	flatMap(final Function<? super T, ? extends RowParser<? extends U>> fn) {
		requireNonNull(fn);
		return row -> fn.apply(parse(row)).parse(row);
	}

	default <U, A> RowsParser<U> collect(Collector<? super T, A, U> collector) {
		requireNonNull(collector);
		return rows -> {
			try (rows) {
				return rows.map(this::parse).collect(collector);
			}
		};
	}

	default RowsParser<List<T>> list() {
		return rows -> {
			try (rows) {
				return rows.map(this::parse).toList();
			}
		};
	}

	default RowsParser<Set<T>> set() {
		return collect(Collectors.toUnmodifiableSet());
	}

	default RowsParser<Stream<T>> stream() {
		return rows -> rows.map(this::parse);
	}

	static <T extends Record> RowParser<T> record(final Class<T> type) {
		final RecordComponent[] components = type.getRecordComponents();
		final Constructor<T> ctor = ctor(type);

		return row -> {
			final int length = Math.min(components.length, row.size());
			final Object[] values = new Object[components.length];
			for (int i = 0; i < length; ++i) {
				values[i] = row.at(i, components[i].getType());
			}
			return create(ctor, values);
		};
	}

	private static <T extends Record> Constructor<T> ctor(final Class<T> type) {
		final Class<?>[] columnTypes = Stream.of(type.getRecordComponents())
			.map(RecordComponent::getType)
			.toArray(Class<?>[]::new);

		try {
			return type.getDeclaredConstructor(columnTypes);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
				"Canonical record constructor must be available.", e
			);
		}
	}

	private static <T> T create(final Constructor<T> ctor, final Object[] args) {
		try {
			return ctor.newInstance(args);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException rte) {
				throw rte;
			} else if (e.getCause() instanceof Error error) {
				throw error;
			} else {
				throw new RuntimeException(e.getCause());
			}
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
