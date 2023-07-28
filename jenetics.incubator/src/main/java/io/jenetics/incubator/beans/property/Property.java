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

import io.jenetics.incubator.beans.Node;

import java.util.Optional;

/**
 * Represents an object's property. A property might be defined as usual
 * <em>bean</em> property, with getter and setter, or as record component. The
 * following code shows how to create (a transitive) list of all properties from
 * a given root object.
 * <pre>{@code
 * final var root = ...;
 * final List<Property> properties = Properties
 *     // Get all properties from the 'root' object which are defined
 *     // in the 'io.jenetics' package.
 *     .stream(root, "io.jenetics")
 *     .toList();
 * }</pre>
 * Only get string properties.
 * <pre>{@code
 * final List<Property> properties = Properties
 *     .stream(root, "io.jenetics")
 *     .filter(property -> property.type() == String.class)
 *     .toList();
 * }</pre>
 * Only get the properties declared in the {@code MyBeanObject} class.
 * <pre>{@code
 * final List<Property> properties = Properties
 *     .stream(root, "io.jenetics")
 *     .filter(property -> property.object().getClass() == MyBeanObject.class)
 *     .toList();
 * }</pre>
 * Only get properties with the name {@code index}. No matter where they defined
 * in the object hierarchy.
 * <pre>{@code
 * final List<Property> properties = Properties
 *     .stream(root, "io.jenetics")
 *     .filter(Property.pathMatcher("**index"))
 *     .toList();
 * }</pre>
 * Updates all "index" properties with value {@code -1} to zero and returns all
 * properties, which couldn't be updated, because the property was immutable.
 * <pre>{@code
 * final List<Property> notUpdated = Properties
 *     .stream(root, "io.jenetics")
 *     .filter(Property.pathMatcher("**index"))
 *     .filter(property -> Objects.equals(property.value(), -1))
 *     .filter(property -> !property.write(0))
 *     .toList();
 * assert notUpdated.isEmpty();
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public sealed interface Property
	extends Node
	permits IndexedProperty, IndexProperty, SimpleProperty
{

	/**
	 * Returns the object which contains {@code this} property.
	 *
	 * @return the object which contains {@code this} property
	 */
	Object enclosingObject();

	/**
	 * The value of the meta-object, may be {@code null}. This method always
	 * returns the initial property value. If the values have been changed, via
	 * the property {@link #writer()} , this method still returns the
	 * <em>old</em> value. If you want the guaranteed <em>current</em> value,
	 * you have to use the {@link #read()} method.
	 *
	 * @see #read()
	 * @see #writer()
	 * @see #isWritable()
	 *
	 * @return the <em>original</em> value of the meta-object
	 */
	Object value();

	/**
	 * Return always the <em>current</em> value of the property.
	 *
	 * @see #value()
	 * @see #writer()
	 *
	 * @return the current property value
	 */
	default Object read() {
		return reader().read();
	}

	/**
	 * Return a value reader of {@code this} property.
	 *
	 * @return value reader of {@code this} property
	 */
	default ValueReader reader() {
		return this::value;
	}

	/**
	 * Return a value writer of {@code this} property, if it is mutable.
	 *
	 * @return value writer of {@code this} property
	 */
	default Optional<ValueWriter> writer() {
		return Optional.empty();
	}


	/**
	 * Property value reader interface, which allows to re-read the property
	 * value.
	 */
	@FunctionalInterface
	interface ValueReader {

		/**
		 * Read the current property value. Might differ from {@link #value()} if
		 * the underlying (mutable) object has been changed.
		 *
		 * @return the current property value
		 */
		Object read();

	}

	/**
	 * Property value writer interface, which allows to mutate the property
	 * value.
	 */
	@FunctionalInterface
	interface ValueWriter {

		/**
		 * Changes the property value.
		 *
		 * @param value the new property value
		 * @return {@code true} if the value has been changed successfully,
		 *         {@code false} if the property value couldn't be changed
		 */
		boolean write(final Object value);

	}

}

