package io.jenetics.incubator.web.openapi;

import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import io.swagger.v3.oas.models.OpenAPI;

import static java.util.Objects.requireNonNull;

abstract class Generator {

	final OpenAPI api;
	final JCodeModel model;

	protected Generator(final OpenAPI api, final JCodeModel model) {
		this.api = requireNonNull(api);
		this.model = requireNonNull(model);
	}

	JDefinedClass interface_(final String name) {
		try {
			return model._class(name, EClassType.INTERFACE);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

	JDefinedClass class_(final String name) {
		try {
			return model._class(name, EClassType.CLASS);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

	JDefinedClass enum_(final String name) {
		try {
			return model._class(name, EClassType.ENUM);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

}
