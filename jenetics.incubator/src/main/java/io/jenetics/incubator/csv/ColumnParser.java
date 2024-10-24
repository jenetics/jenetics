package io.jenetics.incubator.csv;

import java.util.function.Function;

import io.jenetics.ext.util.CsvSupport;

public interface ColumnParser<T> extends Function<String[], T> {
	T parse(final String[] columns);

	@Override
	default T apply(final String[] columns) {
		return parse(columns);
	}

	default LineParser<T> with(final CsvSupport.LineSplitter splitter) {
		return line -> parse(splitter.split(line));
	}

	static ColumnParser<Row> of() {
		return Row::of;
	}

}
