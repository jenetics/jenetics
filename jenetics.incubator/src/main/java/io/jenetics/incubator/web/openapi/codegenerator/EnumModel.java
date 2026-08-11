package io.jenetics.incubator.web.openapi.codegenerator;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.Context.qnameOf;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public record EnumModel(Qname name, String type, List<Object> constants) {

	public static Optional<EnumModel> of(final Schema<?> schema) {
		requireNonNull(schema);

		if (Schemas.isEnum(schema)) {
			return Optional.of(
				new EnumModel(
					qnameOf(schema),
					valueTypeNameOf(schema),
					schema.getEnum().stream()
						.collect(Collectors.toUnmodifiableList())
				)
			);
		} else {
			return Optional.empty();
		}
	}

	private static String valueTypeNameOf(final Schema<?> schema) {
		return switch (schema) {
			case StringSchema ss -> Schemas.javaTypeNameOf(ss);
			default -> Schemas.javaTypeNameOfPrimitives(schema);
		};
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
