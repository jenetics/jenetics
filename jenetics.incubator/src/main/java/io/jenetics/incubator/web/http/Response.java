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
package io.jenetics.incubator.web.http;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * HTTP response.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public sealed interface Response<T> {

	/**
	 * Return the original resource object. The type of the original resource
	 * is not known, since the response type might have been changed by one
	 * of the mapping methods.
	 *
	 * @return the original resource object
	 */
	Request<T> request();

	/**
	 * The HTTP status code of the response.
	 *
	 * @return the HTTP status code
	 */
	int status();

	/**
	 * The success response object.
	 *
	 * @param request the original request object
	 * @param headers the response headers
	 * @param status the response status
	 * @param body the response body
	 * @param <T> the body type
	 */
	record Success<T>(
		Request<T> request,
		Headers headers,
		int status,
		T body
	)
		implements Response<T>
	{
		public Success {
			requireNonNull(request);
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
	sealed interface Failure<T> extends Response<T> { }

	/**
	 * A client error, caused by an exception while accessing the server.
	 *
	 * @param request the original resource object
	 * @param error the thrown exception
	 * @param <T> the body type
	 */
	record ClientError<T>(Request<T> request, Throwable error)
		implements Failure<T>
	{
		public ClientError {
			requireNonNull(request);
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
	 * @param request the original request object
	 * @param headers the response headers
	 * @param status the response status
	 * @param detail the detailed error detail, if available
	 * @param <T> the body type
	 */
	record ServerError<T>(
		Request<T> request,
		Headers headers,
		int status,
		String detail
	)
		implements Failure<T>
	{
		public ServerError {
			requireNonNull(request);
			requireNonNull(headers);
		}
	}

	/**
	 * Convert the given server {@code response} into a {@link Response} object.
	 * <br>
	 * <b>Response mapping</b>
	 * <ul>
	 *     <li>{@link ServerResult.OK} -> {@link Success}</li>
	 *     <li>{@link ServerResult.NOK} -> {@link ServerError}</li>
	 * </ul>
	 *
	 * @param response the server response
	 * @return the converted response object
	 * @param <T> the response body type
	 */
	static <T> Response<T> of(final ServerResult<? extends T> response) {
		requireNonNull(response);

		@SuppressWarnings("unchecked")
		final var resp = (ServerResult<T>)response;

		return switch (resp) {
			case ServerResult.OK<T> ok -> new Success<>(
				ok.request(),
				ok.headers(),
				ok.status(),
				ok.body()
			);
			case ServerResult.NOK<T> nok -> new ServerError<>(
				nok.request(),
				nok.headers(),
				nok.status(),
				nok.detail()
			);
		};
	}

	/**
	 * Convert the given server {@code response} into a {@link Response} object.
	 * If the {@code error} is not {@code null}, a {@link ClientError} is
	 * returned.
	 * <br>
	 * <b>Response mapping</b>
	 * <ul>
	 *     <li>{@link ServerResult.OK} -> {@link Success}</li>
	 *     <li>{@link ServerResult.NOK} -> {@link ServerError}</li>
	 *     <li>{@link Throwable} -> {@link ClientError}</li>
	 * </ul>
	 *
	 * @param request the original request
	 * @param response the server response
	 * @param error the exception thrown on the client side, or {@code null}
	 * @return the converted response object
	 * @param <T> the response body type
	 */
	static <T> Response<T> of(
		final Request<? extends T> request,
		final ServerResult<? extends T> response,
		final Throwable error
	) {
		@SuppressWarnings("unchecked")
		final var req = (Request<T>)request;

		return switch (error) {
			case UncheckedIOException e -> new ClientError<T>(
				req,
				e.getCause()
			);
			case Throwable e -> new ClientError<>(req, e);
			case null -> Response.of(response);
		};
	}

}
