package io.jenetics.incubator.web.openapi.codegenerator;

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
import java.util.Optional;

public record StructuralTypeModel(Qname name, List<Component> components)
	implements SchemaModel
{

	public record Component(
		String name,
		Qname type,
		boolean nullable,
		boolean structural
	) {
	}

	public static Optional<StructuralTypeModel> of(final Schema<?> schema) {
		requireNonNull(schema);

		return switch (schema) {
			case ObjectSchema _, ComposedSchema _ when !propertiesOf(schema).isEmpty()-> {
				final var required = required(schema);

				final var components = propertiesOf(schema).entrySet().stream()
					.map(entry -> {
						final String name = entry.getKey();
						final Schema<?> property = entry.getValue();
						final Qname type = Schemas.hasName(property)
							? Schemas.nameOf(property)
							: Schemas.nameOf(Schemas.deref(property.get$ref()).orElse(null));
						final boolean nullable = !required.contains(name) ||
							property.getNullable() != null && property.getNullable();

						return new Component(name, type, nullable, isStructureSchema(property));
					})
					.toList();

				yield Optional.of(
					new StructuralTypeModel(
						new Qname(namespace(), schema.getName()),
						components
					)
				);
			}
			default -> Optional.empty();
		};
	}

	public static Map<String, Schema<?>> propertiesOf(Schema<?> schema) {
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

	private static List<String> required(final Schema<?> schema) {
		if (schema == null) return List.of();
		final var required = schema.getRequired();
		return required != null ? List.copyOf(required) : List.of();
	}

	private static boolean isStructureSchema(Schema<?> schema) {
		if (schema instanceof ObjectSchema) {
			return true;
		} else if (schema.get$ref() != null) {
			return Schemas.ref(schema).orElse(null) instanceof  ObjectSchema;
		} else {
			return false;
		}
	}

}
