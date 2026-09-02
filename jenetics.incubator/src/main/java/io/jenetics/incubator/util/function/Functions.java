package io.jenetics.incubator.util.function;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Functions {
	private Functions() {
	}

	public static <T> Function<T, T> run(Runnable task) {
		return arg -> {
			task.run();
			return arg;
		};
	}

	public static <T, R> Function<? super List<? extends T>, List<R>>
	parallelize(Function<? super T, ? extends R> fn) {
		record IndexedValue<T>(int index, T value) {}

		return args -> IntStream.range(0, args.size())
			.mapToObj(i -> new IndexedValue<>(i, args.get(i)))
			.parallel()
			.map(v -> new IndexedValue<>(v.index, fn.apply(v.value)))
			.sorted(Comparator.comparing(IndexedValue::index))
			.map(IndexedValue::value)
			.collect(Collectors.toUnmodifiableList());
	}

}
