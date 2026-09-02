package io.jenetics.incubator.util.function;

import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

public interface MapType<A, B/*<A>*/, C, D/*<C>*/> {

	D map(B value, Function<? super A, ? extends C> fn);

	final class ValueMap<A, B> implements MapType<A, A, B, B> {
		@Override
		public B map(A value, Function<? super A, ? extends B> fn) {
			return fn.apply(value);
		}
	}

	final class OptionalMap<A, B>
		implements MapType<A, Optional<? extends A>, B, Optional<B>>
	{
		@Override
		public Optional<B>
		map(Optional<? extends A> value, Function<? super A, ? extends B> fn) {
			return value.map(fn);
		}
	}

	final class MonoMap<A, B> implements MapType<A, Mono<A>, B, Mono<B>> {
		@Override
		public Mono<B> map(Mono<A> value, Function<? super A, ? extends B> fn) {
			return value.map(fn);
		}
	}

}
