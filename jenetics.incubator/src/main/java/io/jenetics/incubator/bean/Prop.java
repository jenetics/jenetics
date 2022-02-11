package io.jenetics.incubator.bean;

import java.util.List;
import java.util.stream.Stream;

public interface Prop {

	Root root();

	List<String> path();

	String name();

	Object value();

	Stream<Prop> props();

}
