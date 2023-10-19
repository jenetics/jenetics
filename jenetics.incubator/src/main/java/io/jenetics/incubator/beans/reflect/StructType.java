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
package io.jenetics.incubator.beans.reflect;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Stream;

/**
 * Represents a <em>structural</em> type like a record or bean class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public sealed interface StructType
	extends PropertyType
	permits BeanType, RecordType
{

	/**
	 * Component information for the <em>structural</em> trait
	 *
	 * @param enclosure the enclosing type
	 * @param name the component name
	 * @param value the component type
	 * @param getter the getter method
	 * @param setter the setter method, may be {@code null}
	 */
	record Component(
		Class<?> enclosure,
		String name,
		Type value,
		Method getter,
		Method setter
	) {
		public Component {
			requireNonNull(enclosure);
			requireNonNull(name);
			requireNonNull(value);
			requireNonNull(getter);
		}
	}

	/**
	 * Return the record components of {@code this} struct trait.
	 *
	 * @return the record components of {@code this} struct trait
	 */
	Stream<Component> components();

}
