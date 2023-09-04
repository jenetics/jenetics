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
package io.jenetics.incubator.beans.description;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Conversion methods for converting {@link Method} objects to getter und
 * setter functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
final class Methods {
	private Methods() {
	}

	static Getter toGetter(final Method method) {
		requireNonNull(method);
		return object -> invoke(method, object);
	}

	static Setter toSetter(final Method method) {
		return method != null
			? (object, value) -> invoke(method, object, value)
			: null;
	}

	private static Object invoke(
		final Method method,
		final Object object,
		final Object... value
	) {
		try {
			method.setAccessible(true);
			return method.invoke(object, value);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException re) {
				throw re;
			} else {
				throw new IllegalStateException(e.getTargetException());
			}
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

}
