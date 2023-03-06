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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
public sealed interface Property permits SimpleProperty, CollectionProperty {

	/**
	 * Returns the object which contains {@code this} property.
	 *
	 * @return the object which contains {@code this} property
	 */
	Object enclosingObject();

	/**
	 * The full path, separated with dots '.', of {@code this} property from
	 * the <em>root</em> object.
	 *
	 * @return the full property path
	 */
	Path path();

	/**
	 * The name of {@code this} property; always non-{@code null}.
	 *
	 * @return the property name
	 */
	default String name() {
		return path().name();
	}

	/**
	 * The type of the metaobject, never {@code null}.
	 *
	 * @return the type of the metaobject
	 */
	Class<?> type();

	/**
	 * The value of the metaobject, may be {@code null}.
	 *
	 * @return the value of the metaobject
	 */
	Object value();

	/**
	 * Return a value reader of {@code this} property.
	 *
	 * @return value reader of {@code this} property
	 */
	default ValueReader reader() {
		return this::value;
	}

	/**
	 * Test whether {@code this} property is writable.
	 *
	 * @return {@code true} if @code this} property is writable, {@code false}
	 *         otherwise
	 */
	default boolean isWritable() {
		return false;
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
	 * Represents the property path, which uniquely identifies a property. A
	 * path can be created with the {@link Path#of(String)} method.
	 */
	final class Path implements Iterable<Path> {

		public static final Path EMPTY = new Path();

		private final PathName name;
		private final List<Path> elements;

		private Path(final String name, final Integer index, final List<Path> head) {
			this.name = new PathName(name, index);
			this.elements = append(head, this);
		}

		private Path() {
			this.name = null;
			this.elements = List.of();
		}

		private static List<Path> append(final List<Path> head, final Path path) {
			final var result = new ArrayList<Path>(head.size() + 1);
			result.addAll(head);
			result.add(path);
			return List.copyOf(result);
		}

		Path(final String name, final int index) {
			this(name, index, List.of());
		}

		Path(final String name) {
			this(name, null, List.of());
		}

		/**
		 * Return the property name, without index, from {@code this} path.
		 * This is the path element <em>farthest</em> away from the root.
		 *
		 * @return the property name
		 */
		public String name() {
			return name.value();
		}

		/**
		 * Return the index, from {@code this} path, or {@code null} if the
		 * property is not part of a collection.
		 *
		 * @return the property index, or {@code null} if {@code this} path has
		 *         no index defined
		 */
		public Integer index() {
			return name.index();
		}

		/**
		 * Returns the number of elements in the path.
		 *
		 * @return the number of elements in the path, or 0 if this path only
		 *         represents a root component
		 */
		public int count() {
			return elements.size();
		}

		/**
		 * Returns an element of this path. The index parameter is the index of
		 * the path element to return. The element that is closest to the root
		 * in the property hierarchy has index 0. The element that is farthest
		 * from the root has index count - 1.
		 *
		 * @param index the path index
		 * @return the path element
		 */
		public Path get(final int index) {
			final var ele = elements.get(index);
			return new Path(ele.name(), ele.index(), List.of());
		}

		/**
		 * Return the path element which is the farthest away from the root property.
		 *
		 * @return the path element which is the farthest away from the root property
		 */
		public Path head() {
			return new Path(name(), index());
		}

		/**
		 * Create a new path object with the given element appended.
		 *
		 * @param element the path element to append
		 * @return a new path object with the given element appended
		 */
		Path append(final String element) {
			return new Path(element, null, elements);
		}

		/**
		 * Create a new path object with the given element and index appended.
		 *
		 * @param element the path element to append
		 * @param index the property index
		 * @return a new path object with the given element appended
		 */
		Path append(final String element, final int index) {
			return new Path(element, index, elements);
		}

		/**
		 * Create a new path object by converting the last element of
		 * {@code this} path to an <em>indexed</em> path element.
		 *
		 * @param index the index of the last path element
		 * @return a new path object
		 */
		Path indexed(final int index) {
			return new Path(name.value(), index, elements.subList(0, elements.size() - 1));
		}

		@Override
		public Iterator<Path> iterator() {
			return elements.iterator();
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			for (var path : this) {
				hashCode = 31*path.name.hashCode();
			}
			return hashCode;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			} else if (obj instanceof Path path && count() == path.count()) {
				for (int i = 0; i < count(); ++i) {
					if (!get(i).name.equals(path.get(i).name)) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return elements.stream()
				.map(ele -> ele.name.toString())
				.collect(Collectors.joining("."));
		}

		/**
		 * Create a new property path form the given string {@code value}. A
		 * valid path consists of a names, which must be a valid Java identifier,
		 * and indexes, separated by a dot, '.'. A valid path with three elements
		 * will look like this:
		 * <pre>{@code
		 * final var path = Path.of("name1.name2[9].value");
		 * }</pre>
		 *
		 * @param value the path value
		 * @return a new property path
		 * @throws IllegalArgumentException if the given path is invalid
		 */
		public static Path of(final String value) {
			final var parts = java.nio.file.Path.of(value.replace('.', '/'))
				.normalize()
				.iterator();

			var path = new Path();
			while (parts.hasNext()) {
				final var part = parts.next();
				final var element = PathName.of(part.toString());
				path = element.index() != null
					? path.append(element.value(), element.index())
					: path.append(element.value());
			}

			return path;
		}

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

