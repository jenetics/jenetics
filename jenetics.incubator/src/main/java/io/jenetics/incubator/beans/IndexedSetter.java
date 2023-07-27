package io.jenetics.incubator.beans;

@FunctionalInterface
public interface IndexedSetter {
	boolean apply(final Object object, final int index, final Object value);
}
