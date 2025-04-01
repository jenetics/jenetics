package io.jenetics.incubator.restfulclient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpHeaders;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public sealed interface Response<T> {
	Resource<?> resource();

	record Success<T>(
		Resource<?> resource,
		HttpHeaders headers,
		int status,
		T body
	) implements Response<T> { }

	sealed interface Failure<T> extends Response<T> { }

	record ClientError<T>(Resource<?> resource, Throwable error)
		implements Failure<T>
	{
		public ClientError {
			requireNonNull(resource);
			requireNonNull(error);
		}

		public int statusCode() {
			return switch (error) {
				case IOException e -> 503;
				case UncheckedIOException e -> 503;
				default -> 500;
			};
		}
	}

	record ServerError<T>(
		Resource<?> resource,
		HttpHeaders headers,
		int status,
		ProblemDetail detail
	) implements Failure<T> { }

	@SuppressWarnings("unchecked")
	default <A> Response<A> map(final Function<? super T, ? extends A> fn) {
		return switch (this) {
			case Success(var s, var h, var r, var b) -> new Success<>(s, h, r, fn.apply(b));
			case Response.Failure<T> f -> (Failure<A>)f;
		};
	}

	@SuppressWarnings("unchecked")
	default  <A> Response<A> flatMap(final Function<? super T, Response<? extends A>> fn) {
		return (Response<A>)switch (this) {
			case Response.Success<T> s -> fn.apply(s.body());
			case Response.Failure<T> f -> f;
		};
	}

}
