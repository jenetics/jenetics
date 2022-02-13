package io.jenetics.incubator.bean;

import static java.lang.String.format;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
		Stream<Property> read(final Path basePath, final Object object);

		default Reader filter(final Predicate<? super Object> filter) {
			return (basePath, object) -> {
				if (filter.test(object)) {
					return this.read(basePath, object);
				} else {
					return Stream.empty();
				}
			};
		}

		default Reader filterPackage(final String pkg) {
			return filter(o ->
				o != null &&
					o.getClass().getPackage().getName().startsWith(pkg)
			);
		}

	}

	/**
	 * Represents the absolute property path.
	 */
	record Path(List<Element> elements) {

		/**
		 * Represents a path element.
		 */
		public record Element(String name, int index) {
			public Element indexed(final int index) {
				return new Element(name, index);
			}
			@Override
			public String toString() {
				return index < 0 ? name : format("%s[%d]", name, index);
			}
		}

		public Path {
			elements = List.copyOf(elements);
		}

		public Path() {
			this(List.of());
		}

		public boolean matches(final String pattern) {
			return false;
		}

		public Path append(final String element) {
			final var result = new ArrayList<Element>(elements.size() + 1);
			result.addAll(elements);
			result.add(new Element(element, -1));
			return new Path(result);
		}

		public Path indexed(final int index) {
			final var result = new ArrayList<>(elements);
			final var last = result.remove(result.size() - 1);
			result.add(last.indexed(index));
			return new Path(result);
		}

		@Override
		public String toString() {
			return elements.stream()
				.map(Objects::toString)
				.collect(Collectors.joining("."));
		}
	}

	/**
	 * Default flattener for flattening {@link Collection} properties. The
	 * flattened collection will be still part of the flattened result.
	 */
	Function<? super Property, ? extends Stream<?>> COLLECTION_FLATTENER =
	property -> property.value() instanceof Collection<?> coll
		? coll.stream()
		: Stream.empty();

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
		return walk(new Path(), object, reader, flattener, visited);
	}

	private static Stream<Property> walk(
		final Path basePath,
		final Object object,
		final Reader reader,
		final Function<? super Property, ? extends Stream<?>> flattener,
		final Map<Object, Object> visited
	) {
		if (visited.containsKey(object)) {
			return Stream.empty();
		}

		final var it = new PreOrderPropertyIterator(basePath, object, reader);
		//final var it = new BreathFirstPropertyIterator(basePath, object, reader);
		final var sp = spliteratorUnknownSize(it, Spliterator.SIZED);

		return StreamSupport.stream(sp, false)
			.filter(p -> {
				synchronized (visited) {
					if (p.value() instanceof Collection<?>) {
						return true;
					} else {
						final var visit = visited.containsKey(p.value());
						visited.put(p.value(), "");
						return !visit;
					}
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

		return Stream.concat(
			Stream.of(property),
			flattener.apply(property)
				.flatMap(ele ->
					walk(
						property.path().indexed(index.getAndIncrement()),
						ele,
						reader,
						flattener,
						visited
					)
				)
		);
	}

}
