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
package io.jenetics.incubator.http;

import java.io.UncheckedIOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.BiFunction;

import io.jenetics.incubator.http.Response.ClientError;

/**
 * This interface is responsible for calling the given {@code resource} and
 * return a result object. This interface is not meant to be implemented directly.
 * The usual <em>implementation</em> will be a method reference from a client
 * implementation.
 *
 * @apiNote
 * The {@link #call(Request)} method is not meant to throw any exception. Error
 * results <b>must</b> be reported via the {@link Response} type.
 *
 * @param <T> the <em>main</em> result type of the {@code resource}
 * @param <C> the result type returned by the caller
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
@FunctionalInterface
public interface Endpoint<T, C> {

	/**
	 * Calls the given {@code resource} and returns its result.
	 *
	 * @param request the resource
	 * @return the call result
	 */
	C call(Request<? extends T> request);

	/**
	 * Caller specialization for synchronous HTTP calls.
	 *
	 * @param <T> the result type
	 */
	interface Sync<T> extends Endpoint<T, Response<T>> {

		/**
		 * Return a new synchronous endpoint.
		 *
		 * @param uri the endpoint URI
		 * @param client the client implementation used for calling the endpoint
		 * @return a new synchronous endpoint
		 * @param <T> the endpoint result type
		 */
		static <T> Sync<T> of(URI uri, Client client) {
			return request -> {
				@SuppressWarnings("unchecked")
				final var req = (Request<T>)request;

				if (Thread.currentThread().isInterrupted()) {
					return new ClientError<>(
						req,
						new InterruptedException()
					);
				}

				final var result = client
					.send(uri, request)
					.handle(toResponse(request));

				try {
					return result.get();
				} catch (InterruptedException e) {
					result.cancel(true);
					Thread.currentThread().interrupt();
					return new ClientError<>(req, e);
				} catch (ExecutionException e) {
					return new ClientError<>(req, e.getCause());
				} catch (Exception e) {
					return new ClientError<>(req, e);
				}
			};
		}
	}

	/**
	 * Caller specialization for asynchronous HTTP calls.
	 *
	 * @param <T> the result type
	 */
	interface Async<T>
		extends Endpoint<T, CompletableFuture<Response<T>>>
	{

		/**
		 * Return a new asynchronous endpoint.
		 *
		 * @param uri the endpoint URI
		 * @param client the client implementation used for calling the endpoint
		 * @return a new asynchronous endpoint
		 * @param <T> the endpoint result type
		 */
		static <T> Async<T> of(URI uri, Client client) {
			return request -> client
				.send(uri, request)
				.handle(toResponse(request));
		}
	}


	static <T> BiFunction<ServerResponse<? extends T>, Throwable, Response<T>>
	toResponse(Request<? extends T> request) {
		@SuppressWarnings("unchecked")
		final var req = (Request<T>)request;

		return (value, error) -> switch (error) {
			case UncheckedIOException e -> new ClientError<T>(
				req,
				e.getCause()
			);
			case Throwable e -> new ClientError<>(req, e);
			case null -> Response.of(value);
		};
	}


	/**
	 * Caller specialization for reactive HTTP calls.
	 *
	 * @param <T> the result type
	 */
	interface Reactive<T>
		extends Endpoint<T, Flow.Publisher<Response<T>>>
	{

		/**
		 * Return a new reactive endpoint.
		 *
		 * @param uri the endpoint URI
		 * @param client the client implementation used for calling the endpoint
		 * @return a new reactive endpoint
		 * @param <T> the endpoint result type
		 */
		static <T> Reactive<T> of(URI uri, Client client) {
			return request -> {
				var publisher = new SubmissionPublisher<Response<T>>(
					Runnable::run,
					1
				);

				client.<T>send(uri, request)
					.whenComplete((response, error) -> {
						if (error != null) {
							publisher.closeExceptionally(error);
						} else {
							publisher.submit(Response.of(response));
							publisher.close();
						}
					});

				return publisher;
			};
		}
	}



//	private static <T> Client.Response<T> toResult(
//		final Request<? extends T> request,
//		final Client.Response<T> response,
//		final HttpResponse<Client.Response<T>> result
//	) {
//		return switch (response) {
//			case Client.Response.OK(var body) -> new Client.Response.Success<>(
//				request,
//				new Headers(result.headers().map()),
//				result.statusCode(),
//				request.type().cast(body)
//			);
//			case Client.Response.NOK(var detail) -> new Client.Response.ServerError<>(
//				request,
//				new Headers(result.headers().map()),
//				result.statusCode(),
//				detail
//			);
//		};
//	}

}
