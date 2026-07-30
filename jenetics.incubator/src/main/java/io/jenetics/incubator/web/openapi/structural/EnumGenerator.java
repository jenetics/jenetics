package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import io.jenetics.incubator.web.openapi.Generator;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.util.Optional;

/**
 * Generates {@link Enum} class from a
 * {@link io.swagger.v3.oas.models.media.StringSchema} with enum format.
 */
public final class EnumGenerator extends Generator {

	private final JDefinedClass clazz;

	public EnumGenerator(final JCodeModel model, final String name) {
		super(model);
		this.clazz = enum_(name);
	}

	public EnumGenerator constant(String name) {
		clazz.enumConstant(name);
		return this;
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
