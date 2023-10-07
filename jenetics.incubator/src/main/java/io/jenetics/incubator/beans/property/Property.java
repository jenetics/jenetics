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
package io.jenetics.incubator.beans.property;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.description.Getter;
import io.jenetics.incubator.beans.description.Setter;

/**
 * Represents an object's property. A property might be defined as usual
 * <em>bean</em> property, with getter and setter, or as record component. The
 * following code shows how to create (a transitive) list of all properties from
 * a given root object.
 * {@snippet lang="java":
 * final var root = null; // @replace substring='null' replacement="..."
 * final List<Property> properties = Properties
 *     // Get all properties from the 'root' object which are defined
 *     // in the 'io.jenetics' package.
 *     .stream(root, "io.jenetics")
 *     .toList();
 * }
 * Only get string properties.
 * {@snippet lang="java":
 * final List<Property> properties = Properties
 *     .stream(root, "io.jenetics")
 *     .filter(property -> property.type() == String.class)
 *     .toList();
 * }
 * Only get the properties declared in the {@code MyBeanObject} class.
 * {@snippet lang="java":
 * final List<Property> properties = Properties
 *     .stream(root, "io.jenetics")
 *     .filter(property -> property.object().getClass() == MyBeanObject.class)
 *     .toList();
 * }
 * Only get properties with the name {@code index}. No matter where they defined
 * in the object hierarchy.
 * {@snippet lang="java":
 * final List<Property> properties = Properties
 *     .stream(root, "io.jenetics")
 *     .filter(Property.pathMatcher("**index"))
 *     .toList();
 * }
 * Updates all "index" properties with value {@code -1} to zero and returns all
 * properties, which couldn't be updated, because the property was immutable.
 * {@snippet lang="java":
 * final List<Property> notUpdated = Properties
 *     .stream(root, "io.jenetics")
 *     .filter(Property.pathMatcher("**index"))
 *     .filter(property -> Objects.equals(property.value(), -1))
 *     .filter(property -> !property.write(0))
 *     .toList();
 * assert notUpdated.isEmpty();
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public sealed interface Property
	extends PathValue<Property.Value>
	permits IndexedProperty, IndexProperty, SimpleProperty
{
	/**
	 * The value type for the property. It contains the information about the
	 * value, type of the property and the enclosure value.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 7.2
	 * @since 7.2
	 */
	sealed interface Value {

		/**
		 * Returns the object which contains {@code this} node.
		 *
		 * @return the object which contains {@code this} node
		 */
		Object enclosure();

		/**
		 * The value of the metaobject, may be {@code null}. This method always
		 * returns the initial property value.
		 *
		 * @return the <em>original</em> value of the metaobject
		 */
		Object value();

		/**
		 * The type of the property value, never {@code null}.
		 *
		 * @return the type of the property value
		 */
		Class<?> type();

		/**
		 * Reads the actual value of the property. This value may be
		 * different from the initial, cached {@link #value()}.
		 *
		 * @return the actual value of the property
		 */
		default Object read() {
			return value();
		}

		/**
		 * Represents an <em>immutable</em> property value.
		 *
		 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
		 * @version 7.2
		 * @since 7.2
		 */
		record Immutable(Object enclosure, Object value, Class<?> type)
			implements Value
		{
			@Override
			public String toString() {
				return "Immutable[value=%s, type=%s, enclosureType=%s]".formatted(
					value(),
					type().getName(),
					enclosure().getClass().getName()
				);
			}
		}

		/**
		 * Represents a <em>mutable</em> property value.
		 *
		 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
		 * @version 7.2
		 * @since 7.2
		 */
		final class Mutable implements Value {

			private final Object enclosure;
			private final Object value;
			private final Class<?> type;
			private final Getter getter;
			private final Setter setter;

			Mutable(
				final Object enclosure,
				final Object value,
				final Class<?> type,
				final Getter getter,
				final Setter setter
			) {
				this.enclosure = requireNonNull(enclosure);
				this.value = value;
				this.type = requireNonNull(type);
				this.getter = requireNonNull(getter);
				this.setter = requireNonNull(setter);
			}

			@Override
			public Object enclosure() {
				return enclosure;
			}

			@Override
			public Object value() {
				return value;
			}

			@Override
			public Class<?> type() {
				return type;
			}

			@Override
			public Object read() {
				return getter.get(enclosure);
			}

			/**
			 * Writes a new value to the property.
			 *
			 * @param value the new property value
			 * @return {@code true} if the new value has been written,
			 *         {@code false} otherwise
			 */
			public boolean write(final Object value) {
				try {
					setter.set(enclosure, value);
					return true;
				} catch (VirtualMachineError|LinkageError e) {
					throw e;
				} catch (Throwable e) {
					return false;
				}
			}

			@Override
			public int hashCode() {
				return Objects.hash(enclosure, value, type);
			}

			@Override
			public boolean equals(final Object obj) {
				return obj == this ||
					obj instanceof Mutable m &&
					Objects.equals(enclosure, m.enclosure) &&
					Objects.equals(value, m.value) &&
					Objects.equals(type, m.type);
			}

			@Override
			public String toString() {
				return "Mutable[value=%s, type=%s, enclosureType=%s]".formatted(
					value(),
					type().getName(),
					enclosure().getClass().getName()
				);
			}

		}
	}

}

