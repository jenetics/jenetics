package io.jenetics.incubator.web.openapi.codegenerator.model;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.Context.namespace;
import static io.jenetics.incubator.web.openapi.codegenerator.Schemas.isEnum;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Optional;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;

public record TypedValueSchema(Schema<?> schema, Qname name, Qname type)
	implements TypedSchema
{

	public static Optional<TypedValueSchema> of(final Schema<?> schema) {
		requireNonNull(schema);

		if (isEnum(schema)) {
			return Optional.empty();
		}

		final var name = Schemas.nameOf(schema);
		if (name.isJavaName()) {
			return Optional.of(
				new TypedValueSchema(
					schema,
					new Qname(namespace(), schema.getName()),
					name
				)
			);
		} else {
			return Optional.empty();
		}
	}

}
