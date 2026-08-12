package io.jenetics.incubator.web.openapi.codegenerator.model;

import static io.jenetics.incubator.web.openapi.codegenerator.Context.namespace;
import static io.jenetics.incubator.web.openapi.codegenerator.Schemas.isEnum;

import io.swagger.v3.oas.models.media.Schema;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;

public record TypedValueSchema(Schema<?> schema, Qname name, Qname type)
	implements TypedSchema
{

	static TypedValueSchema of(final Schema<?> schema) {
		if (schema == null || isEnum(schema)) {
			return null;
		}

		final var name = Schemas.nameOf(schema);
		if (name.isJavaName()) {
			return new TypedValueSchema(
				schema,
				new Qname(namespace(), schema.getName()),
				name
			);
		} else {
			return null;
		}
	}

}
