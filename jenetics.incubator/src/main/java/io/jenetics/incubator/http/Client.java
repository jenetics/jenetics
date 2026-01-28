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

import java.io.Closeable;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

/**
 * Http client interface.
 * {@snippet lang = java:
 * // Jackson object mapper
 * final var mapper = new ObjectMapper();
 * final var marshaling = BodyMarshaling.of(
 *     mapper::writeValue,
 *     mapper::readValue
 * );
 *
 * // Create client with Java default 'HttpClient',
 * // using the Jackson read and write methods.
 * try (var client = Client.of(marshaling)) {
 *     final var request = new Request.GET<>(
 *         String.class,
 *         URI.create("https://server/apo")
 *     );
 *
 *     final CompletableFuture<ServerResult<String>> result = client.send(request);
 *     result.thenAccept(r -> {
 *         switch (r) {
 *             case ServerResult.OK<String> ok -> IO.println("OK: " + ok);
 *             case ServerResult.NOK<String> nok -> IO.println("ERROR: " + nok);
 *         }
 *     });
 * }
 *}
 *
 * @see Caller
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public interface Client extends Closeable {

	/**
	 * Calls the given {@code resource} and returns its result.
	 *
	 * @apiNote
	 * Server side errors are reported via the {@link ServerResult.NOK} class.
	 * On the other hand, a client side error occurred if the returned future
	 * terminates with an exception.
	 *
	 * @param request the resource to call
	 * @return the call response
	 * @param <T> the response body type
	 * @throws NullPointerException if the given {@code resource} is {@code null}
	 */
	<T> CompletableFuture<ServerResult<T>> send(Request<? extends T> request);

	/**
	 * Return a client which uses the standard Java {@link HttpClient}.
	 *
	 * @param base the base {@code URI} of the client, {@link URI#isAbsolute()}
	 *        must be {@code true}
	 * @param client the underlying HTTP client
	 * @param marshaling the body marshaling
	 * @return a new client
	 */
	static Client of(
		final URI base,
		final HttpClient client,
		final BodyMarshaling marshaling
	) {
		requireNonNull(base);
		return new DefaultClient(base, client, marshaling);
	}

	/**
	 * Return a client which uses the standard Java {@link HttpClient}.
	 *
	 * @param client the underlying HTTP client
	 * @param marshaling the body marshaling
	 * @return a new client
	 */
	static Client of(
		final HttpClient client,
		final BodyMarshaling marshaling
	) {
		return new DefaultClient(null, client, marshaling);
	}

	/**
	 * Return a client which uses the standard Java {@link HttpClient}.
	 *
	 * @see HttpClient#newHttpClient()
	 *
	 * @param base the base {@code URI} of the client, {@link URI#isAbsolute()}
	 *        must be {@code true}
	 * @param marshaling the body marshaling
	 * @return a new client
	 */
	static Client of(final URI base, final BodyMarshaling marshaling) {
		return of(base, HttpClient.newHttpClient(), marshaling);
	}

	/**
	 * Return a client which uses the standard Java {@link HttpClient}.
	 *
	 * @see HttpClient#newHttpClient()
	 *
	 * @param marshaling the body marshaling
	 * @return a new client
	 */
	static Client of(final BodyMarshaling marshaling) {
		return of(HttpClient.newHttpClient(), marshaling);
	}

}
