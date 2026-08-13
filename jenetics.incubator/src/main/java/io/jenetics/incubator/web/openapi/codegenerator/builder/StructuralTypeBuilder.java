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
import static io.jenetics.incubator.web.openapi.codegenerator.internal.JCodeModels.interface_;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import io.swagger.v3.oas.models.media.ObjectSchema;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

import io.jenetics.incubator.web.openapi.codegenerator.CodeBuilderException;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;
import io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.StructuralTypeSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.StructuralTypeSchema.Component;

/**
 * Builds a structural interface from a {@link ObjectSchema}.
 * {@snippet lang=java:
 * public interface Ticket {
 *     TicketId ticketId();
 *     Date ticketDate();
 *     TicketType ticketType();
 *     EventId eventId();
 * }
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public final class StructuralTypeBuilder implements CodeBuilder {

	private final StructuralTypeSchema schema;

	/**
	 * Create a new structural interface builder.
	 */
	public StructuralTypeBuilder(final StructuralTypeSchema schema) {
		this.schema = requireNonNull(schema);
	}

	public void build(final JCodeModel model) {
		requireNonNull(model);

		final var clazz = interface_(model, schema.name());
		schema.components().forEach(component -> {
			// Create inlined enum types.
			final var enumSchema = enumSchemaOf(component);
			if (enumSchema != null) {
				final var enumClass = enum_(clazz, enumSchema.name());
				new EnumBodyBuilder(enumSchema, enumClass).build(model);
			}

			final var accessor = clazz.method(
				JMod.NONE,
				model.parseType(component.type().toString()),
				component.name()
			);

			if (component.isNullable()) {
				accessor.annotate(Nullable.class);
			}
		});

		final var builder = builderInterface(clazz);
		builder._extends(clazz);
		schema.components().forEach(component -> {
			final var componentType = model.parseType(component.type().toString());
			final var value = builder.method(JMod.NONE, builder, component.name())
				.param(componentType, "value");

			if (component.isNullable()) {
				value.annotate(Nullable.class);
			}

			if (component.isStructural()) {
				builder.method(JMod.NONE, builder, component.name())
					.param(nestedBuilderConsumer(model, component.type().toString()), "builder");
			}
		});
	}

	private static EnumSchema enumSchemaOf(Component component) {
		if (Schemas.isEnum(component.schema())) {
			return new EnumSchema(
				component.schema(),
				component.type(),
				Schemas.typeOf(component.schema()),
				component.schema().getEnum().stream()
					.collect(Collectors.toUnmodifiableList())
			);
		} else {
			return null;
		}
	}

	private static JDefinedClass builderInterface(final JDefinedClass type) {
		try {
			return type._class(
				JMod.NONE,
				"Builder",
				EClassType.INTERFACE
			);
		} catch (JCodeModelException e) {
			throw new CodeBuilderException(
				"Builder[%s]".formatted(type.fullName()), e
			);
		}
	}

	private static AbstractJClass nestedBuilderConsumer(
		final JCodeModel model,
		final String type
	) {
		return model.ref(Consumer.class)
			.narrow(model.ref("%s.Builder".formatted(type)).wildcardSuper());
	}

}
