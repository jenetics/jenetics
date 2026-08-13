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
package io.jenetics.incubator.web.openapi.codegenerator.builder;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.internal.JCodeModels.enum_;
import static io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema.toConstantName;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import io.swagger.v3.oas.models.media.Schema;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

import io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema;

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

	private final EnumSchema schema;

	public EnumBuilder(EnumSchema schema) {
		this.schema = requireNonNull(schema);
	}

	@Override
	public void build(final JCodeModel model) {
		requireNonNull(schema);
		requireNonNull(model);

		final var clazz = enum_(model, schema.name());
		build(schema, clazz, model);
	}

	static void build(
		final EnumSchema schema,
		final JDefinedClass clazz,
		final JCodeModel model
	) {
		final var string = model.ref(String.class);
		final var type = model.parseType(schema.type().toString());

		// Create field, which contains the original value of the enum.
		final var field = clazz.field(JMod.PRIVATE_FINAL, type, VALUE_NAME);

		final var constructor = clazz.constructor(JMod.NONE);
		final var value = constructor.param(type, VALUE_NAME);
		constructor.body().assign(JExpr.refthis(field), value);

		final var method = clazz.method(JMod.PUBLIC, type, VALUE_NAME);
		method.body()._return(field);

		// Add the enum constants
		for (final var constant : schema.constants()) {
			clazz.enumConstant(toConstantName(constant))
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
			case "java.net.URI" ->
				model.ref(URI.class).staticInvoke("create")
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

}
