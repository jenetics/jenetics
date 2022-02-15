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
 * <em>bean</em> property, with getter and setter, or as record component.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Property {

	/**
	 * Returns the object which contains {@code this} property.
	 *
	 * @return the object which contains {@code this} property
	 */
	Object object();

	/**
	 * The full path, separated with dots '.', of {@code this} property from
	 * the <em>root</em> object.
	 *
	 * @return the full property path
	 */
	Path path();

	/**
	 * The property type.
	 *
	 * @return the property type
	 */
	Class<?> type();

	/**
	 * The name of {@code this} property
	 *
	 * @return the property name
	 */
	Path name();

	/**
	 * The initial, cached property value, might be {@code null}.
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

	default <T> T as(final Class<? extends T> type) {
		return type.cast(value());
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
		 * @param packages the base packages of the object where the properties
		 *        are read from
		 * @return a new reader which reads the properties only from the given
		 * 		   packages
		 */
		default Reader filterPackages(final String... packages) {
			return filter(object -> {
				if (object != null) {
					if (packages.length == 0) {
						return true;
					}

					final var pkg = object.getClass().getPackage().getName();
					for (var p : packages) {
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
	 * Represents the property path.
	 */
	final class Path implements Iterable<Path> {

		record Name(String value, Integer index) {
			Name {
				requireNonNull(value);
				if (index != null && index < 0) {
					throw new IllegalArgumentException(
						"Index must not be negative: " + index
					);
				}
			}

			@Override
			public String toString() {
				return index != null
					? String.format("%s[%d]", value, index)
					: value;
			}

			static Name of(final String value) {
				if (value.isEmpty()) {
					throw iae(value);
				}

				final int begin = value.indexOf('[');
				final int end = value.indexOf(']');
				if (begin != -1 && end != -1) {
					final int index = parse(
						value,
						value.substring(begin + 1, end)
					);
					final String name = value.substring(0, begin);

					return new Name(value, index);
				} else {

				}

				return null;
			}

			private static IllegalArgumentException iae(final String value) {
				return new IllegalArgumentException(
					"Illegal path name: '" + value + "'"
				);
			}

			private static int parse(final String value, final String index) {
				try {
					return Integer.parseInt(index);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(format(
						"Invalid path index '%s' for path '%s'.",
						index, value
					));
				}
			}

		}

		private final Name name;
		private final List<Path> elements;

		private Path(final String name, final Integer index, final List<Path> head) {
			this.name = new Name(name, index);
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

		public Path(final String name, final int index) {
			this(name, index, List.of());
		}

		public Path(final String name) {
			this(name, null, List.of());
		}

		public String name() {
			return name.value;
		}

		public Integer index() {
			return name.index;
		}

		public int count() {
			return elements.size();
		}

		public Path get(final int index) {
			return elements.get(index);
		}

		public Path head() {
			return new Path(name(), index());
		}

		/**
		 * Create a new path object with the given element appended.
		 *
		 * @param element the path element to append
		 * @return a new path object with the given element appended
		 */
		public Path append(final String element) {
			return new Path(element, null, elements);
		}

		public Path append(final String element, final int index) {
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
			return new Path(name.value, index, elements.subList(0, elements.size() - 1));
		}

		@Override
		public Iterator<Path> iterator() {
			return elements.iterator();
		}

		@Override
		public String toString() {
			return elements.stream()
				.map(ele -> ele.name.toString())
				.collect(Collectors.joining("."));
		}

		/**
		 * Tests whether the given <em>glob</em> pattern matches {@code this}
		 * path.
		 *
		 * @param pattern the pattern to match
		 * @return {@code true} if the given {@code pattern} matches {@code this}
		 *         path, {@code false} otherwise
		 */
		public static Predicate<Property> matcher(final String pattern) {
			return property -> false;
		}

	}

}

/**
 * Bean <em>property</em> implementation.
 */
final class MutableProperty implements Property {
	private final PropertyDesc desc;
	private final Object object;
	private final Path path;
	private final Object value;

	MutableProperty(
		final PropertyDesc desc,
		final Object object,
		final Path path,
		final Object value
	) {
		this.desc = requireNonNull(desc);
		this.object = requireNonNull(object);
		this.path = requireNonNull(path);
		this.value = value;
	}

	@Override
	public Object object() {
		return object;
	}

	@Override
	public Path path() {
		return path;
	}

	@Override
	public Class<?> type() {
		return desc.type();
	}

	@Override
	public Path name() {
		return new Path(desc.name());
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public Object read() {
		return desc.read(object);
	}

	@Override
	public boolean write(final Object value) {
		return desc.write(object, value);
	}

	@Override
	public String toString() {
		return Property.toString(this);
	}

}

