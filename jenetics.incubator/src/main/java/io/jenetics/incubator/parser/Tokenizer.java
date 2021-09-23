package io.jenetics.incubator.parser;

import static java.lang.String.format;

public abstract class Tokenizer {

	public static final int EOF_TYPE = 1;

	public static final char EOF = (char)-1;

	private final CharSequence _input;

	protected int pos;
	protected char c = EOF;

	protected Tokenizer(final CharSequence input) {
		if (input.length() > 0) {
			c = input.charAt(0);
		}
		_input = input;
	}

	public abstract Token next();

	public abstract String toTokenName(final int tokenType);

	public void match(final char ch) {
		if (ch == c) {
			consume();
		} else {
			throw new IllegalArgumentException(format(
				"Got invalid character '%s' at position '%d'; expected '%s'",
				c, pos, ch
			));
		}
	}

	public void consume() {
		++pos;

		if (pos >= _input.length()) {
			c = EOF;
		} else {
			c = _input.charAt(pos);
		}
	}

}
