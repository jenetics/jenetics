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
package io.jenetics.ext.internal.parser;

import static java.lang.String.format;

import io.jenetics.ext.internal.parser.Token.Type;

/**
 * Parser implementation for parsing explicit {@link Token} sequences.
 *
 * @param <V> the token value type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
public class TokenParser<V> extends Parser<Token<V>> {

	/**
	 * Create a new parser object with the given {@code tokenizer} and lookahead
	 * count {@code k}.
	 *
	 * @param tokenizer the token source {@code this} parser uses
	 * @param k the lookahead count
	 */
	public TokenParser(final Tokenizer<Token<V>> tokenizer, final int k) {
		super(tokenizer, k);
	}

	/**
	 * Return the token type code for the given lookahead index.
	 *
	 * @param index lookahead index
	 * @return the token type code for the given lookahead index
	 */
	public int LA(final int index) {
		final var token = LT(index);
		return token != null ? token.type().code() : Type.EOF.code();
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

}
