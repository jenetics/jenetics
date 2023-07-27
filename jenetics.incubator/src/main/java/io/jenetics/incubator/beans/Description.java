package io.jenetics.incubator.beans;

sealed interface Description
	extends Comparable<Description>
	permits IndexedPropertyDescription, PropertyDescription
{

	String name();
	Class<?> type();

	@Override
	default int compareTo(final Description o) {
		return name().compareTo(o.name());
	}

}
