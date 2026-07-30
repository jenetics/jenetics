package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import io.jenetics.incubator.web.openapi.Generator;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.util.Locale;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Generates {@link Enum} class from a
 * {@link io.swagger.v3.oas.models.media.StringSchema} with enum format.
 */
public final class EnumGenerator extends Generator {

	private static final String VALUE_NAME = "value";

	private final JDefinedClass clazz;

	/**
	 * Create a new enum code generator.
	 *
	 * @param model the code model to use
	 * @param name the full qualified enum name
	 */
	public EnumGenerator(final JCodeModel model, final String name) {
		super(model);
		this.clazz = enum_(name);
		createEnumBody();
	}

	private void createEnumBody() {
		final var field = clazz.field(JMod.PRIVATE_FINAL, String.class, VALUE_NAME);

		final var constructor = clazz.constructor(JMod.NONE);
		final var value = constructor.param(String.class, VALUE_NAME);
		constructor.body().assign(JExpr.refthis(field), value);

		final var method = clazz.method(JMod.PUBLIC, String.class, VALUE_NAME);
		method.body()._return(field);

		final var toString = clazz.method(JMod.PUBLIC, String.class, "toString");
		toString.annotate(Override.class);
		toString.body()._return(field);
	}

	/**
	 * Add an enum constant with the given {@code name}. The {@code name} is
	 * converted into upper case letters and name mangling is performed if needed.
	 * The original enum name can be accessed with the {@code value()} method of
	 * the generated enum class.
	 *
	 * @param name the constant name
	 * @return {@code this} generator
	 */
	public EnumGenerator constant(String name) {
		requireNonNull(name);

		clazz.enumConstant(toConstantName(name)).arg(JExpr.lit(name));
		return this;
	}

	private static String toConstantName(final String value) {
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

	public static Optional<EnumGenerator>
	of(final JCodeModel model, final Schema<?> schema) {
		if (schema instanceof StringSchema &&
			schema.getEnum() != null &&
			!schema.getEnum().isEmpty())
		{
			return Optional.of(new EnumGenerator(model, schema.getName()));
		} else {
			return Optional.empty();
		}
	}

}
