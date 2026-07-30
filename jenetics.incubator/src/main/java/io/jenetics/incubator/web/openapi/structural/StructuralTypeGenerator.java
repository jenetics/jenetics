package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import io.jenetics.incubator.web.openapi.Generator;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import static java.util.Objects.requireNonNull;

final class StructuralTypeGenerator extends Generator {
	private final JDefinedClass clazz;

	StructuralTypeGenerator(final JCodeModel model, final JDefinedClass clazz) {
		super(model);
		this.clazz = requireNonNull(clazz);
	}

	StructuralTypeGenerator component(String name, String type) {
		final var generator = new ComponentGenerator(name, model.parseType(type));
		generator.generate(clazz);
		return this;
	}

	StructuralTypeGenerator component(String name, Schema<?> schema) {
		component(name, typeNameOf(schema));
		return this;
	}

	static String typeNameOf(Schema<?> schema) {
		return switch (schema) {
			case NumberSchema _ -> "java.lang.Double";
			case BooleanSchema _ -> "java.lang.Boolean";
			case ArraySchema _ -> "java.util.List<?>";
			case StringSchema ss -> switch (ss.getFormat()) {
				case null, default -> "String";
			};
			case ObjectSchema os -> os.getName();
			case Schema<?> s -> {
				final var ref = s.get$ref();
				if (ref != null) {
					final var index = ref.lastIndexOf("/");
					if (index != -1) {
						yield ref.substring(index + 1);
					} else {
						yield "java.lang.Object";
					}
				} else {
					yield "java.lang.Object";
				}
			}
		};
	}

}
