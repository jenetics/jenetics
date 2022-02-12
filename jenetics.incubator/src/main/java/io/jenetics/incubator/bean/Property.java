package io.jenetics.incubator.bean;

import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents an object's property. A property might be defined as usual
 * <em>bean</em> property, with getter and setter, or as record component.
 */
public interface Property {

	/**
	 * This interface is responsible for reading the properties of a given
	 * {@code object}.
	 */
	@FunctionalInterface
	interface Reader {

		Reader BEANS = BeanProperty::read;

		/**
		 * Reads the properties from the given {@code object}. The
		 * {@code basePath} is needed for building the <em>full</em> path of
		 * the read properties. Both arguments may be {@code null}.
		 *
		 * @param basePath the base path of the read properties
		 * @param object the object from where to read its properties
		 * @return the object's properties
		 */
		Stream<Property> read(final String basePath, final Object object);

	}

	record Path() {
		public boolean matches(final String pattern) {
			return false;
		}
	}

	/**
	 * Default flattener for flattening {@link Collection} properties. The
	 * flattened collection will be still part of the flattened result.
	 */
	Function<? super Property, ? extends Stream<?>> COLLECTION_FLATTENER =
	property -> property.value() instanceof Collection<?> coll
		? Stream.concat(Stream.of(property.value()), coll.stream())
		: Stream.of(property.value());

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
	String path();

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
	String name();

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
	Object read();

	/**
	 * Changes the property value.
	 *
	 * @param value the new property value
	 * @return {@code true} if the value has been changed successfully,
	 *         {@code false} if the property is not mutable
	 */
	boolean write(final Object value);


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
		final Map<Object, Object> visited = new IdentityHashMap<>();
		return walk(null, object, reader, flattener, visited);
	}

	private static Stream<Property> walk(
		final String basePath,
		final Object object,
		final Reader reader,
		final Function<? super Property, ? extends Stream<?>> flattener,
		final Map<Object, Object> visited
	) {
		if (visited.containsKey(object)) {
			return Stream.empty();
		}

		final var it = new PreOrderPropertyIterator(basePath, object, reader);
		final var sp = spliteratorUnknownSize(it, Spliterator.SIZED);

		return StreamSupport.stream(sp, false)
			.filter(p -> {
				synchronized (visited) {
					final var visit = visited.containsKey(p.object());
					visited.put(p.object(), "");
					return visit;
				}
			})
			.flatMap(prop -> flatten(prop, reader, flattener, visited));
	}

	private static Stream<Property> flatten(
		final Property property,
		final Reader reader,
		final Function<? super Property, ? extends Stream<?>> flattener,
		final Map<Object, Object> visited
	) {
		final var index = new AtomicInteger();
		return flattener.apply(property).flatMap(ele ->
			walk(
				indexedPath(property.path(), index),
				ele,
				reader,
				flattener,
				visited
			)
		);
	}

	private static String indexedPath(
		final String basePath,
		final AtomicInteger index
	) {
		return String.format("%s[%s]", basePath, index.getAndIncrement());
	}

}
