package io.jenetics.incubator.bean;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

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
	public String name() {
		return descriptor.name;
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
		return format(
			"Property[path=%s, name=%s, value=%s, type=%s, object=%s]",
			path(), name(), value(), type().getName(), object()
		);
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
