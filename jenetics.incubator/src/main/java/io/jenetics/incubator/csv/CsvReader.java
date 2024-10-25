package io.jenetics.incubator.csv;

import java.io.Reader;
import java.util.List;

public interface CsvReader<T> {

	List<T> read(Reader reader);

}
