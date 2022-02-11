package io.jenetics.incubator.bean;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

public final class Root {

	private final Object value;

	public Root(final Object value) {
		this.value = requireNonNull(value);
	}

	public Stream<Prop> props() {
		return Stream.empty();
	}

}

