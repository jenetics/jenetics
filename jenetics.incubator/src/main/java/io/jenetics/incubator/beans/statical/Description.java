package io.jenetics.incubator.beans.statical;

public sealed interface Description
	extends Comparable<Description>
	permits IndexedDescription, SimpleDescription
{

	String name();
	Class<?> type();

	@Override
	default int compareTo(final Description o) {
		return name().compareTo(o.name());
	}

}
