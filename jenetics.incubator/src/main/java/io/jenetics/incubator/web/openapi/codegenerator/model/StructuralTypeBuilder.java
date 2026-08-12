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

import static io.jenetics.incubator.web.openapi.codegenerator.internal.JCodeModels.interface_;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import io.swagger.v3.oas.models.media.ObjectSchema;

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import io.jenetics.incubator.web.openapi.codegenerator.CodeBuilderException;
import io.jenetics.incubator.web.openapi.codegenerator.SchemaModel;
import io.jenetics.incubator.web.openapi.codegenerator.StructuralTypeModel;

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
public final class StructuralTypeBuilder {

	/**
	 * Create a new structural interface builder.
	 */
	private StructuralTypeBuilder() {
	}


	public static void build(SchemaModel schema, final JCodeModel model) {
		if (schema instanceof StructuralTypeModel stm) {
			build0(stm, model);
		}
	}

	private static void build0(final StructuralTypeModel schema, final JCodeModel model) {
		final var clazz = interface_(model, schema.name());
		schema.components().forEach(c -> {
			final var component = clazz.method(
				JMod.NONE,
				model.parseType(c.type().toString()),
				c.name()
			);

			if (c.nullable()) {
				component.annotate(Nullable.class);
			}
		});

		final var builder = builderInterface(clazz);
		builder._extends(clazz);
		schema.components().forEach(component -> {
			final var componentType = model.parseType(component.type().toString());
			final var value = builder.method(JMod.NONE, builder, component.name())
				.param(componentType, "value");

			if (component.nullable()) {
				value.annotate(Nullable.class);
			}

			if (component.structural()) {
				builder.method(JMod.NONE, builder, component.name())
					.param(nestedBuilderConsumer(model, component.type().toString()), "builder");
			}
		});
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
