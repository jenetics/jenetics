package io.jenetics.incubator.util.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FunctionBuilderBase<A, B/*<A>*/, C, D/*<C>*/> {

	@SuppressWarnings("rawtypes")
	private final List<Function> steps;

	@SuppressWarnings("rawtypes")
	public FunctionBuilderBase(List<Function> steps) {
		this.steps = List.copyOf(steps);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public <E, F> FunctionBuilderBase<A, B, E, F>
	then(Function<? super C, ? extends E> fn, MapType<C, D, E, F> map) {
		final var steps = new ArrayList<>(this.steps);
		steps.add(arg -> ((MapType)map).map(arg, fn));
		return new FunctionBuilderBase<>(steps);
	}

	//protected abstract <A, B, C, D> MapType<A, B, C, D> type();
	//protected abstract <A, B, C, D> FunctionBuilderBase<A, B, C, D>
	//create(List<Function> steps);

	@SuppressWarnings("unchecked")
	public Function<B, D> build() {
		return steps.stream()
			.reduce((a, b) -> a.andThen(b))
			.orElse(a -> (D)a);
	}


}

class FnBuilder<A, B> extends FunctionBuilderBase<A, A, B, B> {

	@SuppressWarnings("rawtypes")
	private FnBuilder(List<Function> steps) {
		super(steps);
	}

//	@Override
//	protected <A, B> MapType<A, A, B, B> type() {
//		return new ValueMap<>();
//	}
//
//	@Override
//	protected <A1, B1, C, D> FunctionBuilderBase<A1, B1, C, D>
//	create(List<Function> steps) {
//		return new FnBuilder(steps);
//	}

	public static <T> FnBuilder<T, T> of() {
		return new FnBuilder<>(List.of());
	}
}

