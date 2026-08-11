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
package io.jenetics.incubator.web.openapi.codegenerator.model;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.Context.qnameOf;
import static io.jenetics.incubator.web.openapi.codegenerator.CodeModels.enum_;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import io.jenetics.incubator.web.openapi.codegenerator.CodeBuilder;
import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;

/**
 * Builds {@link Enum} class from a {@link Schema} with enum format.
 * {@snippet lang=java:
 * public enum TicketType {
 *     EVENT("event"),
 *     GENERAL("general");
 *
 *     private final String value;
 *     TicketType(String value) {
 *         this.value = value;
 *     }
 *
 *     public String value() {
 *         return value;
 *     }
 *     @Override
 *     public String toString() {
 *         return value;
 *     }
 *     public static Optional<TicketType> of(String value) {
 *         for (TicketType constant : values()) {
 *             if (constant.value().equals(value) ||
 *                 constant.name().equals(value))
 *             {
 *                 return Optional.of(constant);
 *             }
 *         }
 *         return Optional.empty();
 *     }
 * }
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public final class EnumBuilder implements CodeBuilder {

	private static final String VALUE_NAME = "value";

	private Qname name;
	private String valueType = String.class.getName();
	private final List<Object> constants = new ArrayList<>();

	/**
	 * Create a new enum code builder.
	 */
	public EnumBuilder() {
	}

	/**
	 * Set the (full qualified) enum class name.
	 *
	 * @param name the enum class name
	 * @return {@code this} builder
	 */
	public EnumBuilder name(final Qname name) {
		this.name = requireNonNull(name);
		return this;
	}

	/**
	 * Add an enum constant with the given {@code name}. The {@code name} is
	 * converted into upper case letters and name mangling is performed if needed.
	 * The original enum name can be accessed with the {@code value()} method of
	 * the generated enum class.
	 *
	 * @param name the constant name
	 * @return {@code this} builder
	 */
	public EnumBuilder constant(final String name) {
		requireNonNull(name);
		constants.add(name);
		return this;
	}

	private EnumBuilder valueType(final String valueType) {
		this.valueType = requireNonNull(valueType);
		return this;
	}

	private EnumBuilder constantValue(final Object value) {
		constants.add(requireNonNull(value));
		return this;
	}

	/**
	 * Builds an enum class and adds it to the {@code model}.
	 *
	 * @param model the model the enum class is build and added to
	 */
	@Override
	public void build(final JCodeModel model) {
		final var clazz = enum_(model, name);
		final var string = model.ref(String.class);
		final var type = model.parseType(valueType);

		// Create field, which contains the original value of the enum.
		final var field = clazz.field(JMod.PRIVATE_FINAL, type, VALUE_NAME);

		final var constructor = clazz.constructor(JMod.NONE);
		final var value = constructor.param(type, VALUE_NAME);
		constructor.body().assign(JExpr.refthis(field), value);

		final var method = clazz.method(JMod.PUBLIC, type, VALUE_NAME);
		method.body()._return(field);

		// Add the enum constants
		for (final var constant : constants) {
			clazz.enumConstant(toConstantName(String.valueOf(constant)))
				.arg(literal(model, type, constant));
		}

		// Override 'toString' method.
		final var toString = clazz.method(JMod.PUBLIC, String.class, "toString");
		toString.annotate(Override.class);
		toString.body()._return(string.staticInvoke("valueOf").arg(field));

		// Implement 'of' factory method.
		final var optional = model.ref(Optional.class);
		final var parse = clazz.method(
			JMod.PUBLIC | JMod.STATIC,
			optional.narrow(clazz),
			"of"
		);
		final var parseValue = parse.param(String.class, VALUE_NAME);
		final var constants = parse.body()
			.forEach(clazz, "constant", JExpr.invoke("values"));
		final var constant = constants.var();
		constants.body()
			._if(
				string.staticInvoke("valueOf").arg(constant.invoke(VALUE_NAME))
					.invoke("equals").arg(parseValue)
					.cor(constant
						.invoke("name")
						.invoke("equals").arg(parseValue))
			)
			._then()
			._return(optional.staticInvoke("of").arg(constant));
		parse.body()._return(optional.staticInvoke("empty"));
	}

	private static IJExpression literal(
		final JCodeModel model,
		final AbstractJType type,
		final Object value
	) {
		return switch (type.fullName()) {
			case "boolean", "java.lang.Boolean" -> JExpr.lit(booleanValue(value));
			case "double", "java.lang.Double" ->
				JExpr.lit(numberValue(value).doubleValue());
			case "float", "java.lang.Float" ->
				JExpr.lit(numberValue(value).floatValue());
			case "int", "java.lang.Integer" ->
				JExpr.lit(numberValue(value).intValue());
			case "long", "java.lang.Long" ->
				JExpr.lit(numberValue(value).longValue());
			case "java.math.BigDecimal" ->
				JExpr._new(model.ref(BigDecimal.class))
					.arg(JExpr.lit(value.toString()));
			case null, default -> JExpr.lit(String.valueOf(value));
		};
	}

	private static Number numberValue(final Object value) {
		return value instanceof Number number
			? number
			: new BigDecimal(value.toString());
	}

	private static boolean booleanValue(final Object value) {
		return value instanceof Boolean bool
			? bool
			: Boolean.parseBoolean(value.toString());
	}

	private static String toConstantName(final String value) {
		final var name = new StringBuilder();
		final var text = value.toUpperCase(Locale.ROOT);

		for (int i = 0; i < text.length();) {
			final var cp = text.codePointAt(i);
			i += Character.charCount(cp);

			if (Character.isJavaIdentifierPart(cp)) {
				if (name.isEmpty() && !Character.isJavaIdentifierStart(cp)) {
					name.append('_');
				}
				name.appendCodePoint(cp);
			} else if (!name.isEmpty() && name.charAt(name.length() - 1) != '_') {
				name.append('_');
			}
		}

		while (!name.isEmpty() && name.charAt(name.length() - 1) == '_') {
			name.setLength(name.length() - 1);
		}

		return name.isEmpty() ? "VALUE" : name.toString();
	}

	public static Optional<EnumBuilder> of(final Schema<?> schema) {
		requireNonNull(schema);

		if (Schemas.isEnum(schema)) {
			final var builder = new EnumBuilder();
			builder
				.name(qnameOf(schema))
				.valueType(valueTypeNameOf(schema));
			schema.getEnum().forEach(builder::constantValue);

			return Optional.of(builder);
		} else {
			return Optional.empty();
		}
	}

	private static String valueTypeNameOf(final Schema<?> schema) {
		return switch (schema) {
			case StringSchema ss -> Schemas.javaTypeNameOf(ss);
			default -> Schemas.javaTypeNameOfPrimitives(schema);
		};
	}

}
