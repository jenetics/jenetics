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
import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Parser implementation for parsing a given token sequences.
 *
 * @param <T> the token type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class BaseParser<T> {

	private final Tokenizer<T> _tokenizer;
	private final TokenRing<T> _lookahead;

	/**
	 * Create a new parser object with the given {@code tokenizer} and lookahead
	 * count {@code k}.
	 *
	 * @param tokenizer the token source {@code this} parser uses
	 * @param k the lookahead count
	 */
	public BaseParser(final Tokenizer<T> tokenizer, final int k) {
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
	public T LT(final int index) {
		return _lookahead.LT(index);
	}

	/**
	 * Try to <em>match</em> and consume the next token of the given
	 * {@code type}. If the current token is not from the given type, a
	 * {@link ParsingException} is thrown.
	 *
	 * @param token the token type to match
	 * @return the matched token
	 * @throws NullPointerException if the given token {@code type} is
	 *        {@code null}
	 * @throws ParsingException if the current token doesn't match the desired
	 *         {@code token}
	 */
	public T match(final T token) {
		requireNonNull(token);

		if (Objects.equals(LT(1), token)) {
			consume();
			return token;
		} else {
			throw new ParsingException(format(
				"Expecting %s but found %s.",
				token, LT(1)
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
