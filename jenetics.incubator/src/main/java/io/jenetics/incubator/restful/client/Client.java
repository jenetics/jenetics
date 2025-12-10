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
package io.jenetics.incubator.restful.client;

import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SubmissionPublisher;

import io.jenetics.incubator.restful.Caller;
import io.jenetics.incubator.restful.Resource;
import io.jenetics.incubator.restful.Response;
import io.jenetics.incubator.restful.ResponseException;

/**
 * Rest client interface. Clients are inherently asynchronous.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public interface Client {

	/**
	 * Calls the given {@code resource} and returns its result.
	 *
	 * @param resource the resource to call
	 * @return the call response
	 * @param <T> the response body type
	 * @throws NullPointerException if the given {@code resource} is {@code null}
	 */
	<T> CompletableFuture<Response.Success<T>> call(Resource<? extends T> resource);

	/**
	 * Return asynchronous caller.
	 *
	 * @return asynchronous caller
	 * @param <T> the response body type
	 */
	default <T> Caller.Async<T> async() {
		return this::call;
	}

	/**
	 * Return a synchronous caller, which is build upon the asynchronous caller
	 * returned by the {@link #async()} method.
	 *
	 * @return a synchronous caller
	 * @param <T> the response body type
	 */
	default <T> Caller.Sync<T> sync() {
		return resource -> {
			if (Thread.currentThread().isInterrupted()) {
				return new Response.ClientError<>(resource, new InterruptedException());
			}

			@SuppressWarnings("unchecked")
			final var result = this.call(resource)
				.handle((value, error) ->
					(Response<T>)switch (error) {
						case ResponseException e -> e.failure();
						case UncheckedIOException e -> new Response.ClientError<>(
							resource,
							e.getCause()
						);
						case Throwable e -> new Response.ClientError<>(resource, e);
						case null -> value;
					}
				);

			try {
				return result.get();
			} catch (InterruptedException e) {
				result.cancel(true);
				Thread.currentThread().interrupt();
				return new Response.ClientError<>(resource, e);
			} catch (ExecutionException e) {
				return new Response.ClientError<>(resource, e.getCause());
			} catch (Exception e) {
				return new Response.ClientError<>(resource, e);
			}
		};
	}

	/**
	 * Return a reactive ({@link java.util.concurrent.Flow.Publisher}) caller,
	 * which is build upon the asynchronous caller returned by the {@link #async()}
	 * method.
	 *
	 * @return a reactive caller
	 * @param <T> the response body type
	 */
	default <T> Caller.Reactive<T> reactive() {
		return resource -> {
			var publisher = new SubmissionPublisher<Response.Success<T>>(
				Runnable::run,
				1
			);

			this.<T>call(resource)
				.whenComplete((response, error) -> {
					if (error != null) {
						publisher.closeExceptionally(error);
					} else {
						publisher.submit(response);
						publisher.close();
					}
				});

			return publisher;
		};
	}

}
