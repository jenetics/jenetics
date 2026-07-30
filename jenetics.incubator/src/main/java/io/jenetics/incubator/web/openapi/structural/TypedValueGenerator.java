package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import io.jenetics.incubator.web.openapi.Generator;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class TypedValueGenerator extends Generator {

	private final JDefinedClass clazz;

	public TypedValueGenerator(
		final JCodeModel model,
		final String name,
		final String type
	) {
		super(model);
		this.clazz = record_(name);

		final var valueType = model.parseType(type);
		this.clazz.recordComponent(valueType, "value");

		if (valueType.isReference()) {
			this.clazz.compactConstructor(JMod.PUBLIC).body().add(
				model.ref(Objects.class)
					.staticInvoke("requireNonNull")
					.arg(JExpr.ref("value"))
			);
		}
	}

	public static Optional<TypedValueGenerator>
	of(final JCodeModel model, final Schema<?> schema) {
		return switch (schema) {
			case NumberSchema ns -> Optional.of(
				new TypedValueGenerator(
					model,
					schema.getName(),
					switch (ns.getFormat()) {
						case "float" -> "float";
						case "double" -> "double";
						case "int32" -> "int";
						case "int64" -> "long";
						default -> BigDecimal.class.getName();
					}
				)
			);
			case StringSchema _ -> Optional.of(
				new TypedValueGenerator(
					model,
					schema.getName(),
					String.class.getName()
				)
			);
			case DateSchema _ -> Optional.of(
				new TypedValueGenerator(
					model,
					schema.getName(),
					LocalDate.class.getName()
				)
			);
			default -> Optional.empty();
		};
	}

}
