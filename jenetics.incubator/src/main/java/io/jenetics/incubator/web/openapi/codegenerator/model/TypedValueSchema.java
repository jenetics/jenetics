package io.jenetics.incubator.web.openapi.codegenerator.model;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.Context.namespace;
import static io.jenetics.incubator.web.openapi.codegenerator.Schemas.isEnum;

import io.swagger.v3.oas.models.media.Schema;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;

/**
 * Boxes a {@code schema} which represents a <em>typed value</em>. A typed value
 * is a single value which is wrapped into a record, where the name of the record
 * adds additional semantic. A typical example would be
 * {@snippet lang=java:
 * record Meter(Double value) {}
 * }
 * Instead of using the {@code double} value directly in the API, it is wrapped
 * into a {@code Meter} record, which adds the necessary semantic to the value.
 * In the OpenAPI YAML such a typed value is defined as follows:
 * {@snippet :
 *  components:
 *      schemas:
 *          Meter:
 *              type: number
 *              format: double
 * }
 *
 * @param schema the schema
 * @param name the Java type name of the structural type, e.g.
 *             {@code com.acme.model.Meter}
 * @param type the boxed type name, e.g {@code java.lang.Double}
 */
public record TypedValueSchema(Schema<?> schema, Qname name, Qname type)
	implements TypedSchema
{

	public TypedValueSchema {
		requireNonNull(schema);
		requireNonNull(name);
		requireNonNull(type);
	}

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
