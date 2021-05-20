package io.jenetics.incubator.parser;

import java.util.List;

public interface Parser<T> {

	T parse(final List<String> tokens);

}
