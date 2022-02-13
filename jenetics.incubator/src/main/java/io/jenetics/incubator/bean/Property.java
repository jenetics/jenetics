package io.jenetics.incubator.bean;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.incubator.bean.Property.Path;

/**
 * Represents an object's property. A property might be defined as usual
 * <em>bean</em> property, with getter and setter, or as record component.
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

				final var parent = new SimpleProperty(
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
			Reader.DEFAULT.filterPackages(packages),
			property -> property.value() instanceof Collection<?> coll
				? coll.stream()
				: Stream.empty()
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
		Reader DEFAULT = BeanProperty::read;

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
	 * Represents the absolute property path.
	 */
	final class Path implements Iterable<Path> {
		private final String name;
		private final int index;
		private final List<Path> elements;

		private Path(final String name, final int index, final List<Path> head) {
			this.name = requireNonNull(name);
			this.index = index;
			this.elements = append(head, this);
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
			this(name, -1, List.of());
		}

		private Path() {
			this.name = null;
			this.index = -1;
			this.elements = List.of();
		}

		public String name() {
			return name;
		}

		public int index() {
			return index;
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
			return new Path(element, -1, elements);
		}

		/**
		 * Create a new path object by converting the last element of
		 * {@code this} path to an <em>indexed</em> path element.
		 *
		 * @param index the index of the last path element
		 * @return a new path object
		 */
		Path indexed(final int index) {
			return new Path(name, index, elements.subList(0, elements.size() - 1));
		}

		@Override
		public Iterator<Path> iterator() {
			return elements.iterator();
		}

		@Override
		public String toString() {
			return elements.stream()
				.map(Path::toSimpleString)
				.collect(Collectors.joining("."));
		}

		private String toSimpleString() {
			return index < 0
				? name
				: String.format("%s[%d]", name, index);
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
 * Simple (static) property implementation.
 */
record SimpleProperty(
	Object object,
	Path path,
	Class<?> type,
	Path name,
	Object value
)
	implements Property
{

	SimpleProperty {
		requireNonNull(object);
		requireNonNull(path);
		requireNonNull(type);
		requireNonNull(name);
	}

	@Override
	public String toString() {
		return toString(this);
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
}

/**
 * Bean <em>property</em> implementation.
 */
final class BeanProperty implements Property {
	private final Descriptor descriptor;
	private final Object object;
	private final Path path;
	private final Object value;

	BeanProperty(
		final Descriptor descriptor,
		final Object object,
		final Path path,
		final Object value
	) {
		this.descriptor = requireNonNull(descriptor);
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
		return descriptor.type;
	}

	@Override
	public Path name() {
		return new Path(descriptor.name);
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public Object read() {
		return descriptor.read(object);
	}

	@Override
	public boolean write(final Object value) {
		return descriptor.write(object, value);
	}

	@Override
	public String toString() {
		return SimpleProperty.toString(this);
	}

	/**
	 * Read the bean properties from a given {@code object}.
	 *
	 * @param basePath the base path of the read properties
	 * @param object the object from where to read its properties
	 * @return the object's bean properties
	 */
	static Stream<Property> read(final Path basePath, final Object object) {
		if (object != null) {
			return descriptors(object.getClass())
				.map(desc ->
					new BeanProperty(
						desc,
						object,
						basePath.append(desc.name),
						desc.read(object)
					)
				);
		} else {
			return Stream.empty();
		}
	}

	private static Stream<Descriptor> descriptors(final Class<?> type) {
		try {
			final PropertyDescriptor[] descriptors = Introspector
				.getBeanInfo(type)
				.getPropertyDescriptors();

			return Stream.of(descriptors)
				.filter(desc -> desc.getPropertyType() != Class.class)
				.filter(desc -> desc.getReadMethod() != null)
				.map(desc ->
					new Descriptor(
						desc.getPropertyType(),
						desc.getName(),
						desc.getReadMethod(),
						desc.getWriteMethod()
					)
				)
				.sorted();
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException("Can't introspect Object.", e);
		}
	}

	private static final class Descriptor implements Comparable<Descriptor> {
		private final Class<?> type;
		private final String name;
		private final Method getter;
		private final Method setter;

		private Descriptor(
			final Class<?> type,
			final String name,
			final Method getter,
			final Method setter
		) {
			this.type = requireNonNull(type);
			this.name = requireNonNull(name);
			this.getter = requireNonNull(getter);
			this.setter = setter;
		}

		Object read(final Object object) {
			try {
				return object != null ? getter.invoke(object) : null;
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}

		boolean write(final Object object, final Object value) {
			try {
				if (setter != null && object != null) {
					setter.invoke(object, value);
					return true;
				}
			} catch (IllegalAccessException ignore) {
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof RuntimeException re) {
					throw re;
				} else {
					throw new IllegalStateException(e.getTargetException());
				}
			}

			return false;
		}

		@Override
		public int compareTo(final Descriptor o) {
			return name.compareTo(o.name);
		}

	}

}

/**
 * Preorder property iterator.
 */
final class PropertyPreOrderIterator implements Iterator<Property> {

	private final Property.Reader reader;
	private final Deque<Iterator<Property>> deque = new ArrayDeque<>();

	PropertyPreOrderIterator(
		final Path basePath,
		final Object root,
		final Property.Reader reader
	) {
		this.reader = requireNonNull(reader);
		deque.push(reader.read(basePath, root).iterator());
	}

	@Override
	public boolean hasNext() {
		final Iterator<Property> peek = deque.peek();
		return peek != null && peek.hasNext();
	}

	@Override
	public Property next() {
		final Iterator<Property> it = deque.peek();
		if (it == null) {
			throw new NoSuchElementException("No next element.");
		}

		final Property node = it.next();
		if (!it.hasNext()) {
			deque.pop();
		}

		final Iterator<Property> children = reader
			.read(node.path(), node.value())
			.iterator();
		if (children.hasNext()) {
			deque.push(children);
		}

		return node;
	}

}

