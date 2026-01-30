package io.jenetics.incubator.web.openapi;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.IOException;
import java.nio.charset.Charset;

public class Main {

	static void main() throws IOException {
		final var api = read("/museum-api.yaml");
		final var model = new JCodeModel();

		new ModelGenerator(api, model, "com.museum.model")
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
