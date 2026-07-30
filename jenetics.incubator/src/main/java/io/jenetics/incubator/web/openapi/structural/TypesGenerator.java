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

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 9.1
 * @version 9.1
 */
public class TypesGenerator  {

	private static final List<SchemaTypeBuilder> BUILDERS = List.of(
		StructuralTypeBuilder::build,
		EnumBuilder::build
	);

	private final OpenAPI api;
	private final JCodeModel model;

	TypesGenerator(final OpenAPI api, final JCodeModel model, String pkg) {
		this.api = requireNonNull(api);
		this.model = requireNonNull(model);

		api.getComponents().getSchemas().forEach((name, schema) ->
			schema.setName("%s.%s".formatted(pkg, name))
		);
	}

	void generate() {
		api.getComponents().getSchemas()
			.forEach((_, schema) -> BUILDERS
				.forEach(b -> b.build(schema, model)));
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
		final var input = TypesGenerator.class.getResourceAsStream(name);
		final var parser = new OpenAPIV3Parser();
		final var api = new String(input.readAllBytes());
		return parser.readContents(api).getOpenAPI();
	}

}
