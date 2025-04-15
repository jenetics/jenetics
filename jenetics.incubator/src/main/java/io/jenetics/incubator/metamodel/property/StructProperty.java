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
package io.jenetics.incubator.metamodel.property;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.reflect.PropertyType;
import io.jenetics.incubator.metamodel.reflect.StructType;

/**
 * Represents a <em>struct</em> property, like records or beans.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public sealed abstract class StructProperty
	extends SimpleProperty
	permits BeanProperty, RecordProperty
{

	/**
	 * The components of a struct property.
	 *
	 * @param name the property name
	 * @param value the property value
	 */
	public record Component(String name, Object value) {
		public Component {
			requireNonNull(name);
		}
	}

	StructProperty(final PropParam param) {
		super(param);
	}

	/**
	 * Returns the components of the property.
	 *
	 * @return the struct components
	 */
	public Stream<Component> components() {
		return PropertyType.of(type()) instanceof StructType st
			? st.components().map(component -> new Component(
					component.name(),
					read(component.getter())
				))
			: Stream.empty();
	}

	private Object read(final Method method) {
		try {
			return value() != null ? method.invoke(value()) : null;
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

}
