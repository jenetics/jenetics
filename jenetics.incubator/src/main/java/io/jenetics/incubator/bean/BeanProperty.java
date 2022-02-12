package io.jenetics.incubator.bean;

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
	String path,
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

	/**
	 * Read the bean properties from a given {@code object}.
	 *
	 * @param basePath the base path of the read properties
	 * @param object the object from where to read its properties
	 * @return the object's bean properties
	 */
	static Stream<Property> read(
		final String basePath,
		final Object object
	) {
		return object != null
			? descriptors(object.getClass())
				.map(desc -> toProperty(basePath, desc, object))
			: Stream.empty();
	}

	private static BeanProperty toProperty(
		final String basePath,
		final PropertyDescriptor descriptor,
		final Object parent
	) {
		final var path = basePath != null
			? basePath + "." + descriptor.getName()
			: descriptor.getName();

		return new BeanProperty(
			descriptor,
			path,
			parent,
			readValue(descriptor, parent)
		);
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
		return descriptors0(type)
			.filter(desc -> desc.getReadMethod() != null);
	}

	private static Stream<PropertyDescriptor> descriptors0(final Class<?> type) {
		try {
			final PropertyDescriptor[] descriptors = Introspector
				.getBeanInfo(type)
				.getPropertyDescriptors();

			return Stream.of(descriptors)
				.sorted(Comparator.comparing(PropertyDescriptor::getName));
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException("Can't introspect Object.");
		}
	}

}
