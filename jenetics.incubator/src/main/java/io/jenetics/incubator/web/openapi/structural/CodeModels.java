package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.exceptions.JCodeModelException;

import io.jenetics.incubator.web.openapi.GenerationException;

public final class CodeModels {
	private CodeModels() {
	}


	public static JDefinedClass
	interface_(final JCodeModel model, final String name) {
		try {
			return model._class(name, EClassType.INTERFACE);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

	public static JDefinedClass
	class_(final JCodeModel model, final String name) {
		try {
			return model._class(name, EClassType.CLASS);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

	public static JDefinedClass
	enum_(final JCodeModel model, final String name) {
		try {
			return model._class(name, EClassType.ENUM);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}

	public static JDefinedClass
	record_(final JCodeModel model, final String name) {
		try {
			return model._class(name, EClassType.RECORD);
		} catch (JCodeModelException e) {
			throw new GenerationException(e);
		}
	}
}
