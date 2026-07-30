/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.web.openapi;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

final class PropertyGenerator extends Generator {
	private String name;
	private AbstractJType type;
	private boolean mutable = true;

	PropertyGenerator(JCodeModel model) {
		super(model);
	}

	PropertyGenerator name(final String name) {
		this.name = requireNonNull(name);
		return this;
	}

	PropertyGenerator type(final AbstractJType type) {
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
