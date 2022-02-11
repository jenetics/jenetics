package io.jenetics.incubator.bean;

import java.beans.PropertyDescriptor;

public record Property(
	PropertyDescriptor descriptor,
	String path,
	Object parent,
    Object value
) {

	public Class<?> type() {
		return descriptor.getPropertyType();
	}

	public String name() {
		return descriptor.getName();
	}

	/*
    public boolean set(final Object value) {
        final var paths = path.split(Pattern.quote("."));
        final var name = paths[paths.length - 1];

        final var setter = Beans.descriptors(parent.getClass())
            .peek(System.out::println)
            .filter(d -> name.equals(d.getName()))
            .flatMap(d -> Optional.ofNullable(d.getWriteMethod()).stream())
            .findFirst();

        final var result = setter.map(set -> {
            try {
                set.invoke(parent, value);
                //this.value = value;
                return true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        });

        return result.orElse(false);
    }
	 */

}
