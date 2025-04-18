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
package io.jenetics.incubator.metamodel.type;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a <em>structural</em> type like a record or bean class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public sealed interface StructType
	extends MetaModelType
	permits BeanType, RecordType
{

	final class PropertyType {

		@FunctionalInterface
		public interface Getter {

			/**
			 * Return the property value from a given <em>parent</em> {@code object}.
			 *
			 * @param object the object from which the property is read
			 * @return the property value
			 */
			Object get(final Object object);

		}

		@FunctionalInterface
		public interface Setter {

			/**
			 * Sets the property {@code value} to the given <em>parent</em> {@code object}.
			 *
			 * @param object the object for which the property is set
			 * @param value the new property value
			 */
			void set(final Object object, Object value);

		}

		private final String name;
		private final StructType enclosure;
		private final Type type;
		private final Getter getter;
		private final Setter setter;

		PropertyType(
			final String name,
			final StructType enclosure,
			final Type type,
			final Getter getter,
			final Setter setter
		) {
			this.name = requireNonNull(name);
			this.enclosure = requireNonNull(enclosure);
			this.type = requireNonNull(type);
			this.getter = requireNonNull(getter);
			this.setter = setter;
		}

		public String name() {
			return name;
		}

		public StructType enclosure() {
			return enclosure;
		}

		public Type type() {
			return type;
		}

		public Getter getter() {
			return getter;
		}

		public Optional<Setter> setter() {
			return Optional.ofNullable(setter);
		}

		public Object get(final Object object) {
			if (enclosure.type() instanceof Class<?> cls &&
				!cls.isAssignableFrom(object.getClass()))
			{
				throw new IllegalArgumentException("Invalid enclosure: " + enclosure);
			}

			return null;
		}

		/**
		 * Return {@code true} if {@code this} property is mutable.
		 *
		 * @return {@code true} if {@code this} property is mutable
		 */
		public boolean isMutable() {
			return setter != null;
		}

	}

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
