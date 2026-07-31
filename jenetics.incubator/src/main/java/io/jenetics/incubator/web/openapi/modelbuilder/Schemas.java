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
package io.jenetics.incubator.web.openapi.modelbuilder;

import static java.util.Objects.requireNonNull;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.math.BigDecimal;

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

		return switch (schema) {
			case NumberSchema ns -> typeNameOf(ns);
			case BooleanSchema _ -> "java.lang.Boolean";
			case ArraySchema as -> typeNameOf(as);
			case StringSchema ss -> switch (ss.getFormat()) {
				case null, default -> "String";
			};
			case ObjectSchema os -> os.getName();
			case Schema<?> s -> typeNameOfRef(s.get$ref());
		};
	}

	public static String typeNameOfRef(final String ref) {
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

	/**
	 * Return the numeric Java type name of the given number schema.
	 *
	 * @param schema the number schema
	 * @return the numeric Java type
	 */
	public static String typeNameOf(NumberSchema schema) {
		requireNonNull(schema);

		return switch (schema.getFormat()) {
			case "float" -> "float";
			case "double" -> "double";
			case "int32" -> "int";
			case "int64" -> "long";
			default -> BigDecimal.class.getName();
		};
	}

	public static String typeNameOf(ArraySchema schema) {
		return "java.util.List<%s>"
			.formatted(typeNameOfRef(schema.getItems().get$ref()));
	}

	public static boolean isEnum(Schema<?> schema) {
		requireNonNull(schema);
		return schema.getEnum() != null && !schema.getEnum().isEmpty();
	}

}
