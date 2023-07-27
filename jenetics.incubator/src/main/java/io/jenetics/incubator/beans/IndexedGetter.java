package io.jenetics.incubator.beans;

@FunctionalInterface
public interface IndexedGetter {
	Object apply(final Object object, final int index);
}
