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

public abstract class Parser {

	private final Tokenizer _tokenizer;
	final TokenRing _lookahead;

	protected Parser(final Tokenizer tokenizer, final int k) {
		_tokenizer = requireNonNull(tokenizer);
		_lookahead = new TokenRing(k);
		for (int i = 0; i < k; ++i) {
			consume();
		}
	}

	public Token LT(final int i) {
		return _lookahead.LT(i);
	}

	public int LA(final int i) {
		return _lookahead.LA(i);
	}

	public String match(final Type type) {
		if (LA(1) == type.code()) {
			final var value = LT(1).value();
			consume();
			return value;
		} else {
			new Exception().printStackTrace();
			throw new ParseException(format(
				"Expecting %s but found %s: %s.",
				type, LT(1), _lookahead
			));
		}
	}

	protected void consume() {
		_lookahead.add(_tokenizer.next());
	}

}
