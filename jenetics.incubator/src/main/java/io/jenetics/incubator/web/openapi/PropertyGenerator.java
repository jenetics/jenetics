package io.jenetics.incubator.web.openapi;

import static java.util.Objects.requireNonNull;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;

import org.jspecify.annotations.NonNull;

final class PropertyGenerator {
	private String name;
	private Class<?> type;
	private boolean mutable = true;

	PropertyGenerator name(final String name) {
		this.name = requireNonNull(name);
		return this;
	}

	PropertyGenerator type(final Class<?> type) {
		this.type = requireNonNull(type);
		return this;
	}

	PropertyGenerator mutable(final boolean mutable) {
		this.mutable = mutable;
		return this;
	}

	void generate(final JDefinedClass clazz) {
		final var field = clazz.field(JMod.PRIVATE, type, name);

		final var getter = clazz.method(JMod.PUBLIC, type, name);
		getter.body()._return(field);

		if (mutable) {
			final var setter = clazz.method(JMod.PUBLIC, clazz, name);
			setter.annotate(NonNull.class);
			final var parameter = setter.param(type, name);
			setter.body()
				.assign(JExpr._this().ref(field), parameter)
				._return(JExpr._this());
		}
	}

}
