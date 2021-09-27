package io.jenetics.incubator.parser;

import static java.lang.String.format;

import java.util.stream.Stream;

public abstract class Tokenizer {

	private static final char EOF = (char)-1;

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

	public final Stream<Token> tokens() {
		return Stream.generate(this::next)
			.takeWhile(token -> token.type().code() == StandardTokenType.EOF.code());
	}

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

	public final boolean isEof(final char ch) {
		return ch == EOF;
	}

}
