package io.jenetics.incubator.util.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FunctionBuilder<T, R> {

	@SuppressWarnings("rawtypes")
	private final List<Function> steps;

	@SuppressWarnings("rawtypes")
	private FunctionBuilder(List<Function> steps) {
		this.steps = List.copyOf(steps);
	}

	public <A> FunctionBuilder<T, A> then(Function<? super R, ? extends A> fn) {
		final var steps = new ArrayList<>(this.steps);
		steps.add(fn);
		return new FunctionBuilder<>(steps);
	}

	@SuppressWarnings("unchecked")
	public Function<T, R> build() {
		return steps.stream()
			.reduce((a, b) -> a.andThen(b))
			.orElse(a -> (R)a);
	}

	public static <T> FunctionBuilder<T, T> of() {
		return new FunctionBuilder<>(List.of());
	}

}
