package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;
import io.jenetics.incubator.web.openapi.Generator;
import io.jenetics.incubator.web.openapi.Main;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.IOException;
import java.nio.charset.Charset;

import static java.util.Objects.requireNonNull;

public class TypesGenerator extends Generator {
	private final OpenAPI api;
	private final String pkg;

	TypesGenerator(final OpenAPI api, final JCodeModel model, String pkg) {
		super(model);
		this.api = requireNonNull(api);
		this.pkg = requireNonNull(pkg);

		api.getComponents().getSchemas().forEach((name, schema) ->
			schema.setName("%s.%s".formatted(pkg, name))
		);
	}

	void generate() {
		api.getComponents().getSchemas().forEach((_, schema) -> {
			switch (schema) {
				case ObjectSchema os -> schema(os);
				case StringSchema ss -> schema(ss);
				default -> {}
			}
		});
	}

	private TypesGenerator schema(final ObjectSchema schema) {
		final var generator = new StructuralTypeGenerator(
			model,
			interface_(schema.getName())
		);

		schema.getProperties().forEach(generator::component);
		return this;
	}

	private TypesGenerator schema(final StringSchema schema) {
		EnumGenerator.of(model, schema)
			.ifPresent(g -> schema.getEnum().forEach(g::constant));

		return this;
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
