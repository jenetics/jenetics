package io.jenetics.incubator.web.openapi.codegenerator.model;

import io.swagger.v3.oas.models.media.Schema;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;

/**
 * Represents the different types of OpenAPI schemas and wraps the underlying
 * OpenAPI {@link Schema} class.
 */
public sealed interface TypedSchema
	permits StructuralTypeSchema, EnumSchema, TypedValueSchema, GenericSchema
{

	/**
	 * The (Java) type name of {@code this} typed schema.
	 *
	 * @return the typed schema name
	 */
	Qname name();

	/**
	 * The underlying OpenAPI schema.
	 *
	 * @return the underlying OpenAPI schema
	 */
	Schema<?> schema();

	/**
	 * Wraps the given {@code schema} into one of the {@link TypedSchema} boxes.
	 *
	 * @param schema the schema to box
	 * @return the boxed schema
	 */
	static TypedSchema of(final Schema<?> schema) {
		TypedSchema type = EnumSchema.of(schema);
		if (type == null) {
			type = StructuralTypeSchema.of(schema);
		}
		if (type == null) {
			type = TypedValueSchema.of(schema);
		}
		if (type == null) {
			type = new GenericSchema(schema);
		}

		return type;
	}

}
