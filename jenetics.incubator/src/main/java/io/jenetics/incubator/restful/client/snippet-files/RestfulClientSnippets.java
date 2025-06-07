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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jenetics.incubator.restful.Parameter;
import io.jenetics.incubator.restful.Resource;
import io.jenetics.incubator.restful.Response;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class RestfulClientSnippets {
	private RestfulClientSnippets() {
	}

	static final class DefaultClientSnippets {
		void usage() {
			// @start region="DefaultClient.usage"
			// Jackson object mapper.
			final var mapper = new ObjectMapper();

			// Client with Jackson reader/writer.
			final var client = new DefaultClient(
				"https://jsonplaceholder.typicode.com/",
				mapper::readValue,
				mapper::writeValue
			);

			// Resource result.
			record Todo(int userId, int id, String title, boolean completed) { }

			// Call the resource.
			final Response<Todo> result = Resource
				.of("/todos/{id}/", Todo.class)
				.params(Parameter.path("id", "123"))
				.GET(client.sync());

			// Print the result.
			switch (result) {
				case Response.Success<Todo> s -> System.out.println(s.body());
				case Response.Failure<Todo> f -> System.err.println("Error: " + f.status());
			}
			// @end
		}
	}

}
