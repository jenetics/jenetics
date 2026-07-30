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
package io.jenetics.incubator.web.openapi.structural;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.structural.CodeModels.enum_;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Generates {@link Enum} class from a
 * {@link io.swagger.v3.oas.models.media.StringSchema} with enum format.
 */
public final class EnumBuilder {

	private static final String VALUE_NAME = "value";

	private String name;
	private final List<String> constants = new ArrayList<>();

	/**
	 * Create a new enum code generator.
	 */
	public EnumBuilder() {
	}

	/**
	 * Set the (full qualified) enum class name.
	 *
	 * @param name the enum class name
	 * @return {@code this} builder
	 */
	public EnumBuilder name(final String name) {
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
	 * @return {@code this} generator
	 */
	public EnumBuilder constant(final String name) {
		requireNonNull(name);
		constants.add(name);
		return this;
	}

	/**
	 * Builds the enum class and adds it to the {@code model}.
	 *
	 * @param model the model the enum class is build and added to
	 */
	public void build(final JCodeModel model) {
		final var clazz = enum_(model, name);

		// Create field, which contains the original string value of the enum.
		final var field = clazz.field(JMod.PRIVATE_FINAL, String.class, VALUE_NAME);

		final var constructor = clazz.constructor(JMod.NONE);
		final var value = constructor.param(String.class, VALUE_NAME);
		constructor.body().assign(JExpr.refthis(field), value);

		final var method = clazz.method(JMod.PUBLIC, String.class, VALUE_NAME);
		method.body()._return(field);

		// Add the enum constants
		for (final var name : constants) {
			clazz.enumConstant(toConstantName(name)).arg(JExpr.lit(name));
		}

		// Override 'toString' method.
		final var toString = clazz.method(JMod.PUBLIC, String.class, "toString");
		toString.annotate(Override.class);
		toString.body()._return(field);

		// Implement 'parse' method.
		final var optional = model.ref(Optional.class);
		final var parse = clazz.method(
			JMod.PUBLIC | JMod.STATIC,
			optional.narrow(clazz),
			"parse"
		);
		final var parseValue = parse.param(String.class, VALUE_NAME);
		final var constants = parse.body()
			.forEach(clazz, "constant", JExpr.invoke("values"));
		final var constant = constants.var();
		constants.body()
			._if(
				constant.invoke(VALUE_NAME).invoke("equals").arg(parseValue)
					.cor(constant.invoke("name").invoke("equals").arg(parseValue))
			)
			._then()
			._return(optional.staticInvoke("of").arg(constant));
		parse.body()._return(optional.staticInvoke("empty"));
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

	public static boolean build(final Schema<?> schema,  final JCodeModel model) {
		if (schema instanceof StringSchema ss &&
			schema.getEnum() != null &&
			!schema.getEnum().isEmpty())
		{
			final var builder = new EnumBuilder();
			builder.name(schema.getName());
			ss.getEnum().forEach(builder::constant);
			builder.build(model);

			return true;
		} else {
			return false;
		}

	}

}
