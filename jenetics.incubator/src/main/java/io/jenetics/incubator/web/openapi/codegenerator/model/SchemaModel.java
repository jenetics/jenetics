package io.jenetics.incubator.web.openapi.codegenerator.model;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Optional;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;

public sealed interface SchemaModel
	permits EnumModel, StructuralTypeModel, TypedValueModel
{
	Qname name();

	static Optional<SchemaModel> of(final Schema<?> schema) {
		Optional<? extends SchemaModel> model = EnumModel.of(schema);
		if (model.isEmpty()) {
			model = StructuralTypeModel.of(schema);
		}
		if (model.isEmpty()) {
			model = TypedValueModel.of(schema);
		}

		@SuppressWarnings("unchecked")
		final var result = (Optional<SchemaModel>)model;
		return result;
	}

}
