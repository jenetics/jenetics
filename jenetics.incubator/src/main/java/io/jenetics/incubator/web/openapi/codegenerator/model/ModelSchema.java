package io.jenetics.incubator.web.openapi.codegenerator.model;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Optional;

public sealed interface ModelSchema
	permits StructuralTypeSchema, EnumSchema, TypedValueSchema, UnknownSchema
{
	Schema<?> schema();

	static Optional<ModelSchema> of(final Schema<?> schema) {
		Optional<? extends ModelSchema> model = EnumSchema.of(schema);
		if (model.isEmpty()) {
			model = StructuralTypeSchema.of(schema);
		}
		if (model.isEmpty()) {
			model = TypedValueSchema.of(schema);
		}

		@SuppressWarnings("unchecked")
		final var result = (Optional<ModelSchema>)model;
		return result;
	}

}
