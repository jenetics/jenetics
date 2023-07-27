package io.jenetics.incubator.beans;

@FunctionalInterface
public interface Getter {
	Object apply(final Object object);
}
