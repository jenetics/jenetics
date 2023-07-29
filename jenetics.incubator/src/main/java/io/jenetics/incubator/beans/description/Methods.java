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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Methods {
	private Methods() {
	}

	static Getter toGetter(final Method method) {
		return object -> {
			try {
				method.setAccessible(true);
				return method.invoke(object);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalArgumentException(e);
			}
		};
	}

	static Setter toSetter(final Method method) {
		return (object, value) -> {
			try {
				if (method != null) {
					method.setAccessible(true);
					method.invoke(object, value);
				}
			} catch (IllegalAccessException ignore) {
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof RuntimeException re) {
					throw re;
				} else {
					throw new IllegalStateException(e.getTargetException());
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid argument: " + value, e);
			}
		};
	}

}