package io.jenetics.incubator.metamodel.access;

@FunctionalInterface
public interface Curryer<T> {
	T curry(final Object value);
}
