package io.jenetics.incubator.property;

@FunctionalInterface
public interface Getter {
	Object apply(final Object object);
}
