package io.jenetics.incubator.web.openapi;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import io.swagger.v3.oas.models.OpenAPI;

import static java.util.Objects.requireNonNull;

final class ModelEnumGenerator extends Generator {

	private final JDefinedClass clazz;

	ModelEnumGenerator(
		final OpenAPI api,
		final JCodeModel model,
		final JDefinedClass clazz
	) {
		super(api, model);
		this.clazz = requireNonNull(clazz);
	}

	ModelEnumGenerator constant(String name) {
		clazz.enumConstant(name);
		return this;
	}

}
