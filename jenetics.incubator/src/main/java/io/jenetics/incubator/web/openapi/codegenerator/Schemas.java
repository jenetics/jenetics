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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ByteArraySchema;
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
import java.util.UUID;

/**
 * Some helper methods for handling schemas.
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
	public static String typeNameOf(Schema<?> schema) {
		requireNonNull(schema);

		var name = javaTypeNameOfPrimitives(schema);
		if (name == null) {
			name = switch (schema) {
				case StringSchema ss when isEnum(ss) -> ss.getName();
				case ObjectSchema os -> os.getName();
				case Schema<?> s -> typeNameOfSchemaRef(s.get$ref());
			};
		}

		return name;
	}

	/**
	 * Returns the type name for all primitive schemas, which are essentially
	 * all, exception {@link ObjectSchema}.
	 *
	 * @param schema the schema
	 * @return the corresponding Java type name, or {@code null} if the schema
	 *         can't be represented by a Java type.
	 */
	public static String javaTypeNameOfPrimitives(Schema<?> schema) {
		return switch (schema) {
			case ArraySchema as -> "java.util.List<%s>"
				.formatted(typeNameOfSchemaRef(as.getItems().get$ref()));
			case BinarySchema _ -> "byte[]";
			case BooleanSchema _ -> Boolean.class.getName();
			case ByteArraySchema _ -> "byte[]";
			case DateSchema _ -> LocalDate.class.getName();
			case DateTimeSchema _ -> OffsetDateTime.class.getName();
			case EmailSchema _ -> String.class.getName();
			case FileSchema _ -> Path.class.getName();
			case IntegerSchema _ -> Integer.class.getName();
			case NumberSchema ns -> Schemas.javaTypeNameOf(ns);
			case PasswordSchema _  -> char[].class.getName();
			case StringSchema ss when !isEnum(ss) -> javaTypeNameOf(ss);
			case UUIDSchema _ -> UUID.class.getName();
			default -> null;
		};
	}

	/**
	 * Return the Java type name of the given {@code schame}. This method
	 * obeys the schema string format when returning the Java type name.
	 *
	 * @param schema the schema
	 * @return the corresponding Java type name
	 */
	public static String javaTypeNameOf(StringSchema schema) {
		return switch (schema.getFormat()) {
			case "uri", "URI" -> URI.class.getName();
			case null, default -> String.class.getName();
		};
	}

	public static String javaTypeNameOf(NumberSchema schema) {
		return switch (schema.getFormat()) {
			case "float" -> Float.class.getName();
			case "double" -> Double.class.getName();
			case "int32" -> Integer.class.getName();
			case "int64" -> Long.class.getName();
			default -> BigDecimal.class.getName();
		};
	}

	public static String typeNameOfSchemaRef(final String ref) {
		if (ref != null) {
			final var index = ref.lastIndexOf("/");
			if (index != -1) {
				return ref.substring(index + 1);
			} else {
				return  "java.lang.Object";
			}
		} else {
			return "java.lang.Object";
		}
	}

	public static boolean isEnum(Schema<?> schema) {
		requireNonNull(schema);
		return schema.getEnum() != null && !schema.getEnum().isEmpty();
	}

	public static Schema<?> schemaOfRef(OpenAPI api, String ref) {
		if (ref == null) {
			return null;
		}
		final var typeName = typeNameOfSchemaRef(ref);
		return api.getComponents().getSchemas().get(typeName);
	}

}
