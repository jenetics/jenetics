package io.jenetics.incubator.property;

@FunctionalInterface
public interface Setter {
	boolean apply(final Object object, Object value);
}
