package io.jenetics.incubator.web.openapi.codegenerator.model;

import static java.util.Objects.requireNonNull;

import io.swagger.v3.oas.models.media.Schema;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.Schemas;

public record EnumSchema(Schema<?> schema, Qname name, Qname type, List<Object> constants)
	implements TypedSchema
{

	public static Optional<EnumSchema> of(final Schema<?> schema) {
		requireNonNull(schema);

		if (Schemas.isEnum(schema)) {
			return Optional.of(
				new EnumSchema(
					schema,
					Schemas.nameOf(schema),
					Schemas.typeOf(schema),
					schema.getEnum().stream()
						.collect(Collectors.toUnmodifiableList())
				)
			);
		} else {
			return Optional.empty();
		}
	}

	public static String toConstantName(final String value) {
		final var name = new StringBuilder();
		final var text = value.toUpperCase(Locale.ROOT);

		for (int i = 0; i < text.length();) {
			final var cp = text.codePointAt(i);
			i += Character.charCount(cp);

			if (Character.isJavaIdentifierPart(cp)) {
				if (name.isEmpty() && !Character.isJavaIdentifierStart(cp)) {
					name.append('_');
				}
				name.appendCodePoint(cp);
			} else if (!name.isEmpty() && name.charAt(name.length() - 1) != '_') {
				name.append('_');
			}
		}

		while (!name.isEmpty() && name.charAt(name.length() - 1) == '_') {
			name.setLength(name.length() - 1);
		}

		return name.isEmpty() ? "VALUE" : name.toString();
	}

}
