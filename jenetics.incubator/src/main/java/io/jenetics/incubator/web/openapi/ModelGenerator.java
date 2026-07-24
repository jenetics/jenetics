package io.jenetics.incubator.web.openapi;

import com.helger.jcodemodel.JCodeModel;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;

import static java.util.Objects.requireNonNull;

final class ModelGenerator extends Generator {

	private final String pkg;

	ModelGenerator(final OpenAPI api, final JCodeModel model, String pkg) {
		super(api, model);
		this.pkg = requireNonNull(pkg);

		api.getComponents().getSchemas().forEach((name, schema) ->
			schema.setName("%s.%s".formatted(pkg, name))
		);
	}

	void generate() {
		api.getComponents().getSchemas().forEach((_, schema) -> {
			switch (schema) {
				case ObjectSchema os -> schema(os);
				case StringSchema ss -> schema(ss);
				default -> {}
			}
		});
	}

	private ModelGenerator schema(final ObjectSchema schema) {
		final var generator = new ModelClassGenerator(
			api, model,
			class_(schema.getName())
		);

		schema.getProperties().forEach(generator::property);
		generator.equalsAndHashCode();

		return this;
	}

	private ModelGenerator schema(final StringSchema schema) {
		if (schema.getEnum() != null && !schema.getEnum().isEmpty()) {
			final var generator = new ModelEnumGenerator(
				api, model,
				enum_(schema.getName())
			);
			schema.getEnum().forEach(generator::constant);
		}

		return this;
	}

}
