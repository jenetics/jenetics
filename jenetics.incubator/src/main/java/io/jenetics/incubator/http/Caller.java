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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

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
public interface Caller<T, C> {

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
	interface Sync<T> extends Caller<T, Response<T>> {

		/**
		 * Calls the endpoint with the given {@code request}. The method call is
		 * synchronous and doesn't throw an exception.
		 *
		 * @apiNote
		 * All errors, server side and client side, are returned via the
		 * {@link Response} object.
		 *
		 * @param request the request
		 * @return the response fo the request
		 */
		@Override
		Response<T> call(Request<? extends T> request);

		/**
		 * Return a new synchronous endpoint.
		 *
		 * @param client the client implementation used for calling the endpoint
		 * @return a new synchronous endpoint
		 * @param <T> the endpoint result type
		 */
		static <T> Sync<T> of(Client client) {
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
					.send(request.uri(), request)
					.handle((value, error) -> Response.of(request, value, error));

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
		extends Caller<T, CompletableFuture<Response<T>>>
	{

		/**
		 * Calls the endpoint with the given {@code request}. The method call is
		 * asynchronous and doesn't throw an exception.
		 *
		 * @apiNote
		 * All errors, server side and client side, are returned via the
		 * {@link Response} object.
		 *
		 * @param request the request
		 * @return the response fo the request
		 */
		@Override
		CompletableFuture<Response<T>> call(Request<? extends T> request);

		/**
		 * Return a new asynchronous endpoint.
		 *
		 * @param client the client implementation used for calling the endpoint
		 * @return a new asynchronous endpoint
		 * @param <T> the endpoint result type
		 */
		static <T> Async<T> of(Client client) {
			return request -> client
				.send(request.uri(), request)
				.handle((value, error) -> Response.of(request, value, error));
		}
	}

	/**
	 * Caller specialization for reactive HTTP calls.
	 *
	 * @param <T> the result type
	 */
	interface Reactive<T>
		extends Caller<T, Flow.Publisher<Response<T>>>
	{

		/**
		 * Calls the endpoint with the given {@code request}. The method call is
		 * reactive and doesn't throw an exception.
		 *
		 * @apiNote
		 * All errors, server side and client side, are returned via the
		 * {@link Response} object.
		 *
		 * @param request the request
		 * @return the response fo the request
		 */
		@Override
		Flow.Publisher<Response<T>> call(Request<? extends T> request);

		/**
		 * Return a new reactive endpoint.
		 *
		 * @param client the client implementation used for calling the endpoint
		 * @return a new reactive endpoint
		 * @param <T> the endpoint result type
		 */
		static <T> Reactive<T> of(Client client) {
			return request -> {
				var publisher = new SubmissionPublisher<Response<T>>(
					Runnable::run,
					1
				);

				client.<T>send(request.uri(), request)
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

}
