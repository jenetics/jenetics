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
 * @since !__version__!
 * @version !__version__!
 */
final class DefaultClient implements Client {

	private final URI base;
	private final HttpClient client;
	private final BodyMarshaling marshaling;

	public DefaultClient(
		final URI base,
		final HttpClient client,
		final BodyMarshaling marshaling
	) {
		this.base = base;
		this.client = requireNonNull(client);
		this.marshaling = requireNonNull(marshaling);

		if (base != null && !base.isAbsolute()) {
			throw new IllegalArgumentException(
				"Base URI must be absolute: " + base
			);
		}
	}

	@Override
	public <T> CompletableFuture<ServerResult<T>>
	send(Request<? extends T> request) {
		final CompletableFuture<HttpResponse<ServerResult<T>>> response =
			client.sendAsync(
				toHttpRequest(request),
				new ServerBodyHandler<T>(request, marshaling, request.type())
			);

		return response.thenCompose(result -> completedFuture(result.body()));
	}

	private <T> HttpRequest toHttpRequest(final Request<? extends T> request) {
		final var uri = base != null
			? base.resolve(request.uri())
			: request.uri();

		final var builder = HttpRequest.newBuilder().uri(uri);
		request.headers().addTo(builder);

		switch (request) {
			case Request.GET<?> _ -> builder.GET();
			case Request.POST<?> req -> builder.POST(new ClientBodyPublisher(
				marshaling,
				req.body().orElse(null)
			));
			case Request.PUT<?> req -> builder.PUT(new ClientBodyPublisher(
				marshaling,
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
		implements HttpResponse.BodyHandler<ServerResult<T>>
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
		public HttpResponse.BodySubscriber<ServerResult<T>>
		apply(final HttpResponse.ResponseInfo info) {
			return switch (info.statusCode()) {
				case 200, 201, 202, 203, 204 -> subscriber(
					reader,
					type,
					body -> new ServerResult.OK<>(
						request,
						Headers.of(info.headers()),
						info.statusCode(),
						body
					)
				);
				default -> subscriber(
					reader,
					String.class,
					body -> new ServerResult.NOK<>(
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
						return fn
							.apply(reader
								.read(in, type));
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			);
		}

	}

}
