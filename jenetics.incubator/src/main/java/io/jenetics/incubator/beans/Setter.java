package io.jenetics.incubator.beans;

@FunctionalInterface
public interface Setter {
	boolean apply(final Object object, Object value);
}
