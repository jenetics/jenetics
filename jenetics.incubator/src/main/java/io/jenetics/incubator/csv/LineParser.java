package io.jenetics.incubator.csv;

import java.util.function.Function;

import io.jenetics.ext.util.CsvSupport;

@FunctionalInterface
public interface LineParser<T> extends Function<String, T> {

	T parse(String line);

	@Override
	default T apply(final String line) {
		return parse(line);
	}

	static LineParser<String[]> of(CsvSupport.LineSplitter splitter) {
		return splitter.copy()::split;
	}

}
