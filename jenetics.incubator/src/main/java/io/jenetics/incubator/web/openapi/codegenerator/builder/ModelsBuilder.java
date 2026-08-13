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
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

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
public final class ModelsBuilder implements CodeBuilder {

	private final OpenAPI api;
	private final String namespace;

	public ModelsBuilder(final OpenAPI api, final String namespace) {
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
		new ApiContext(api, namespace).run(() ->
			api.getComponents().getSchemas().values()
				.forEach(schema -> {
					switch (TypedSchema.of(schema)) {
						case EnumSchema s -> new EnumBuilder(s).build(model);
						case TypedValueSchema s -> TypedValueBuilder.build(s, model);
						case StructuralTypeSchema s -> StructuralTypeBuilder.build(s, model);
						case GenericSchema _ -> {}
					}
				})
		);
	}

	// /////////////////////////////////////////////////////////////////////////

	static void main() throws IOException {
		final var api = read("/museum-api.yaml");
		api.getComponents().getSchemas()
			.forEach((name, schema) -> schema.setName(name));

		final var buildDir = Path.of("./jenetics.incubator/build/generated/sources/openapi/");
		Files.createDirectories(buildDir);

		final var model = new JCodeModel();
		new ModelsBuilder(api, "com.museum.model").build(model);

		var writer = new JCMWriter(model);
		writer.setCharset(Charset.defaultCharset());
		writer.build(buildDir.toFile());
	}

	public static OpenAPI read(final String name) throws IOException {
		final var input = ModelsBuilder.class.getResourceAsStream(name);
		final var parser = new OpenAPIV3Parser();
		final var api = new String(input.readAllBytes());
		return parser.readContents(api).getOpenAPI();
	}

}
