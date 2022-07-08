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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents an object's property. A property might be defined as usual
 * <em>bean</em> property, with getter and setter, or as record component. The
 * following code shows how to create (a transitive) list of all properties from
 * a given root object.
 * <pre>{@code
 * final var root = ...;
 * final List<Property> properties = Property
 *     // Wall all properties from the 'root' object which are defined
 *     // in the 'io.jenetics' package.
 *     .walk(root, "io.jenetics")
 *     .toList();
 * }</pre>
 * Only get string properties.
 * <pre>{@code
 * final List<Property> properties = Property
 *     .walk(root, "io.jenetics")
 *     .filter(property -> property.type() == String.class)
 *     .toList();
 * }</pre>
 * Only get the properties declared in the {@code MyBeanObject} class.
 * <pre>{@code
 * final List<Property> properties = Property
 *     .walk(root, "io.jenetics")
 *     .filter(property -> property.object().getClass() == MyBeanObject.class)
 *     .toList();
 * }</pre>
 * Only get properties with the name {@code index}. No matter where they defined
 * in the object hierarchy.
 * <pre>{@code
 * final List<Property> properties = Property
 *     .walk(root, "io.jenetics")
 *     .filter(Property.pathMatcher("**index"))
 *     .toList();
 * }</pre>
 * Updates all "index" properties with value {@code -1} to zero and returns all
 * properties, which couldn't be updated, because the property was immutable.
 * <pre>{@code
 * final List<Property> notUpdated = Property
 *     .walk(root, "io.jenetics")
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
public interface Property {

	/**
	 * Returns the object which contains {@code this} property; always
	 * non-{@code null}.
	 *
	 * @return the object which contains {@code this} property
	 */
	Object object();

	/**
	 * The full path, separated with dots '.', of {@code this} property from
	 * the <em>root</em> object; always non-{@code null}.
	 *
	 * @return the full property path
	 */
	Path path();

	/**
	 * The property type; always non-{@code null}.
	 *
	 * @return the property type
	 */
	Class<?> type();

	/**
	 * The name of {@code this} property; always non-{@code null}.
	 *
	 * @return the property name
	 */
	Path name();

	/**
	 * The initial, cached property value; might be {@code null}.
	 *
	 * @return the initial, cached property value
	 */
	Object value();

	/**
	 * Read the current property value. Might differ from {@link #value()} if
	 * the underlying (mutable) object has been changed.
	 *
	 * @return the current property value
	 */
	default Object read() {
		return value();
	}

	/**
	 * Changes the property value.
	 *
	 * @param value the new property value
	 * @return {@code true} if the value has been changed successfully,
	 *         {@code false} if the property is not mutable
	 */
	default boolean write(final Object value) {
		return false;
	}

	/**
	 * Return a Stream that is lazily populated with bean properties by walking
	 * the object graph rooted at a given starting {@code object}. The object
	 * tree is traversed in pre-order.
	 *
	 * @param object the root of the object tree
	 * @param reader the property reader for the given object kind
	 * @param flattener function which allows flattening (unroll) properties.
	 *        This might be useful when a property is a collection and contains
	 *        itself objects for which you are interested in its properties.
	 * @return the property stream, containing all transitive properties of the
	 *         given root {@code object}. The object tree is traversed in
	 *         pre-order.
	 */
	static Stream<Property> walk(
		final Object object,
		final Reader reader,
		final Function<? super Property, ? extends Stream<?>> flattener
	) {
		requireNonNull(reader);
		requireNonNull(flattener);

		final Map<Object, Object> visited = new IdentityHashMap<>();
		return walk(new Path(), object, reader, flattener, visited);
	}

	private static Stream<Property> walk(
		final Path basePath,
		final Object object,
		final Reader reader,
		final Function<? super Property, ? extends Stream<?>> flattener,
		final Map<Object, Object> visited
	) {
		if (object == null) {
			return Stream.empty();
		}

		final boolean exists;
		synchronized(visited) {
			if (!(exists = visited.containsKey(object))) {
				visited.put(object, "");
			}
		}

		if (exists) {
			return Stream.empty();
		} else {
			final var it = new PropertyPreOrderIterator(basePath, object, reader);
			final var sp = spliteratorUnknownSize(it, Spliterator.SIZED);

			return StreamSupport.stream(sp, false)
				.flatMap(prop ->
					Stream.concat(
						Stream.of(prop),
						flatten(prop, reader, flattener, visited)
					)
				);
		}
	}

	private static Stream<Property> flatten(
		final Property property,
		final Reader reader,
		final Function<? super Property, ? extends Stream<?>> flattener,
		final Map<Object, Object> visited
	) {
		final var index = new AtomicInteger();

		return flattener.apply(property)
			.flatMap(ele -> {
				final Path path = property.path()
					.indexed(index.getAndIncrement());

				final var parent = new ImmutableProperty(
					property.value(),
					path,
					ele != null ? ele.getClass() : Object.class,
					path.head(),
					ele
				);

				return Stream.concat(
					Stream.of(parent),
					walk(
						path,
						ele,
						reader,
						flattener,
						visited
					)
				);
			});
	}

	static Stream<Property> walk(
		final Object object,
		final Function<? super Property, ? extends Stream<?>> flattener,
		final String... packages
	) {
		return walk(
			object,
			Reader.DEFAULT.filterPackages(packages),
			flattener
		);
	}

	/**
	 * Return a Stream that is lazily populated with bean properties by walking
	 * the object graph rooted at a given starting {@code object}. The object
	 * tree is traversed in pre-order.
	 *
	 * @param object the root of the object tree
	 * @param packages the base packages of the object where the properties
	 *        are read from
	 * @return the property stream, containing all transitive properties of the
	 *         given root {@code object}. The object tree is traversed in
	 *         pre-order.
	 */
	static Stream<Property> walk(final Object object, final String... packages) {
		return walk(
			object,
			property -> property.value() instanceof Collection<?> coll
				? coll.stream()
				: Stream.empty(),
			packages
		);
	}

	/**
	 * Read the direct (first level) bean properties from a given {@code object}.
	 * If the given {@code object} is {@code null}, an empty stream is returned.
	 *
	 * @param basePath the base path of the read properties
	 * @param object the object from where to read its properties
	 * @return the object's bean properties
	 */
	static Stream<Property> read(final Path basePath, final Object object) {
		if (object != null) {
			return PropertyDesc.stream(object.getClass())
				.map(desc -> {
					if (desc.setter() != null) {
						return new MutableProperty(
							desc,
							object,
							basePath.append(desc.name()),
							desc.read(object)
						);
					} else {
						return new ImmutableProperty(
							object,
							basePath.append(desc.name()),
							desc.type(),
							new Path(desc.name()),
							desc.read(object)
						);
					}
				});
		} else {
			return Stream.empty();
		}
	}

	/**
	 * Return a matcher for the {@link Property.Path} of a property.
	 *
	 * @see Property.Path#matcher(String)
	 *
	 * @param pattern the path pattern
	 * @return a new property path matcher
	 */
	static Predicate<Property> pathMatcher(final String pattern) {
		final var matcher = Path.matcher(pattern);
		return property -> matcher.test(property.path());
	}

	static String toString(final Property property) {
		return format(
			"Property[path=%s, name=%s, value=%s, type=%s, object=%s]",
			property.path(),
			property.name(),
			property.value(),
			property.type() != null ? property.type().getName() : null,
			property.object()
		);
	}


	/**
	 * This interface is responsible for reading the properties of a given
	 * {@code object}.
	 */
	@FunctionalInterface
	interface Reader {

		/**
		 * The default property reader, using the bean introspector class.
		 */
		Reader DEFAULT = Property::read;

		/**
		 * Reads the properties from the given {@code object}. The
		 * {@code basePath} is needed for building the <em>full</em> path of
		 * the read properties. Both arguments may be {@code null}.
		 *
		 * @param basePath the base path of the read properties
		 * @param object the object from where to read its properties
		 * @return the object's properties
		 */
		Stream<Property> read(final Path basePath, final Object object);

		/**
		 * Create a new reader which filters specific object from the property
		 * read.
		 *
		 * @param filter the object filter applied to the reader
		 * @return a new reader with the applied filter
		 */
		default Reader filter(final Predicate<? super Object> filter) {
			return (basePath, object) -> {
				if (filter.test(object)) {
					return this.read(basePath, object);
				} else {
					return Stream.empty();
				}
			};
		}

		/**
		 * Create a new reader which reads the properties only from the given
		 * packages.
		 *
		 * @param includes the base packages of the object where the properties
		 *        are read from
		 * @return a new reader which reads the properties only from the given
		 * 		   packages
		 */
		default Reader filterPackages(final String... includes) {
			return filter(object -> {
				if (object != null) {
					if (includes.length == 0) {
						return true;
					}

					final var pkg = object.getClass().getPackage().getName();
					for (var p : includes) {
						if (pkg.startsWith(p)) {
							return true;
						}
					}
				}

				return false;
			});
		}
	}


	/**
	 * Represents the property path, which uniquely identifies a property. A
	 * path can be created with the {@link Path#of(String)} method.
	 */
	final class Path implements Iterable<Path> {

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
		 * Return the path element which is farthest away from the root property.
		 *
		 * @return the path element which is farthest away from the root property
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

		/**
		 * Return a path matcher with the given pattern.
		 *
		 * @param pattern the pattern to match
		 * @return {@code true} if the given {@code pattern} matches {@code this}
		 *         path, {@code false} otherwise
		 */
		public static Predicate<Path> matcher(final String pattern) {
			final PathMatcher matcher = FileSystems.getDefault()
				.getPathMatcher(toGlobPattern(pattern));

			return path -> matcher.matches(
				java.nio.file.Path.of(
					path.toString().replace('.', '/')
				)
			);
		}

		private static String toGlobPattern(final String pattern) {
			return "glob:" + pattern.replace('.', '/')
				.replace("[", "\\[")
				.replace("]", "\\]");
		}

	}

}

