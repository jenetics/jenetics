package io.jenetics.incubator.parser;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public abstract class Parser {

	private final Tokenizer _tokenizer;
	private Token _lookahead;

	protected Parser(final Tokenizer tokenizer) {
		_tokenizer = requireNonNull(tokenizer);
	}

	protected void match(final int type) {
		if (_lookahead.type() == type) {
			consume();
		} else {
			throw new ParseException(format(
				"Expecting %s but found %s.",
				_tokenizer.toString(), _lookahead
			));
		}
	}

	protected void consume() {

	}

}
