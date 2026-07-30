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

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;
import io.jenetics.incubator.web.openapi.Generator;
import io.jenetics.incubator.web.openapi.Main;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class TypesGenerator extends Generator {

	private static final List<SchemaTypeBuilder> BUILDERS = List.of(
		EnumBuilder::build
	);

	private final OpenAPI api;

	TypesGenerator(final OpenAPI api, final JCodeModel model, String pkg) {
		super(model);
		this.api = requireNonNull(api);

		api.getComponents().getSchemas().forEach((name, schema) ->
			schema.setName("%s.%s".formatted(pkg, name))
		);
	}

	void generate() {
		api.getComponents().getSchemas().forEach((_, schema) -> {
			switch (schema) {
				case ObjectSchema os -> schema(os);
				//case StringSchema ss -> schema(ss);
				case Schema<?> s -> BUILDERS.forEach(b -> b.build(s, model));
			}
		});
	}

	private void schema(final ObjectSchema schema) {
		final var generator = new StructuralTypeBuilder(
			model,
			interface_(schema.getName())
		);

		schema.getProperties().forEach(generator::component);
	}

//	private void schema(final StringSchema schema) {
//		EnumBuilder.of(model, schema)
//			.ifPresentOrElse(
//				g -> schema.getEnum().forEach(g::constant),
//				() -> TypedValueGenerator.of(model, schema)
//			);
//	}

	private void schema(final Schema<?> schema) {
		TypedValueGenerator.of(model, schema);
	}


	// /////////////////////////////////////////////////////////////////////////

	static void main() throws IOException {
		final var api = read("/museum-api.yaml");
		final var model = new JCodeModel();

		new TypesGenerator(api, model, "com.museum.model")
			.generate();

		var writer = new JCMWriter(model);
		writer.build(new OutputStreamCodeWriter(System.out, Charset.defaultCharset()));
	}

	static OpenAPI read(final String name) throws IOException {
		final var input = Main.class.getResourceAsStream(name);
		final var parser = new OpenAPIV3Parser();
		final var api = new String(input.readAllBytes());
		return parser.readContents(api).getOpenAPI();
	}

}
