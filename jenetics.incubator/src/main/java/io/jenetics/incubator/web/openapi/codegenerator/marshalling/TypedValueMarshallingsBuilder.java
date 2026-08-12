package io.jenetics.incubator.web.openapi.codegenerator.marshalling;

import com.helger.jcodemodel.JCodeModel;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.internal.JCodeModels;

public class TypedValueMarshallingsBuilder {

	private Qname name;
	private String type;

	public TypedValueMarshallingsBuilder() {
	}

	public TypedValueMarshallingsBuilder name(final Qname name) {
		this.name = name;
		return this;
	}

	public TypedValueMarshallingsBuilder type(final String type) {
		this.type = type;
		return this;
	}

	public void build(final JCodeModel model) {
		final var clazz = JCodeModels.class_(model, name);
		clazz.generify(type);
	}

	/*
	public static boolean build(final Schema<?> schema, final JCodeModel model) {
		requireNonNull(schema);
		requireNonNull(model);

		if (isEnum(schema)) {
			return false;
		}

		final var type = Schemas.javaTypeNameOfPrimitives(schema);
		if (type != null) {
			new TypedValueMarshallingsBuilder()
				.type(schema.getName())
				.build(model);

			return true;
		} else {
			return false;
		}
	}
	 */

}
