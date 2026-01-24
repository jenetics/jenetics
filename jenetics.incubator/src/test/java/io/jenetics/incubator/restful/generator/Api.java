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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.jenetics.incubator.restful.generator.model.AllOf;
import io.jenetics.incubator.restful.generator.model.AnyOf;
import io.jenetics.incubator.restful.generator.model.Complex;
import io.jenetics.incubator.restful.generator.model.OneOf;
import io.jenetics.incubator.openapi.model.Property;
import io.jenetics.incubator.restful.generator.model.Struct;
import io.jenetics.incubator.restful.generator.model.Type;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
@SuppressWarnings("rawtypes")
public record Api(OpenAPI api) {

	public record NamedSchema(String name, Schema<?> schema) {
	}

	public List<Complex> types() {
		return schemas().stream()
			.<Complex>mapMulti((schema, sink) -> {
				if (toType(schema) instanceof Complex complex) {
					sink.accept(complex);
				}
			})
			.toList();
	}

	private Type toType(Api.NamedSchema schema) {
		if (schema == null) {
			return null;
		}

		final var dt = DataType.of(schema.schema());
		return switch (dt) {
			case STRING -> toStr(schema);
			case NUMBER -> toNum(schema);
			case INTEGER -> toInt(schema);
			case BOOLEAN -> toBool(schema);
			case ARRAY -> toArray(schema);
			case OBJECT -> toObject(schema);
			case null -> toType(refOf(schema));
		};
	}

	private Type toObject(Api.NamedSchema schema) {
		var oneOf = oneOf(schema);
		if (!oneOf.isEmpty()) {
			return new OneOf(
				schema.name(),
				oneOf.stream()
					.map(this::toType)
					.toList()
			);
		}

		var allOf = allOf(schema);
		if (!allOf.isEmpty()) {
			return new AllOf(
				schema.name(),
				allOf.stream()
					.map(this::toType)
					.toList()
			);
		}

		var anyOf = anyOf(schema);
		if (!anyOf.isEmpty()) {
			return new AnyOf(
				schema.name(),
				allOf.stream()
					.map(this::toType)
					.toList()
			);
		}

		var ref = toType(refOf(schema));
		if (ref != null) {
			return ref;
		}

		var properties = Api.properties(schema).stream()
			.map(entry -> new Property(entry.name(), toType(entry)))
			.toList();

		return new Struct(schema.name(), properties);
	}

	private Primitive.Str toStr(Api.NamedSchema schema) {
		return new Primitive.Str();
	}

	private Primitive.Num toNum(Api.NamedSchema schema) {
		return new Primitive.Num();
	}

	private Primitive.Int toInt(Api.NamedSchema schema) {
		return new Primitive.Int();
	}

	private Primitive.Bool toBool(Api.NamedSchema schema) {
		return new Primitive.Bool();
	}

	private Primitive.Array toArray(Api.NamedSchema schema) {
		return new Primitive.Array(toType(arrayTypeSchema(schema)));
	}

	private List<NamedSchema> schemas() {
		if (api.getComponents() != null &&
			api.getComponents().getSchemas() != null)
		{
			return api.getComponents().getSchemas().entrySet().stream()
				.map(entry -> new NamedSchema(entry.getKey(), entry.getValue()))
				.toList();
		} else {
			return List.of();
		}
	}

	private NamedSchema schemaOfRef(String ref) {
		var index = ref.lastIndexOf('/');
		if (index == -1) {
			return null;
		}

		var reference = ref.substring(index + 1);
		return schemas().stream()
			.filter(entry -> Objects.equals(entry.name(), reference))
			.findFirst()
			.orElse(null);
	}

	private static List<NamedSchema> properties(NamedSchema schema) {
		if (schema.schema().getProperties() != null) {
			return schema.schema().getProperties().entrySet().stream()
				.map(entry -> new NamedSchema(entry.getKey(), entry.getValue()))
				.toList();
		} else {
			return List.of();
		}
	}

	private List<NamedSchema> oneOf(NamedSchema schema) {
		if (schema.schema().getOneOf() != null) {
			return schema.schema().getOneOf().stream()
				.filter(Objects::nonNull)
				.map(s -> new NamedSchema("", s))
				.toList();
		} else {
			return List.of();
		}
	}

	private List<NamedSchema> allOf(NamedSchema schema) {
		if (schema.schema().getAllOf() != null) {
			return schema.schema().getAllOf().stream()
				.filter(Objects::nonNull)
				.map(s -> new NamedSchema("", s))
				.toList();
		} else {
			return List.of();
		}
	}

	private List<NamedSchema> anyOf(NamedSchema schema) {
		if (schema.schema().getAnyOf() != null) {
			return schema.schema().getAnyOf().stream()
				.filter(Objects::nonNull)
				.map(s -> new NamedSchema("", s))
				.toList();
		} else {
			return List.of();
		}
	}

	private NamedSchema refOf(NamedSchema schema) {
		if (schema.schema() != null && schema.schema().get$ref() != null) {
			return schemaOfRef(schema.schema().get$ref());
		} else {
			return null;
		}
	}

	private NamedSchema arrayTypeSchema(NamedSchema schema) {
		if (schema.schema().getItems() != null && schema.schema().getItems().get$ref() != null) {
			return schemaOfRef(schema.schema().getItems().get$ref());
		} else {
			return null;
		}
	}

	public static Api of(String resource) throws IOException {
		final var parser = new OpenAPIV3Parser();
		try (var in = Generator.class.getResourceAsStream(resource)) {
			assert in != null;
			final var content = new String(in.readAllBytes());
			final var result = parser.readContents(content);
			return new Api(result.getOpenAPI());
		}
	}

}
