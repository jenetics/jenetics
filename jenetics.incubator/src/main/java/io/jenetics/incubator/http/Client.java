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

import java.io.Closeable;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

/**
 * Rest client interface. Clients are inherently asynchronous.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public interface Client extends Closeable {

	/**
	 * Calls the given {@code resource} and returns its result.
	 *
	 * @apiNote
	 * Server side errors are reported via the {@link ServerResponse.NOK} class.
	 * On the other hand, a client side error occurred if the returned future
	 * terminates with an exception.
	 *
	 * @param request the resource to call
	 * @return the call response
	 * @param <T> the response body type
	 * @throws NullPointerException if the given {@code resource} is {@code null}
	 */
	<T> CompletableFuture<ServerResponse<T>>
	send(URI uri, Request<? extends T> request);

	/**
	 * Return a client which uses the standard Java {@link HttpClient}.
	 *
	 * @param client the underlying HTTP client
	 * @param reader the object reader (deserializer)
	 * @param writer the object writer (serializer)
	 * @return a new client
	 */
	static Client of(
		final HttpClient client,
		final ResponseBodyReader reader,
		final RequestBodyWriter writer
	) {
		return new DefaultClient(client, reader, writer);
	}

	/**
	 * Return a client which uses the standard Java {@link HttpClient}.
	 *
	 * @see HttpClient#newHttpClient()
	 *
	 * @param reader the object reader (deserializer)
	 * @param writer the object writer (serializer)
	 * @return a new client
	 */
	static Client of(
		final ResponseBodyReader reader,
		final RequestBodyWriter writer
	) {
		return of(HttpClient.newHttpClient(), reader, writer);
	}

}
