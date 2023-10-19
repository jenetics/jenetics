package io.jenetics.incubator.beans.property;

@FunctionalInterface
public interface Writer {
	boolean write(final Object value);
}
