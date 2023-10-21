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

import java.util.Optional;

import io.jenetics.incubator.beans.Path;

/**
 * Represents an object's property. A property might be defined as usual
 * <em>bean</em> property, with getter and setter, or as record component. The
 * following code shows how to create (a transitive) list of all properties from
 * a given root object.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public sealed interface Property
	permits SimpleProperty, IndexedProperty
{

	/**
	 * Return the path of the property.
	 *
	 * @return the path of the property
	 */
	Path path();

	/**
	 * Return the property name, or {@code <root>} if it is the property root.
	 *
	 * @return the name of the property
	 */
	default String name() {
		return path().isEmpty() ? "<root>" : path().element().toString();
	}

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
	 * Reads the actual value of the property. This value may be different from
	 * the initial, cached {@link #value()}.
	 *
	 * @return the actual value of the property
	 */
	default Object read() {
		return value();
	}

	/**
	 * Return the writer which allows to update the property, or
	 * {@link Optional#empty()} if the property is unmodifiable.
	 *
	 * @return the property writer
	 */
	default Optional<Writer> writer() {
		return Optional.empty();
	}

}

