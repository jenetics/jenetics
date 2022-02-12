package io.jenetics.incubator.bean;

import static java.util.Objects.requireNonNull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.stream.Stream;

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


	static Stream<Property> properties(
		final String basePath,
		final Object parent
	) {
		return parent != null
			? descriptors(parent.getClass())
			.map(desc -> toProperty(basePath, desc, parent))
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

	static Object readValue(final PropertyDescriptor descriptor, final Object parent) {
		try {
			return descriptor.getReadMethod().invoke(parent);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	static boolean writeValue(
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
