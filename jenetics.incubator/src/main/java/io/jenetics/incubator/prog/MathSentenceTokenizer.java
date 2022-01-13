/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.prog;

import static io.jenetics.incubator.mathexpr.MathTokenType.COMMA;
import static io.jenetics.incubator.mathexpr.MathTokenType.DIV;
import static io.jenetics.incubator.mathexpr.MathTokenType.IDENTIFIER;
import static io.jenetics.incubator.mathexpr.MathTokenType.LPAREN;
import static io.jenetics.incubator.mathexpr.MathTokenType.MINUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.MOD;
import static io.jenetics.incubator.mathexpr.MathTokenType.NUMBER;
import static io.jenetics.incubator.mathexpr.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathTokenType.RPAREN;
import static io.jenetics.incubator.mathexpr.MathTokenType.TIMES;

import java.util.List;
import java.util.Optional;

import io.jenetics.incubator.grammar.Cfg.Terminal;
import io.jenetics.incubator.parser.IterableTokenizer;
import io.jenetics.incubator.parser.Token;

/**
 * Wraps a generated list of terminals into a tokenizer. This is than used as
 * input for the parsing part.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
final class MathSentenceTokenizer extends IterableTokenizer<Terminal, String> {

	public MathSentenceTokenizer(final List<Terminal> sentence) {
		super(sentence, MathSentenceTokenizer::toToken);
	}

	private static Token<String> toToken(final Terminal terminal) {
		return switch (terminal.value()) {
			case "(" -> new Token<>(LPAREN, terminal.value());
			case ")" -> new Token<>(RPAREN, terminal.value());
			case "," -> new Token<>(COMMA, terminal.value());
			case "+" -> new Token<>(PLUS, terminal.value());
			case "-" -> new Token<>(MINUS, terminal.value());
			case "*" -> new Token<>(TIMES, terminal.value());
			case "/" -> new Token<>(DIV, terminal.value());
			case "%" -> new Token<>(MOD, terminal.value());
			case "^", "**" -> new Token<>(POW, terminal.value());
			default -> toNumber(terminal.value()).isPresent()
				? new Token<>(NUMBER, terminal.value())
				: new Token<>(IDENTIFIER, terminal.value());
		};
	}

	// TODO: improve implementation
	private static Optional<Double> toNumber(final String value) {
		try {
			return Optional.of(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

}
