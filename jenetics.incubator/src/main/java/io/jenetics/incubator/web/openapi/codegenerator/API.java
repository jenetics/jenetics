package io.jenetics.incubator.web.openapi.codegenerator;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class API {
	private API() {
	}

	public static OpenAPI read(final Path path) throws IOException {
		try (var input = Files.newInputStream(path)) {
			return read(input);
		}
	}

	public static OpenAPI read(final InputStream input) throws IOException {
		final var string = new String(input.readAllBytes());
		final var parser = new OpenAPIV3Parser();
		final var api = parser.readContents(string).getOpenAPI();

		api.getComponents().getSchemas()
			.forEach((name, schema) -> schema.setName(name));

		return api;
	}

	public static OpenAPI readResource(final String name) throws IOException {
		return read(API.class.getResourceAsStream(name));
	}

}
