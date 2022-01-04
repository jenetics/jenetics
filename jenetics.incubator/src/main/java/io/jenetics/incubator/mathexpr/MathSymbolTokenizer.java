package io.jenetics.incubator.mathexpr;

import java.util.List;

import io.jenetics.incubator.grammar.Cfg.Terminal;
import io.jenetics.incubator.parser.IterableTokenizer;
import io.jenetics.incubator.parser.Token;

public class MathSymbolTokenizer extends IterableTokenizer<Terminal, Terminal> {

	public MathSymbolTokenizer(final List<Terminal> sentence) {
		super(sentence, MathSymbolTokenizer::toToken);
	}

	@Override
	public Token<Terminal> next() {
		return null;
	}

	private static Token<Terminal> toToken(final Terminal terminal) {
		return switch (terminal.value()) {
			case "(" -> new Token<>(Token.Type.of(1, ""), terminal);
			case ")" -> new Token<>(Token.Type.of(1, ""), terminal);
			default -> null;
		};
	}

}
