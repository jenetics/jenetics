package io.jenetics.incubator.web.openapi.codegenerator.model;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.Context.namespace;
import static io.jenetics.incubator.web.openapi.codegenerator.Schemas.allOf;
import static io.jenetics.incubator.web.openapi.codegenerator.Schemas.deref;
import static io.jenetics.incubator.web.openapi.codegenerator.Schemas.properties;

import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;

/**
 * Boxes a {@code schema} which represents a structural type.
 *
 * @param schema the schema
 * @param name the Java type name of the structural type
 * @param components the component list of the structural type
 */
public record StructuralTypeSchema(
	Schema<?> schema,
	Qname name,
	List<Component> components
)
	implements TypedSchema
{

	public StructuralTypeSchema {
		requireNonNull(schema);
		requireNonNull(name);
		components = List.copyOf(components);
	}

	/**
	 * A component of the structural type.
	 *
	 * @param schema the <em>property</em> schema: {@link Schema#getProperties()}
	 * @param name the component name
	 */
	public record Component(Schema<?> schema, String name) {

		public Component {
			requireNonNull(schema);
			requireNonNull(name);
		}

		/**
		 * Return {@code true} if {@code this} component is nullable.
		 *
		 * @return {@code true} if {@code this} component is nullable,
		 *         {@code false} otherwise
		 */
		public boolean isNullable() {
			final var required = schema.getRequired() != null
				? schema.getRequired()
				: List.of();

			return !required.contains(name) ||
				schema.getNullable() != null &&
					schema.getNullable();
		}

		/**
		 * Return {@code true} if {@code this} component is also a structural
		 * type.
		 *
		 * @return {@code true} if {@code this} component is also a structural
		 * 		   type, {@code false} otherwise
		 */
		public boolean isStructural() {
			if (schema instanceof ObjectSchema) {
				return true;
			} else if (schema.get$ref() != null) {
				return Schemas.ref(schema).orElse(null) instanceof ObjectSchema;
			} else {
				return false;
			}
		}

		/**
		 * Return the qualified name of the type of the component.
		 *
		 * @return the qualified name of the type of the component
		 */
		public Qname type() {
			if (Schemas.isEnum(schema)) {
				return new Qname(capitalize(name));
			} else {
				return Schemas.hasName(schema)
					? Schemas.nameOf(schema)
					: Schemas.nameOf(Schemas.deref(schema.get$ref()).orElse(null));
			}
		}

		private static String capitalize(String str) {
			if (str == null || str.isEmpty()) return str;
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}

	}

	static StructuralTypeSchema of(final Schema<?> schema) {
		return switch (schema) {
			case ObjectSchema _, ComposedSchema _
				when !propertiesOf(schema).isEmpty()-> {
					final var components = propertiesOf(schema).entrySet().stream()
						.map(entry -> new Component(entry.getValue(), entry.getKey()))
						.toList();

					yield new StructuralTypeSchema(
						schema,
						new Qname(namespace(), schema.getName()),
						components
					);
			}
			case null, default -> null;
		};
	}

	private static Map<String, Schema<?>> propertiesOf(Schema<?> schema) {
		return switch (schema) {
			case ObjectSchema s -> properties(s);
			case ComposedSchema cs -> {
				final var schemas = new HashMap<String, Schema<?>>();

				for (var s : allOf(cs)) {
					deref(s.get$ref()).ifPresent(ref -> {
						schemas.putAll(propertiesOf(ref));
						schemas.putAll(properties(s));
					});
				}

				yield Map.copyOf(schemas);
			}
			case null, default -> Map.of();
		};
	}

}
