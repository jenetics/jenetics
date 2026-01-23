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

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
final class DefaultClient implements Client {

	private final HttpClient client;
	private final ResponseBodyReader reader;
	private final RequestBodyWriter writer;

	public DefaultClient(
		final HttpClient client,
		final ResponseBodyReader reader,
		final RequestBodyWriter writer
	) {
		this.client = requireNonNull(client);
		this.reader = requireNonNull(reader);
		this.writer = requireNonNull(writer);
	}

	/**
	 * Calls the given {@code resource} and returns its result.
	 *
	 *
	 * @param request the request object
	 * @return the call response
	 * @param <T> the response body type
	 * @throws NullPointerException if the given {@code resource} is {@code null}
	 */
	@Override
	public <T> CompletableFuture<ServerResponse<T>>
	send(URI uri, Request<? extends T> request) {
		final CompletableFuture<HttpResponse<ServerResponse<T>>> response =
			client.sendAsync(
				toHttpRequest(uri, request),
				new ServerBodyHandler<T>(request, reader, request.type())
			);

		return response
			.thenCompose(result -> completedFuture(result.body())
				/*
				switch (result.body()) {
					case Response.OK<T> ok -> completedFuture(ok);
					case Response.NOK<T> nok -> {
						final var exception = new ResultException(nok);
						yield failedFuture(exception);
					}
				}
				 */
			);
		/*
			.exceptionallyCompose(throwable -> {
				final var error = new Response.ClientError<>(
					request,
					switch (throwable) {
						case UncheckedIOException e -> e.getCause();
						case Throwable e -> e;
					}
				);
				final var exception = new ResultException(error);
				return failedFuture(exception);
			});

		 */
	}

	private <T> HttpRequest
	toHttpRequest(final URI uri, final Request<? extends T> request) {
		final var builder = HttpRequest.newBuilder().uri(uri);
		request.headers().addTo(builder);

		switch (request) {
			case Request.GET<?> _ -> builder.GET();
			case Request.POST<?> req -> builder.POST(new ClientBodyPublisher(
				writer,
				req.body().orElse(null)
			));
			case Request.PUT<?> req -> builder.PUT(new ClientBodyPublisher(
				writer,
				req.body().orElse(null)
			));
			case Request.DELETE<?> _ -> builder.DELETE();
		}

		return builder.build();
	}

	@Override
	public void close() {
		client.close();
	}


	private static final class ServerBodyHandler<T>
		implements HttpResponse.BodyHandler<ServerResponse<T>>
	{
		private final Request<T> request;
		private final ResponseBodyReader reader;
		private final Class<T> type;

		@SuppressWarnings("unchecked")
		private ServerBodyHandler(
			final Request<? extends T> request,
			final ResponseBodyReader reader,
			final Class<? extends T> type
		) {
			this.request = (Request<T>)requireNonNull(request);
			this.reader = requireNonNull(reader);
			this.type = (Class<T>)requireNonNull(type);
		}

		@Override
		public HttpResponse.BodySubscriber<ServerResponse<T>>
		apply(final HttpResponse.ResponseInfo info) {
			return switch (info.statusCode()) {
				case 200, 201, 202, 203, 204 -> subscriber(
					reader,
					type,
					body -> new ServerResponse.OK<>(
						request,
						Headers.of(info.headers()),
						info.statusCode(),
						body
					)
				);
				default -> subscriber(
					reader,
					String.class,
					body -> new ServerResponse.NOK<>(
						request,
						Headers.of(info.headers()),
						info.statusCode(),
						body
					)
				);
			};
		}

		private static <A, B> HttpResponse.BodySubscriber<B> subscriber(
			final ResponseBodyReader reader,
			final Class<A> type,
			final Function<? super A, ? extends B> fn
		) {
			requireNonNull(type);

			return HttpResponse.BodySubscribers.mapping(
				HttpResponse.BodySubscribers.ofInputStream(),
				in -> {
					try {
						return fn.apply(reader.read(in, type));
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			);
		}

	}

}
