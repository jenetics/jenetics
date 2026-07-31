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
import static io.jenetics.incubator.web.openapi.modelbuilder.CodeModels.record_;
import static io.jenetics.incubator.web.openapi.modelbuilder.Schemas.isEnum;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Builds <em>typed value</em> classes, which is essentially a record with wraps
 * a <em>value</em> type, like a numeric value or a string. The name of the
 * record adds semantic to the value.
 * {@snippet lang=java:
 * public record TicketConfirmation(String value) {
 *     public TicketConfirmation {
 *         Objects.requireNonNull(value);
 *     }
 * }
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public final class TypedValueBuilder {

	private String name;
	private String type;

	/**
	 * Create a new typed value builder.
	 */
	public TypedValueBuilder() {
	}

	/**
	 * Set the (full qualified) wrapper class name.
	 *
	 * @param name the wrapper class name
	 * @return {@code this} builder
	 */
	public TypedValueBuilder name(final String name) {
		this.name = requireNonNull(name);
		return this;
	}

	/**
	 * The wrapped type.
	 *
	 * @param type the wrapped type
	 * @return {@code this} builder
	 */
	public TypedValueBuilder type(final String type) {
		this.type = requireNonNull(type);
		return this;
	}

	/**
	 * Builds wrapper class and adds it to the {@code model}.
	 *
	 * @param model the model the enum class is build and added to
	 */
	public void build(final JCodeModel model) {
		final var clazz = record_(model, name);

		final var valueType = model.parseType(type);
		clazz.recordComponent(valueType, "value");

		if (valueType.isReference()) {
			clazz.compactConstructor(JMod.PUBLIC).body().add(
				model.ref(Objects.class)
					.staticInvoke("requireNonNull")
					.arg(JExpr.ref("value"))
			);
		}
	}

	/**
	 * Builds the class from the {@code schema} and adds it to the {@code model}.
	 *
	 * @see SchemaTypeBuilder
	 *
	 * @param schema the schema spec which defines the class
	 * @param model the model where the class is added to
	 * @return {@code true} if the builder has generated a class from the schema,
	 *         {@code false} if the {@code schema} doesn't specify the Java type,
	 *         the builder is able to build.
	 */
	public static boolean build(final Schema<?> schema,  final JCodeModel model) {
		requireNonNull(schema);
		requireNonNull(model);

		final var type = switch (schema) {
			case NumberSchema ns -> Schemas.typeNameOf(ns);
			case ArraySchema as -> Schemas.typeNameOf(as);
			case DateSchema _ -> LocalDate.class.getName();
			case DateTimeSchema _ -> OffsetDateTime.class.getName();
			case UUIDSchema _ -> UUID.class.getName();
			case StringSchema ss when !isEnum(ss) -> String.class.getName();
			default -> null;
		};

		if (type != null) {
			new TypedValueBuilder()
				.name(schema.getName())
				.type(type)
				.build(model);

			return true;
		} else {
			return false;
		}
	}

}
