package io.jenetics.incubator.beans.statical;

@FunctionalInterface
public interface IndexedGetter {
	Object apply(final Object object, final int index);
}
