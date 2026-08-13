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

import com.helger.jcodemodel.JCodeModel;
import io.swagger.v3.oas.models.media.Schema;

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

	private final EnumSchema schema;

	public EnumBuilder(EnumSchema schema) {
		this.schema = requireNonNull(schema);
	}

	@Override
	public void build(final JCodeModel model) {
		requireNonNull(model);

		final var enumClass = enum_(model, schema.name());
		new EnumBodyBuilder(schema, enumClass).build(model);
	}

}
