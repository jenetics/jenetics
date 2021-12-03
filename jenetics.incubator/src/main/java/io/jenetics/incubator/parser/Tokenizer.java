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

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Base class for all tokenizers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public abstract class Tokenizer {

	private static final char EOF = (char)-1;

	private final CharSequence _input;

	protected int pos;
	protected char c = EOF;

	/**
	 * Create a new tokenizer from the given character sequence.
	 *
	 * @param input the input character sequence
	 * @throws NullPointerException if the given {@code input} is {@code null}
	 */
	protected Tokenizer(final CharSequence input) {
		if (input.length() > 0) {
			c = input.charAt(0);
		}
		_input = input;
	}

	public abstract Token next();

	public final Tokenizer filter(final Predicate<? super Token> filter) {
		return new Tokenizer("") {
			@Override
			public Token next() {
				var token = Tokenizer.this.next();
				while (!filter.test(token) && token != Token.EOF) {
					token = Tokenizer.this.next();
				}
				return token;
			}
		};
	}

	public final Stream<Token> tokens() {
		return Stream.generate(this::next)
			.takeWhile(token -> token.type().code() != Token.Type.EOF.code());
	}

	protected void match(final char ch) {
		if (ch == c) {
			consume();
		} else {
			throw new TokenizerException(format(
				"Got invalid character '%s' at position '%d'; expected '%s'",
				c, pos, ch
			));
		}
	}

	protected void consume() {
		++pos;

		if (pos >= _input.length()) {
			c = EOF;
		} else {
			c = _input.charAt(pos);
		}
	}

	protected final boolean isNonEof(final char ch) {
		return ch != EOF;
	}

}
