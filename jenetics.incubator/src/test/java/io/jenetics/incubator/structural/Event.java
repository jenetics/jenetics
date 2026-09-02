package io.jenetics.incubator.structural;

public interface Event {
	Long id();
	String name();

	interface Builder extends Event {
		Builder id(Long value);
		Builder name(String value);
	}
}
