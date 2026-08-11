package io.jenetics.incubator.web.openapi.codegenerator;

import java.util.Optional;

public interface SchemaCodeBuilder {
	Optional<CodeBuilder> newCodeBuilder();
}
