package io.jenetics.incubator.web.openapi.codegenerator.model;

import io.swagger.v3.oas.models.media.Schema;

public record UnknownSchema(Schema<?> schema) implements ModelSchema {
}
