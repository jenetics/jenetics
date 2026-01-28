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

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import org.joda.time.Hours;

import io.jenetics.incubator.http.BodyMarshaling;
import io.jenetics.incubator.http.Caller;
import io.jenetics.incubator.http.Client;
import io.jenetics.incubator.http.Request;
import io.jenetics.incubator.http.Response;
import io.jenetics.incubator.http.ServerResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public final class MuseumApi {

	static final Parameter.Value
		MUSEUM_NAME =
		Parameter.Path.key("museum-name");

	static final Parameter.Value
		LIMIT =
		Parameter.Query.key("limit");

	static final Resource<Hours>
		MUSEUM_HOURS =
		Resource.of("/museums/{museum-name}/museum-hours", Hours.class)
			.add(ContentType.JSON);

	static void main() throws Exception {
		final var mapper = new ObjectMapper();
		final var marshaling = BodyMarshaling.of(
			mapper::writeValue,
			mapper::readValue
		);

		try (var client = Client.of(marshaling)) {
			final Response<Hours> response = MUSEUM_HOURS
				.add(Parameter.path("museum-name", "KHM"))
				.add(Parameter.query("limit", "10"))
				.PUT("body", Caller.Sync.of(client));

			switch (response) {
				case Response.Success<Hours> s -> {}
				case Response.Failure<?> f -> {
					switch (f) {
						case Response.ServerError<?> se -> {}
						case Response.ClientError<?> ce -> {}
					}
				}
			}

			final CompletableFuture<Response<Hours>> async = MUSEUM_HOURS
				.add(Parameter.path("museum-name", "KHM"))
				.add(Parameter.query("limit", "10"))
				.PUT("body", Caller.Async.of(client));

			final Flow.Publisher<Response<Hours>> publisher = MUSEUM_HOURS
				.add(Parameter.path("museum-name", "KHM"))
				.add(Parameter.query("limit", "10"))
				.PUT("body", Caller.Reactive.of(client));

			final Mono<Response<Hours>> mono = MUSEUM_HOURS
				.add(Parameter.path("museum-name", "KHM"))
				.add(Parameter.query("limit", "10"))
				.PUT("body", MonoCaller.of(client));

		}

	}

	void foo() throws IOException {
		// Jackson object mapper
		final var mapper = new ObjectMapper();
		final var marshaling = BodyMarshaling.of(
			mapper::writeValue,
			mapper::readValue
		);

		// Create client with Java default 'HttpClient',
		// using the Jackson read and write methods.
		try (var client = Client.of(marshaling)) {
			final var request = new Request.GET<>(
				String.class,
				URI.create("https://server/apo")
			);

			final CompletableFuture<ServerResult<String>> result = client.send(request);
			result.thenAccept(r -> {
				switch (r) {
					case ServerResult.OK<String> ok -> System.out.println("OK: " + ok);
					case ServerResult.NOK<String> nok -> System.out.println("ERROR: " + nok);
				}
			});
		}
	}

}
