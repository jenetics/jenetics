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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;

public class ClientTest {

	static void main() throws Exception {
		final var mapper = new ObjectMapper();
		final var marshaling = BodyMarshaling.of(
			mapper::writeValue,
			mapper::readValue
		);

		try (var client = Client.of(marshaling)) {
			final var request = new Request.GET<>(
				String.class,
				URI.create("https://github.com/")
			);

			final Caller.Sync<String> caller = Caller.Sync.of(client);
			final Response<String> response = caller.call(request);
			switch (response) {
				case Response.Success<String> s -> IO.println(s);
				case Response.Failure<?> f -> {
					switch (f) {
						case Response.ServerError<?> se -> IO.println(se);
						case Response.ClientError<?> ce -> {
							ce.error().printStackTrace();
						}
					}
				}
			}
		}
	}

}
