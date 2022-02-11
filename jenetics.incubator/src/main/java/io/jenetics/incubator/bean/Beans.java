package io.jenetics.incubator.bean;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.stream.Stream;

final class Beans {

    private Beans() {
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

    private static Prop toProperty(
        final String basePath,
        final PropertyDescriptor descriptor,
        final Object parent
    ) {
        final var path = basePath != null
            ? basePath + "." + descriptor.getName()
            : descriptor.getName();

		return new Prop(
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

    static Stream<PropertyDescriptor> descriptors(final Class<?> type) {
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
