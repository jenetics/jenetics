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
package io.jenetics.incubator.restful.generator;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import io.jenetics.incubator.openapi.model.Property;
import io.jenetics.incubator.restful.generator.model.Struct;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public class Generator {

	private final Api api;

	public Generator(final Api api) {
		this.api = requireNonNull(api);
	}

	public static void main(String[] args) throws IOException {
		final var resource = "/io/jenetics/incubator/restful/museum-api.yaml";
		final var generator = new Generator(Api.of(resource));

		final List<Struct> structs = generator.api.types().stream()
			.peek(System.out::println)
			.filter(t -> "Ticket".equals(t.name()))
			.map(t -> (Struct)t)
			.toList();

		System.out.println(toString(structs.getFirst()));
	}

	static String toString(final Struct struct) {
		var prop = struct.properties().stream()
			.map(Generator::toString)
			.collect(Collectors.joining(",\n"))
			.indent(4);

		return  """
			public record %s(
			%s) { }
			""".formatted(struct.name(), prop);
	}

	static String toString(final Property property) {
		return property.type() + " " + property.name();
	}

}
