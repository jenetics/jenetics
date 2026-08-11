package io.jenetics.incubator.web.openapi.codegenerator;

import static java.util.Objects.requireNonNull;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Optional;

public record TypedValueModel(Qname name, Qname type)
	implements SchemaModel
{

	public static Optional<TypedValueModel> of(final Schema<?> schema) {
		requireNonNull(schema);

		return Optional.empty();
	}

}
