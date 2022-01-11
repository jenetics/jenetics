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
package io.jenetics.incubator.parser;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import io.jenetics.incubator.parser.Token.Type;

/**
 * Base class for all parsers.
 *
 * @param <V> the token value type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public abstract class Parser<V> {

	private final Tokenizer<V> _tokenizer;
	private final TokenRing<V> _lookahead;

	protected Parser(final Tokenizer<V> tokenizer, final int k) {
		_tokenizer = requireNonNull(tokenizer);
		_lookahead = new TokenRing<>(k);
		for (int i = 0; i < k; ++i) {
			consume();
		}
	}

	/**
	 * Return the lookahead token with the given index. The index starts at
	 * {@code 1}.
	 *
	 * @param index lookahead index
	 * @return the token at the given index
	 */
	public Token<V> LT(final int index) {
		return _lookahead.LT(index);
	}

	/**
	 * Return the token type code for the given lookahead index.
	 *
	 * @param index lookahead index
	 * @return the token type code for the given lookahead index
	 */
	public int LA(final int index) {
		return LT(index).type().code();
	}

	/**
	 * Try to <em>match</em> and consume the next token of the given
	 * {@code type}. If the current token is not from the given type, a
	 * {@link ParsingException} is thrown.
	 *
	 * @param type the token type to match
	 * @return the matched token
	 * @throws NullPointerException if the given token {@code type} is
	 *        {@code null}
	 * @throws ParsingException if the current token doesn't match the desired
	 *        token {@code type}
	 */
	public Token<V> match(final Type type) {
		if (LA(1) == type.code()) {
			final var token = LT(1);
			consume();
			return token;
		} else {
			throw new ParsingException(format(
				"Expecting %s but found %s.",
				type, LT(1)
			));
		}
	}

	/**
	 * Consumes the next token.
	 */
	public void consume() {
		_lookahead.add(_tokenizer.next());
	}

}
