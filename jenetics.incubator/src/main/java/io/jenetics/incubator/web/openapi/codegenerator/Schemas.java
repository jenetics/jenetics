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
package io.jenetics.incubator.web.openapi.codegenerator;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.ApiContext.api;
import static io.jenetics.incubator.web.openapi.codegenerator.ApiContext.namespace;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ByteArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.EmailSchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.PasswordSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Some helper methods for handling schemas.
 *
 * @implNote
 * Uses {@link ApiContext} class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public final class Schemas {
	private Schemas() {
	}

	/**
	 * Return the type name of the given {@code schema}. The type name is used
	 * as component type in structural interfaces.
	 *
	 * @param schema the schema
	 * @return the type name of the given {@code schema}
	 */
	public static Qname nameOf(Schema<?> schema) {
		return nameOf0(schema).orElseThrow(() ->
			new IllegalArgumentException("No name for '%s'.".formatted(schema))
		);
	}

	public static boolean hasName(Schema<?> schema) {
		return nameOf0(schema).isPresent();
	}

	private static Optional<Qname> nameOf0(Schema<?> schema) {
		requireNonNull(schema);

		var result = switch (schema) {
			// These types will be generated and not mapped on
			// existing Java types.
			case Schema<?> s
				when isEnum(s) && s.getName() != null ->
					new Qname(namespace(), s.getName());
			case ObjectSchema _, ComposedSchema _
				when schema.getName() != null ->
					new Qname(namespace(), schema.getName());
			default -> null;
		};

		if (result == null) {
			result = typeOf(schema);
		}

		return Optional.ofNullable(result);
	}

	public static Qname typeOf(Schema<?> schema) {
		return switch (schema) {
			// These schemas are mapped to Java types.
			case BinarySchema _ -> Qname.of("byte[]");
			case BooleanSchema _ -> Qname.of(Boolean.class.getName());
			case ByteArraySchema _ -> Qname.of("byte[]");
			case DateSchema _ -> Qname.of(LocalDate.class.getName());
			case DateTimeSchema _ -> Qname.of(OffsetDateTime.class.getName());
			case EmailSchema _ -> Qname.of(String.class.getName());
			case FileSchema _ -> Qname.of(Path.class.getName());
			case PasswordSchema _  -> Qname.of("char[]");
			case UUIDSchema _ -> Qname.of(UUID.class.getName());
			case ArraySchema s -> Qname.of(
				"java.util.List<%s>".formatted(
					deref(s.getItems().get$ref())
						.map(Schemas::nameOf)
						.map(Objects::toString)
						.orElse(Object.class.getName())
				)
			);

			// Obey the format for the following schemas.
			case StringSchema s -> switch (s.getFormat()) {
				case "uri", "URI" -> Qname.of(URI.class.getName());
				case null, default -> Qname.of(String.class.getName());
			};
			case IntegerSchema s -> switch (s.getFormat()) {
				case "int64" -> Qname.of(Long.class.getName());
				case null, default -> Qname.of(Integer.class.getName());
			};
			case NumberSchema s -> switch (s.getFormat()) {
				case "float" -> Qname.of(Float.class.getName());
				case "double" -> Qname.of(Double.class.getName());
				case "int32" -> Qname.of(Integer.class.getName());
				case "int64" -> Qname.of(Long.class.getName());
				case null, default -> Qname.of(BigDecimal.class.getName());
			};

			default -> null;
		};
	}

	/**
	 * Checks whether the given {@code schema} is an enumeration.
	 *
	 * @param schema the schema to check; nulls allowed
	 * @return {@code true} if the given {@code schema} is an enumeration,
	 *         {@code false} otherwise
	 */
	public static boolean isEnum(Schema<?> schema) {
		requireNonNull(schema);
		return schema.getEnum() != null && !schema.getEnum().isEmpty();
	}

	public static Optional<Schema<?>> deref(String ref) {
		if (ref != null) {
			final var index = ref.lastIndexOf("/");
			if (index != -1) {
				final var name = ref.substring(index + 1);
				return Optional.ofNullable(schemas().get(name));
			}
		}

		return Optional.empty();
	}

	public static Optional<Schema<?>> ref(Schema<?> schema) {
		final var ref = schema != null ? schema.get$ref() : null;
		return deref(ref);
	}

	public static Optional<Schema<?>> parentOf(Schema<?> schema) {
		return schemas().values().stream()
			.filter(s -> properties(s).values().stream().anyMatch(p -> p == schema))
			.findFirst();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Schema<?>> schemas() {
		final var components = api().getComponents();
		if (components != null) {
			final var schemas = (Map<String, Schema<?>>)(Object)components.getSchemas();
			if (schemas != null) {
				return Map.copyOf(schemas);
			}
		}

		return Map.of();
	}


	@SuppressWarnings("unchecked")
	public static List<Schema<?>> allOf(Schema<?> schema) {
		return schema.getAllOf() != null
			? List.copyOf((List<Schema<?>>)(List<?>)schema.getAllOf())
			: List.of();
	}

	@SuppressWarnings("unchecked")
	public static List<Schema<?>> oneOf(Schema<?> schema) {
		return schema.getOneOf() != null
			? List.copyOf((List<Schema<?>>)(List<?>)schema.getOneOf())
			: List.of();
	}

	@SuppressWarnings("unchecked")
	public static List<Schema<?>> anyOf(Schema<?> schema) {
		return schema.getAnyOf() != null
			? List.copyOf((List<Schema<?>>)(List<?>)schema.getAnyOf())
			: List.of();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Schema<?>> properties(Schema<?> schema) {
		return schema.getProperties() != null
			? Map.copyOf((Map<String, Schema<?>>)(Map<String, ?>)schema.getProperties())
			: Map.of();
	}

}
