package io.jenetics.incubator.web.openapi.codegenerator;

import io.swagger.v3.oas.models.media.Schema;

public record Qschema(String name, Schema<?> schema) {
}
