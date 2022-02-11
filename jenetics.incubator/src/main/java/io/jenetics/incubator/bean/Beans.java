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

    private static Property toProperty(
        final String basePath,
        final PropertyDescriptor descriptor,
        final Object parent
    ) {
        final var path = basePath != null
            ? basePath + "." + descriptor.getName()
            : descriptor.getName();

        try {
            return new Property(
				descriptor,
				path,
                parent,
                descriptor.getReadMethod().invoke(parent)
            );
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
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
