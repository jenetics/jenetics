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
import java.util.Optional;
import java.util.stream.Collectors;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;

public record StructuralTypeModel(Schema<?> schema, Qname name, List<Component> components)
	implements SchemaModel
{

	public record Component(
		Schema<?> schema,
		String name
	) {

		public boolean isNullable() {
			final var required = schema.getRequired() != null
				? schema.getRequired()
				: List.of();

			return !required.contains(name) ||
				schema.getNullable() != null &&
					schema.getNullable();
		}

		public boolean isStructural() {
			if (schema instanceof ObjectSchema) {
				return true;
			} else if (schema.get$ref() != null) {
				return Schemas.ref(schema).orElse(null) instanceof ObjectSchema;
			} else {
				return false;
			}
		}

		public Optional<EnumModel> enumModel() {
			if (Schemas.isEnum(schema)) {
				return Optional.of(
					new EnumModel(
						schema,
						new Qname(capitalize(name)),
						Schemas.typeOf(schema),
						schema.getEnum().stream()
							.collect(Collectors.toUnmodifiableList())
					)
				);
			} else {
				return Optional.empty();
			}
		}

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

	public static Optional<StructuralTypeModel> of(final Schema<?> schema) {
		requireNonNull(schema);

		return switch (schema) {
			case ObjectSchema _, ComposedSchema _ when !propertiesOf(schema).isEmpty()-> {
				final var components = propertiesOf(schema).entrySet().stream()
					.map(entry -> {
						final String name = entry.getKey();
						final Schema<?> property = entry.getValue();

						final Qname type = Schemas.hasName(property)
							? Schemas.nameOf(property)
							: Schemas.nameOf(Schemas.deref(property.get$ref()).orElse(null));

						return new Component(property, name);
					})
					.toList();

				yield Optional.of(
					new StructuralTypeModel(
						schema,
						new Qname(namespace(), schema.getName()),
						components
					)
				);
			}
			default -> Optional.empty();
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
