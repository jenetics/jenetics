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
package io.jenetics.incubator.restfulclient;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DefaultClientTest {

	record Todo(int userId, int id, String title, boolean completed) {
	}

	public enum Account implements Parameter.Header {
		FOO("x-account", "foo"),
		BAR("x-account", "bar");

		private final String name;
		private final String value;

		Account(final String name, final String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String key() {
			return name;
		}

		@Override
		public String value() {
			return value;
		}
	}

	static final Resource<String> DOCUMENT = Resource
		.of("/documents/{id}", String.class)
		.params(ContentType.JSON);

	static final Resource<String> BOOK = Resource
		.of("/books/{id}", String.class)
		.params(ContentType.JSON);

	static final Resource<String> PERSON = Resource
		.of("/persons/{id}", String.class)
		.params(ContentType.JSON);

	static final Parameter.Value ID = Parameter.Path.key("id");

	@Test
	public void call() {
		final var mapper = new ObjectMapper();

		final DefaultClient client = new DefaultClient(
			"https://jsonplaceholder.typicode.com/",
			mapper::readValue,
			mapper::writeValue
		);

		final Mono<Response.Success<Todo>> result = Resource
			.of("/todos/{id}/", Todo.class)
			.params(ID.value("1"))
			.GET(MonoCaller.of(client));

		System.out.println(result.block());
	}

}
