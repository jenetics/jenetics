package io.jenetics.incubator.web.openapi.codegenerator.builder;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema.toConstantName;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

import io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema;

/**
 * Builds the body of enum schemas.
 */
final class EnumBodyBuilder implements CodeBuilder {

	private static final String VALUE_NAME = "value";

	private final EnumSchema schema;
	private final JDefinedClass clazz;

	EnumBodyBuilder(final EnumSchema schema, final JDefinedClass clazz) {
		this.schema = requireNonNull(schema);
		this.clazz = requireNonNull(clazz);
	}

	@Override
	public void build(final JCodeModel model) {
		requireNonNull(model);

		final var string = model.ref(String.class);
		final var type = model.parseType(schema.type().toString());

		// Create field, which contains the original value of the enum.
		final var field = clazz.field(JMod.PRIVATE_FINAL, type, VALUE_NAME);

		final var constructor = clazz.constructor(JMod.NONE);
		final var value = constructor.param(type, VALUE_NAME);
		constructor.body().assign(JExpr.refthis(field), value);

		final var method = clazz.method(JMod.PUBLIC, type, VALUE_NAME);
		method.body()._return(field);

		// Add the enum constants
		for (final var constant : schema.constants()) {
			clazz.enumConstant(toConstantName(constant))
				.arg(literal(model, type, constant));
		}

		// Override 'toString' method.
		final var toString = clazz.method(JMod.PUBLIC, String.class, "toString");
		toString.annotate(Override.class);
		toString.body()._return(string.staticInvoke("valueOf").arg(field));

		// Implement 'of' factory method.
		final var optional = model.ref(Optional.class);
		final var parse = clazz.method(
			JMod.PUBLIC | JMod.STATIC,
			optional.narrow(clazz),
			"of"
		);
		final var parseValue = parse.param(String.class, VALUE_NAME);
		final var constants = parse.body()
			.forEach(clazz, "constant", JExpr.invoke("values"));
		final var constant = constants.var();
		constants.body()
			._if(
				string.staticInvoke("valueOf").arg(constant.invoke(VALUE_NAME))
					.invoke("equals").arg(parseValue)
					.cor(constant
						.invoke("name")
						.invoke("equals").arg(parseValue))
			)
			._then()
			._return(optional.staticInvoke("of").arg(constant));
		parse.body()._return(optional.staticInvoke("empty"));
	}

	private static IJExpression literal(
		final JCodeModel model,
		final AbstractJType type,
		final Object value
	) {
		return switch (type.fullName()) {
			case "boolean", "java.lang.Boolean" -> JExpr.lit(booleanValue(value));
			case "double", "java.lang.Double" ->
				JExpr.lit(numberValue(value).doubleValue());
			case "float", "java.lang.Float" ->
				JExpr.lit(numberValue(value).floatValue());
			case "int", "java.lang.Integer" ->
				JExpr.lit(numberValue(value).intValue());
			case "long", "java.lang.Long" ->
				JExpr.lit(numberValue(value).longValue());
			case "java.math.BigDecimal" ->
				JExpr._new(model.ref(BigDecimal.class))
					.arg(JExpr.lit(value.toString()));
			case "java.net.URI" ->
				model.ref(URI.class).staticInvoke("create")
					.arg(JExpr.lit(value.toString()));
			case null, default -> JExpr.lit(String.valueOf(value));
		};
	}

	private static Number numberValue(final Object value) {
		return value instanceof Number number
			? number
			: new BigDecimal(value.toString());
	}

	private static boolean booleanValue(final Object value) {
		return value instanceof Boolean bool
			? bool
			: Boolean.parseBoolean(value.toString());
	}

}
