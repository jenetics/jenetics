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

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import io.jenetics.incubator.restful.Parameter;
import io.jenetics.incubator.restful.Resource;
import io.jenetics.incubator.restful.Response;
import io.jenetics.incubator.restful.ResponseException;

/**
 * The default client implementation which uses the Java {@link HttpClient}.
 * {@snippet class="RestfulClientSnippets" region="DefaultClient.usage"}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public final class DefaultClient implements Client {

	private final URI host;
	private final HttpClient client;
	private final Reader reader;
	private final Writer writer;

	public DefaultClient(
		final URI host,
		final HttpClient client,
		final Reader reader,
		final Writer writer
	) {
		this.host = host.normalize();
		this.client = requireNonNull(client);
		this.reader = requireNonNull(reader);
		this.writer = requireNonNull(writer);
	}

	public DefaultClient(
		final String host,
		final Reader reader,
		final Writer writer
	) {
		this(URI.create(host), HttpClient.newHttpClient(), reader, writer);
	}

	/**
	 * Calls the given {@code resource} and returns its result.
	 *
	 * @param resource the resource to call
	 * @return the call response
	 * @param <T> the response body type
	 * @throws NullPointerException if the given {@code resource} is {@code null}
	 */

	public <T> CompletableFuture<Response.Success<T>>
	call(final Resource<? extends T> resource) {
		final CompletableFuture<HttpResponse<ServerResponse<T>>> response =
			client.sendAsync(
				toRequest(resource),
				new ServerBodyHandler<T>(reader, resource.type())
			);

		return response
			.thenCompose(result ->
				switch (result.body().toResponse(resource, result)) {
					case Response.Success<T> success -> completedFuture(success);
					case Response.Failure<T> failure -> {
						final var exception = new ResponseException(failure);
						yield failedFuture(exception);
					}
				}
			)
			.exceptionallyCompose(throwable -> {
				final var error = new Response.ClientError<>(
					resource,
					switch (throwable) {
						case UncheckedIOException e -> e.getCause();
						case Throwable e -> e;
					}
				);
				final var exception = new ResponseException(error);
				return failedFuture(exception);
			});
	}

	private <T> HttpRequest toRequest(final Resource<? extends T> resource) {
		final var builder = HttpRequest.newBuilder()
			.uri(host.resolve(resource.resolvedPath().substring(1)));

		resource.parameters().stream()
			.filter(p -> p instanceof Parameter.Header)
			.forEach(header -> builder.header(header.key(), header.value()));

		switch (resource.method()) {
			case GET -> builder.GET();
			case POST -> builder.POST(new ClientBodyPublisher(
				writer,
				resource.body().orElse(null)
			));
			case PUT -> builder.PUT(new ClientBodyPublisher(
				writer,
				resource.body().orElse(null)
			));
			case DELETE -> builder.DELETE();
		}

		return builder.build();
	}

}
