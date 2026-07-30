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
