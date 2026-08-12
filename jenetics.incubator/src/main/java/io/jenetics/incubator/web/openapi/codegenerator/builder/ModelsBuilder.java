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

import static io.jenetics.incubator.web.openapi.codegenerator.Context.api;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.jenetics.incubator.web.openapi.codegenerator.CodeBuilder;
import io.jenetics.incubator.web.openapi.codegenerator.Context;
import io.jenetics.incubator.web.openapi.codegenerator.model.TypedSchema;

/**
 * Code builder for the schemas of an OpenAPI specification.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public class ModelsBuilder {

	private static final List<CodeBuilder> BUILDERS = List.of(
		StructuralTypeBuilder::build,
		EnumModelBuilder::build,
		TypedValueBuilder::build
	);

	/**
	 * Create a new enum code builder.
	 */
	public ModelsBuilder() {
	}

	/**
	 * Builds the model classes class and adds it to the {@code model}.
	 *
	 * @param model the model classes is build and added to
	 */
	public void build(final JCodeModel model) {
		api().getComponents().getSchemas().values().stream()
			.map(TypedSchema::of)
			.forEach(schema -> BUILDERS.forEach(b -> b.build(schema, model)));
	}

	// /////////////////////////////////////////////////////////////////////////

	static void main() throws IOException {
		final var api = read("/museum-api.yaml");
		api.getComponents().getSchemas()
			.forEach((name, schema) -> schema.setName(name));

		final var buildDir = Path.of("./jenetics.incubator/build/generated/sources/openapi/");
		Files.createDirectories(buildDir);
		final var model = new JCodeModel();

		new Context(api, "com.museum.model").run(() ->
			new ModelsBuilder()
				.build(model)
		);

		var writer = new JCMWriter(model);
		writer.setCharset(Charset.defaultCharset());
		writer.build(buildDir.toFile());
	}

	static OpenAPI read(final String name) throws IOException {
		final var input = ModelsBuilder.class.getResourceAsStream(name);
		final var parser = new OpenAPIV3Parser();
		final var api = new String(input.readAllBytes());
		return parser.readContents(api).getOpenAPI();
	}

}
