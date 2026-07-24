package io.jenetics.incubator.web.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

record Api(OpenAPI api) {

	Schema<?> ref(String uri) {
		final var name = uri.substring(uri.lastIndexOf("/") + 1);
		return api.getComponents().getSchemas().get(name);
	}

}
