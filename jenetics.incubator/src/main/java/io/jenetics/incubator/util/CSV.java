package io.jenetics.incubator.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CSV {

	/**
	 * Represents a CSV row token.
	 */
	final static class Token {
		final String seq;
		final int pos;

		Token(final String seq, final int pos) {
			this.seq = seq;
			this.pos = pos;
		}
	}


	private static final Pattern SPLITTER = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))");

	private static final char SEPARATOR = ',';
	private static final char QUOTE = '"';













	public static List<String> split(final String row) {
		return Arrays.asList(SPLITTER.split(row));
	}

}
