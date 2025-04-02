/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.restfulclient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpHeaders;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
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
