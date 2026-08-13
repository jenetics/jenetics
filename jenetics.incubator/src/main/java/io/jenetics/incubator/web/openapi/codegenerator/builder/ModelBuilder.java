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

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import io.swagger.v3.oas.models.OpenAPI;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.jenetics.incubator.web.openapi.codegenerator.API;
import io.jenetics.incubator.web.openapi.codegenerator.ApiContext;
import io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.GenericSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.StructuralTypeSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.TypedSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.TypedValueSchema;

/**
 * Code builder for the schemas of an OpenAPI specification.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public final class ModelBuilder implements CodeBuilder {

	private final OpenAPI api;
	private final String namespace;

	public ModelBuilder(final OpenAPI api, final String namespace) {
		this.api = requireNonNull(api);
		this.namespace = requireNonNull(namespace);
	}

	/**
	 * Builds the model classes class and adds it to the {@code model}.
	 *
	 * @param model the model classes is build and added to
	 */
	@Override
	public void build(final JCodeModel model) {
		schemas().forEach(schema -> {
			switch (schema) {
				case EnumSchema s -> new EnumBuilder(s).build(model);
				case TypedValueSchema s -> new TypedValueBuilder(s).build(model);
				case StructuralTypeSchema s -> new StructuralTypeBuilder(s).build(model);
				case GenericSchema _ -> {}
			}
		});
	}

	/**
	 * Return the list of typed schemas available in the OpenAPI spec.
	 *
	 * @return the list of typed OpenAPI schemas
	 */
	public List<TypedSchema> schemas() {
		return new ApiContext(api, namespace).call(() ->
			api.getComponents().getSchemas().values().stream()
				.map(TypedSchema::of)
				.toList()
		);
	}

	/* *************************************************************************
	 * Create the model classes for the example museum API.
	 * ************************************************************************/

	static void main() throws IOException {
		final var api = API.readResource("/museum-api.yaml");

		final var buildDir = Path.of(
			"./jenetics.incubator/build/generated/sources/openapi/"
		);
		Files.createDirectories(buildDir);

		final var model = new JCodeModel();
		new ModelBuilder(api, "com.museum.model").build(model);

		var writer = new JCMWriter(model);
		writer.setCharset(Charset.defaultCharset());
		writer.build(buildDir.toFile());
	}

}
