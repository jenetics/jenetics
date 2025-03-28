package io.jenetics.incubator.restfulclient;

import java.net.http.HttpHeaders;
import java.util.function.Function;

public sealed interface Response<T> {
	int status();
	HttpHeaders headers();
	Resource<?> resource();

	record Success<T>(
		int status,
		HttpHeaders headers,
		Resource<?> resource,
		T body
	) implements Response<T> { }

	record Failure<T>(
		int status,
		HttpHeaders headers,
		Resource<?> resource,
		ErrorInfo error
	) implements Response<T> { }


	@SuppressWarnings("unchecked")
	default <A> Response<A> map(final Function<? super T, ? extends A> fn) {
		return switch (this) {
			case Success<T> s -> new Success<>(
				s.status(),
				s.headers(),
				s.resource(),
				fn.apply(s.body())
			);
			case Failure<T> f -> (Failure<A>)f;
		};
	}

	@SuppressWarnings("unchecked")
	default  <A> Response<A> flatMap(final Function<? super T, Response<? extends A>> fn) {
		return (Response<A>)switch (this) {
			case Success<T> s -> fn.apply(s.body());
			case Failure<T> f -> f;
		};
	}

}
