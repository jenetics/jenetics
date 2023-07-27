package io.jenetics.incubator.property;

@FunctionalInterface
public interface IndexedSetter {
	boolean apply(final Object object, final int index, final Object value);
}
