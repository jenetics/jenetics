package io.jenetics.incubator.mathexpr;

import static java.util.Objects.requireNonNull;

import java.util.List;

import io.jenetics.incubator.grammar.Cfg.Terminal;
import io.jenetics.incubator.parser.Token;
import io.jenetics.incubator.parser.Tokenizer;

public class MathSymbolTokenizer implements Tokenizer<Terminal> {

	private final List<Terminal> _sentence;
	private final List<Terminal> _operations;

	public MathSymbolTokenizer(
		final List<Terminal> sentence,
		final List<Terminal> operations
	) {
		_sentence = requireNonNull(sentence);
		_operations = requireNonNull(operations);
	}

	@Override
	public Token<Terminal> next() {
		return null;
	}

}
