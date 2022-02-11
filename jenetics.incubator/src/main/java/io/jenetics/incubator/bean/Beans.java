package io.jenetics.incubator.bean;

import static java.util.Objects.requireNonNull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

final class Beans {

	final static class BeanPropertyIterator extends PropertyIterator {
		BeanPropertyIterator(
			final String basePath,
			final Object root,
			final Predicate<? super Class<?>> filter
		) {
			super(basePath, root, filter);
		}
		@Override
		Iterator<Property> next(String basePath, Object parent) {
			return Beans.properties(basePath, parent).iterator();
		}
	}

	private record BeanProperty(
		PropertyDescriptor descriptor,
		String path,
		Object parent,
		Object value
	)
		implements Property
	{
		BeanProperty {
			requireNonNull(descriptor);
			requireNonNull(path);
			requireNonNull(parent);
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
			return Beans.readValue(descriptor, parent);
		}
		@Override
		public boolean write(final Object value) {
			return Beans.writeValue(descriptor, parent, value);
		}
	}

    private Beans() {
    }

    private static Stream<Property> properties(
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
