package io.jenetics.tool.measurement;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Table {

	private final List<String> _header;
	private final List<String[]> _rows = new ArrayList<>();

	private Table(final List<String> header) {
		_header = Collections.unmodifiableList(header);
	}

	public List<String> header() {
		return _header;
	}

	public List<String[]> rows() {
		return Collections.unmodifiableList(_rows);
	}

	public void add(final String... row) {
		if (row.length != _header.size()) {
			throw new IllegalArgumentException(format(
				"Invalid number of columns. Expected %d, but got %d.",
				_header.size(), row.length
			));
		}
		_rows.add(row);
	}

	public void write(final OutputStream out) throws IOException {
		final OutputStreamWriter osw = new OutputStreamWriter(out);
		final BufferedWriter writer = new BufferedWriter(osw);

		writer.write(toString(_header));
		writer.write("\n");

		for (String[] row : _rows) {
			writer.write(toString(asList(row)));
			writer.write("\n");
		}

		writer.flush();
	}

	private static String toString(final List<String> row) {
		return String.join(", ", row);
	}

	public static Table read(final InputStream in) throws IOException {
		return null;
	}


	public static Table of(final String... header) {
		return new Table(asList(header));
	}

}
