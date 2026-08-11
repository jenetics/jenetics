package io.jenetics.incubator.web.openapi.codegenerator;

import static java.util.Objects.requireNonNull;

import io.swagger.v3.oas.models.media.Schema;

import java.util.List;
import java.util.Optional;

public record StructuralTypeModel(Qname name, List<Component> components)
	implements SchemaModel
{

	public record Component(
		String name,
		Qname type,
		boolean nullable
	) {
	}

	public static Optional<StructuralTypeModel> of(final Schema<?> schema) {
		requireNonNull(schema);

		return Optional.empty();
	}

}
