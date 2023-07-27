package io.jenetics.incubator.beans.statical;

@FunctionalInterface
public interface Setter {
	boolean apply(final Object object, Object value);
}
