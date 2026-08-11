package io.jenetics.incubator.web.openapi.codegenerator;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Optional;

public interface CodeBuilderFactory {
	Optional<? extends CodeBuilder> create(final Schema<?> schema);
}
