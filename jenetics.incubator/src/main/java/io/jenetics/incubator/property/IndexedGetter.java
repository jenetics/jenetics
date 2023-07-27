package io.jenetics.incubator.property;

@FunctionalInterface
public interface IndexedGetter {
	Object apply(final Object object, final int index);
}
