package io.jenetics.incubator.web.openapi.codegenerator.model;

import io.swagger.v3.oas.models.media.Schema;

public record NoModel(Schema<?> schema) implements SchemaModel {
}
