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
import static io.jenetics.incubator.web.openapi.codegenerator.internal.JCodeModels.record_;
import static io.jenetics.incubator.web.openapi.codegenerator.Schemas.isEnum;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JOp;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import io.jenetics.incubator.web.openapi.codegenerator.CodeBuilder;
import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;

/**
 * Builds <em>typed value</em> classes, which is essentially a record with wraps
 * a <em>value</em> type, like a numeric value or a string. The name of the
 * record adds semantic to the value.
 * {@snippet lang = java:
 * import java.util.Objects;
 * public record TicketConfirmation(String value) {
 *     public TicketConfirmation {
 *         Objects.requireNonNull(value);
 *     }
 * }
 *}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public final class TypedValueBuilder implements CodeBuilder {

	static final String VALUE_COMPONENT_NAME = "value";

	private Qname name;
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
	public TypedValueBuilder name(final Qname name) {
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
	@Override
	public void build(final JCodeModel model) {
		final var clazz = record_(model, name);

		final var valueType = model.parseType(type);
		final var boxedValueType = valueType.boxify();
		clazz.recordComponent(valueType, VALUE_COMPONENT_NAME);

		if (valueType.isReference()) {
			clazz.compactConstructor(JMod.PUBLIC).body().add(
				model.ref(Objects.class)
					.staticInvoke("requireNonNull")
					.arg(JExpr.ref(VALUE_COMPONENT_NAME))
			);
		}

		final var box = clazz.method(
			JMod.PUBLIC | JMod.STATIC,
			clazz,
			"box"
		);
		box.annotate(Nullable.class);
		final var value = box.param(boxedValueType, VALUE_COMPONENT_NAME);
		value.annotate(Nullable.class);
		box.body()._return(
			JOp.cond(
				JOp.ne(value, JExpr._null()),
				JExpr._new(clazz).arg(value),
				JExpr._null()
			)
		);

		final var unbox = clazz.method(
			JMod.PUBLIC | JMod.STATIC,
			boxedValueType,
			"unbox"
		);
		unbox.annotate(Nullable.class);
		final var boxed = unbox.param(clazz, "box");
		boxed.annotate(Nullable.class);
		unbox.body()._return(
			JOp.cond(
				JOp.ne(boxed, JExpr._null()),
				JExpr.invoke(boxed, VALUE_COMPONENT_NAME),
				JExpr._null()
			)
		);

		final var with = clazz.method(
			JMod.PUBLIC,
			clazz,
			"with"
		);
		with.annotate(Nullable.class);
		final var fn = with.param(
			model.ref(Function.class).narrow(
				boxedValueType.wildcardSuper(),
				boxedValueType.annotated(Nullable.class).wildcardExtends()
			),
			"fn"
		);
		final var newValue = with.body().decl(
			JMod.FINAL,
			boxedValueType,
			"newValue",
			JExpr.invoke(fn, "apply").arg(JExpr.ref(VALUE_COMPONENT_NAME))
		);
		with.body()._return(
			JOp.cond(
				JOp.ne(newValue, JExpr._null()),
				JExpr._new(clazz).arg(newValue),
				JExpr._null()
			)
		);
	}

	public static Optional<TypedValueBuilder> of(final Schema<?> schema) {
		requireNonNull(schema);

		if (isEnum(schema)) {
			return Optional.empty();
		}

		final var type = Schemas.javaTypeNameOfPrimitives(schema);
		if (type != null) {
			final var builder = new TypedValueBuilder()
				.name(qnameOf(schema))
				.type(type);

			return Optional.of(builder);
		} else {
			return Optional.empty();
		}
	}

}
