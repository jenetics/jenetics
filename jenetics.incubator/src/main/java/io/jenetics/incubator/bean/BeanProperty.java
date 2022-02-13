package io.jenetics.incubator.bean;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Bean <em>property</em> implementation.
 */
record BeanProperty(
	PropertyDescriptor descriptor,
	Path path,
	Object object,
	Object value
)
	implements Property
{
	BeanProperty {
		requireNonNull(descriptor);
		requireNonNull(path);
		requireNonNull(object);
	}

	@Override
	public Class<?> type() {
		return descriptor.getPropertyType();
	}

	@Override
	public String name() {
		return descriptor.getName();
	}

	@Override
	public Object read() {
		return readValue(descriptor, object);
	}

	@Override
	public boolean write(final Object value) {
		return writeValue(descriptor, object, value);
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
	static Stream<Property> read(
		final Path basePath,
		final Object object
	) {
		if (object != null) {
			return descriptors(object.getClass()).map(desc ->
				new BeanProperty(
					desc,
					basePath.append(desc.getName()),
					object,
					readValue(desc, object)
				)
			);
		} else {
			return Stream.empty();
		}
	}

	private static Object readValue(final PropertyDescriptor descriptor, final Object parent) {
		try {
			return descriptor.getReadMethod().invoke(parent);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	private static boolean writeValue(
		final PropertyDescriptor descriptor,
		final Object parent,
		final Object value
	) {
		try {
			final var wm = descriptor.getWriteMethod();
			if (wm != null) {
				wm.invoke(parent, value);
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

	private static Stream<PropertyDescriptor> descriptors(final Class<?> type) {
		try {
			final PropertyDescriptor[] descriptors = Introspector
				.getBeanInfo(type)
				.getPropertyDescriptors();

			return Stream.of(descriptors)
				.filter(desc -> desc.getPropertyType() != Class.class)
				.filter(desc -> desc.getReadMethod() != null)
				.sorted(Comparator.comparing(PropertyDescriptor::getName));
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException("Can't introspect Object.");
		}
	}

}
