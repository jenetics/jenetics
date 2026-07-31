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

import static io.jenetics.incubator.web.openapi.modelbuilder.ModelBuilder.API;
import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.modelbuilder.CodeModels.interface_;
import static io.jenetics.incubator.web.openapi.modelbuilder.Schemas.typeNameOf;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

	private record Component(
		String name,
		String type,
		boolean nestedBuilder,
		boolean nullable
	) {
		Component {
			requireNonNull(name);
			requireNonNull(type);
		}
	}

	private String name;
	private final List<Component> components = new ArrayList<>();

	/**
	 * Create a new structural interface builder.
	 */
	public StructuralTypeBuilder() {
	}

	/**
	 * Set the (full qualified) structural interface name.
	 *
	 * @param name the structural interface name
	 * @return {@code this} builder
	 */
	public StructuralTypeBuilder name(final String name) {
		this.name = requireNonNull(name);
		return this;
	}

	/**
	 * Adds a component to the structural interface.
	 *
	 * @param name the component name
	 * @param type the component type
	 * @return {@code this} builder
	 */
	StructuralTypeBuilder component(final String name, final String type) {
		component(name, type, isStructureType(type), false);
		return this;
	}

	/**
	 * Adds a component to the structural interface.
	 *
	 * @param name the component name
	 * @param schema the component schema
	 * @return {@code this} builder
	 */
	StructuralTypeBuilder component(String name, Schema<?> schema) {
		component(name, typeNameOf(schema), isStructureSchema(schema), false);
		return this;
	}

	private boolean isStructureSchema(Schema<?> schema) {
		if (schema.get$ref() != null) {
			return Schemas.schemaOfRef(API.get(), schema.get$ref()) instanceof ObjectSchema;
		} else {
			return false;
		}
	}

	private boolean isStructureType(String type) {
		return API.get().getComponents().getSchemas().get(type) instanceof ObjectSchema;
	}

	private void component(
		final String name,
		final String type,
		final boolean nestedBuilder,
		final boolean nullable
	) {
		components.add(new Component(name, type, nestedBuilder, nullable));
	}

	/**
	 * Builds a structural interface and adds it to the {@code model}.
	 *
	 * @param model the model the structural interface is build and added to
	 */
	public void build(final JCodeModel model) {
		final var clazz = interface_(model, name);
		components.forEach(c -> {
			final var component = clazz.method(
				JMod.NONE,
				model.parseType(c.type()),
				c.name()
			);

			if (c.nullable()) {
				component.annotate(Nullable.class);
			}
		});

		final var builder = builderInterface(clazz);
		builder._extends(clazz);
		components.forEach(c -> {
			final var componentType = model.parseType(c.type());
			final var value = builder.method(JMod.NONE, builder, c.name())
				.param(componentType, "value");

			if (c.nullable()) {
				value.annotate(Nullable.class);
			}

			if (c.nestedBuilder()) {
				builder.method(JMod.NONE, builder, c.name())
					.param(nestedBuilderConsumer(model, c.type()), "builder");
			}
		});
	}

	private static JDefinedClass builderInterface(final JDefinedClass type) {
		try {
			return type._class(
				JMod.PUBLIC,
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

		if (schema instanceof ObjectSchema os) {
			final var builder = new StructuralTypeBuilder();
			builder.name(schema.getName());
			final var required = required(os);
			os.getProperties().forEach((name, property) ->
				builder.component(
					name,
					typeNameOf(property),
					builder.isStructureSchema(property),
					!required.contains(name) ||
					property.getNullable() != null && property.getNullable()
				)
			);
			builder.build(model);

			return true;
		} else {
			return false;
		}

	}

	private static List<String> required(final ObjectSchema schema) {
		final var required = schema.getRequired();
		return required != null ? List.copyOf(required) : List.of();
	}

}
