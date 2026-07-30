package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;

import static java.util.Objects.requireNonNull;

final class ComponentGenerator {
	private final String name;
	private final AbstractJType type;

	ComponentGenerator(
		final String name,
		final AbstractJType type
	) {
		this.name = requireNonNull(name);
		this.type = requireNonNull(type);
	}

	void generate(final JDefinedClass clazz) {
		clazz.method(JMod.PUBLIC, type, name);
	}

}
