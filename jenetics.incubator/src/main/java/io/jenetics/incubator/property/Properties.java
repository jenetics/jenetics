package io.jenetics.incubator.property;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

public final class Properties {
	private Properties() {
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
	public static Stream<Property> stream(
		final Object object,
		final Property.Reader reader,
		final Function<? super Property, ? extends Stream<?>> flattener
	) {
		requireNonNull(reader);
		requireNonNull(flattener);

		final Map<Object, Object> visited = new IdentityHashMap<>();
		return stream(Property.Path.EMPTY, object, reader, flattener, visited);
	}

	private static Stream<Property> stream(
		final Property.Path basePath,
		final Object object,
		final Property.Reader reader,
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
		final Property.Reader reader,
		final Function<? super Property, ? extends Stream<?>> flattener,
		final Map<Object, Object> visited
	) {
		final var index = new AtomicInteger();

		return flattener.apply(property)
			.flatMap(ele -> {
				final Property.Path path = property.path()
					.indexed(index.getAndIncrement());

				final var parent = new ReadonlyPropertyRecord(
					property.value(),
					path,
					ele != null ? ele.getClass() : Object.class,
					path.head(),
					ele
				);

				return Stream.concat(
					Stream.of(parent),
					stream(
						path,
						ele,
						reader,
						flattener,
						visited
					)
				);
			});
	}

	public static Stream<Property> stream(
		final Object object,
		final Function<? super Property, ? extends Stream<?>> flattener,
		final String... packages
	) {
		return stream(
			object,
			Property.Reader.DEFAULT.filterPackages(packages),
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
	public static Stream<Property> stream(final Object object, final String... packages) {
		return stream(
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
	public static Stream<Property> read(final Property.Path basePath, final Object object) {
		if (object != null) {
			return PropertyDescription.stream(object.getClass())
				.map(desc -> {
					if (desc.setter() != null) {
						return new WriteablePropertyRecord(
							desc,
							object,
							basePath.append(desc.name()),
							desc.read(object)
						);
					} else {
						return new ReadonlyPropertyRecord(
							object,
							basePath.append(desc.name()),
							desc.type(),
							new Property.Path(desc.name()),
							desc.read(object)
						);
					}
				});
		} else {
			return Stream.empty();
		}
	}

}
