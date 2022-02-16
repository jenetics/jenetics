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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * A {@code PropertyDesc} describes one property that a Java Bean exports or a
 * {@link java.lang.reflect.RecordComponent} in the case of a record class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
record PropertyDesc(
	Class<?> type,
	String name,
	Method getter,
	Method setter
)
	implements Comparable<PropertyDesc>
{
	PropertyDesc {
		requireNonNull(type);
		requireNonNull(name);
		requireNonNull(getter);
	}

	/**
	 * Read the property value of the given {@code object}.
	 *
	 * @param object the object where the property is declared
	 * @return the property value of the given {@code object}
	 */
	Object read(final Object object) {
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
	 * successfully, {@code false} if the property is immutable
	 */
	boolean write(final Object object, final Object value) {
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
	public int compareTo(final PropertyDesc o) {
		return name.compareTo(o.name);
	}

	/**
	 * Return a stream of property descriptions for the given {@code type}.
	 *
	 * @param type the type to be analyzed
	 * @return a stream of property descriptions for the given {@code type}
	 */
	static Stream<PropertyDesc> stream(final Class<?> type) {
		final Stream<PropertyDesc> result;

		if (type.isRecord()) {
			result = Stream.of(type.getRecordComponents())
				.map(cmp ->
					new PropertyDesc(
						cmp.getType(),
						cmp.getName(),
						cmp.getAccessor(),
						null
					)
				);
		} else {
			try {
				final PropertyDescriptor[] descriptors = Introspector
					.getBeanInfo(type)
					.getPropertyDescriptors();

				result = Stream.of(descriptors)
					.filter(desc -> desc.getPropertyType() != Class.class)
					.filter(desc -> desc.getReadMethod() != null)
					.map(desc ->
						new PropertyDesc(
							desc.getPropertyType(),
							desc.getName(),
							desc.getReadMethod(),
							desc.getWriteMethod()
						)
					);
			} catch (IntrospectionException e) {
				throw new IllegalArgumentException("Can't introspect Object.", e);
			}
		}

		return result.sorted();
	}

}
