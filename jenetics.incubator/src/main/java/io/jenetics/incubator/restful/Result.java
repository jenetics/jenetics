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
package io.jenetics.incubator.restful;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.net.http.HttpHeaders;
import java.util.function.Function;

/**
 * Represents the resource response.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public sealed interface Result<T> extends Serializable {

	/**
	 * Return the original resource object. The type of the original resource
	 * is not known, since the response type might have been changed by one
	 * of the mapping methods.
	 *
	 * @return the original resource object
	 */
	Resource<?> resource();

	/**
	 * The HTTP status code of the response.
	 *
	 * @return the HTTP status code
	 */
	int status();

	/**
	 * The success response object.
	 *
	 * @param resource the original resource object
	 * @param headers the response headers
	 * @param status the response status
	 * @param body the response body
	 * @param <T> the body type
	 */
	record Success<T>(
		Resource<?> resource,
		HttpHeaders headers,
		int status,
		T body
	)
		implements Result<T>
	{
		public Success {
			requireNonNull(resource);
			requireNonNull(headers);
		}
	}

	/**
	 * The failure response. An error might be caused by the client, e.g., an
	 * {@link IOException} thrown while calling the resource. This will lead to
	 * a {@link ClientError}. If the service returns a status code greater than
	 * 200, a {@link ServerError} is created.
	 *
	 * @param <T> the body type
	 */
	sealed interface Failure<T> extends Result<T> { }

	/**
	 * A client error, caused by an exception while accessing the server.
	 *
	 * @param resource the original resource object
	 * @param error the thrown exception
	 * @param <T> the body type
	 */
	record ClientError<T>(Resource<?> resource, Throwable error)
		implements Failure<T>
	{
		public ClientError {
			requireNonNull(resource);
			requireNonNull(error);
		}

		/**
		 * Standard mapping of the error class to a status code.
		 *
		 * @return the default status code associated with the exception type
		 */
		@Override
		public int status() {
			return switch (error) {
				case IOException e -> 503;
				case UncheckedIOException e -> 503;
				default -> 500;
			};
		}
	}

	/**
	 * Error returned by the server.
	 *
	 * @param resource the original resource object
	 * @param headers the response headers
	 * @param status the response status
	 * @param detail the detailed error detail, if available
	 * @param <T> the body type
	 */
	record ServerError<T>(
		Resource<?> resource,
		HttpHeaders headers,
		int status,
		ProblemDetail detail
	)
		implements Failure<T>
	{
		public ServerError {
			requireNonNull(resource);
			requireNonNull(headers);
		}
	}

	/**
	 * Applies the mapping function to the (success) response object. Errors are
	 * returned unchanged.
	 *
	 * @param fn the mapping function
	 * @return the mapped response object
	 * @param <A> the new response body type
	 */
	@SuppressWarnings("unchecked")
	default <A> Result<A> map(final Function<? super T, ? extends A> fn) {
		requireNonNull(fn);

		return switch (this) {
			case Success(var s, var h, var r, var b) -> new Success<>(s, h, r, fn.apply(b));
			case Result.Failure<T> f -> (Failure<A>)f;
		};
	}

	/**
	 * Applies the mapping function to the (success) response object. Errors are
	 * returned unchanged.
	 *
	 * @param fn the mapping function
	 * @return the mapped response object
	 * @param <A> the new response body type
	 */
	@SuppressWarnings("unchecked")
	default  <A> Result<A> flatMap(final Function<? super T, Result<? extends A>> fn) {
		requireNonNull(fn);

		return (Result<A>)switch (this) {
			case Result.Success<T> s -> fn.apply(s.body());
			case Result.Failure<T> f -> f;
		};
	}

}
