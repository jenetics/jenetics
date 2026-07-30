package io.jenetics.incubator.web.openapi;

import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.exceptions.JCodeModelException;

import static java.util.Objects.requireNonNull;

public abstract class Generator {

	protected final JCodeModel model;

	protected Generator(final JCodeModel model) {
		this.model = requireNonNull(model);
	}

	public JDefinedClass interface_(final String name) {
		try {
			return model._class(name, EClassType.INTERFACE);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

	public JDefinedClass class_(final String name) {
		try {
			return model._class(name, EClassType.CLASS);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

	public JDefinedClass enum_(final String name) {
		try {
			return model._class(name, EClassType.ENUM);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

	public JDefinedClass record_(final String name) {
		try {
			return model._class(name, EClassType.RECORD);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

}
