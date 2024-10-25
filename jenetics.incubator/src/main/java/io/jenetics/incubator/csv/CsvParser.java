package io.jenetics.incubator.csv;

import java.io.Reader;
import java.util.List;

public interface CsvParser<T> {

	List<T> parse(Reader reader);

}
