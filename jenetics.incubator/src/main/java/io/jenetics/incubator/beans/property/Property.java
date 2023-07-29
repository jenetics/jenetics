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

import io.jenetics.incubator.beans.PathEntry;

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
	extends PathEntry<Value>
	permits IndexedProperty, IndexProperty, SimpleProperty
{
}

