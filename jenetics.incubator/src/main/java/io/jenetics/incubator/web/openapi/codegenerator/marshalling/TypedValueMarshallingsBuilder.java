package io.jenetics.incubator.web.openapi.codegenerator.marshalling;

import io.jenetics.incubator.web.openapi.codegenerator.Qname;

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

//	public static void build(TypedValueSchema schema, final JCodeModel model) {
//		final var clazz = JCodeModels.class_(model, schema.name());
//
//		var jackson = model._class(JsonSerializer.class.getName());
//
//		clazz._extends(jackson)
//		clazz.generify(schema.type().toString());
//	}

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
