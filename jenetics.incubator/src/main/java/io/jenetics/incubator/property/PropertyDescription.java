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
package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A {@code PropertyDesc} describes one property that a Java Bean exports or a
 * {@link java.lang.reflect.RecordComponent} in the case of a record class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
record PropertyDescription(
	String name,
	Class<?> type,
	Method getter,
	Method setter
)
	implements Comparable<PropertyDescription>
{

	public PropertyDescription {
		requireNonNull(name);
		requireNonNull(type);
		requireNonNull(getter);
	}

	public boolean isWriteable() {
		return setter != null;
	}

	/**
	 * Read the property value of the given {@code object}.
	 *
	 * @param object the object where the property is declared
	 * @return the property value of the given {@code object}
	 */
	public Object read(final Object object) {
		try {
			return getter.invoke(object);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Tries to write a new value to {@code this} property.
	 *
	 * @param object the object where the property is declared
	 * @param value  the new property value
	 * @return {@code true} if the new property value has been written
	 *         successfully, {@code false} if the property is immutable
	 */
	public boolean write(final Object object, final Object value) {
		try {
			if (setter != null) {
				setter.invoke(object, value);
				return true;
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

		return false;
	}

	@Override
	public int compareTo(final PropertyDescription o) {
		return name.compareTo(o.name);
	}

}
