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

/**
 * Base class for all tokenizers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public abstract class CharSequenceTokenizer<T extends Token>
	implements Tokenizer<T>
{

	private static final char EOF = (char)-1;

	private final CharSequence _input;

	protected int pos = 0;
	protected char c = EOF;

	/**
	 * Create a new tokenizer from the given character sequence.
	 *
	 * @param input the input character sequence
	 * @throws NullPointerException if the given {@code input} is {@code null}
	 */
	protected CharSequenceTokenizer(final CharSequence input) {
		requireNonNull(input);

		if (input.length() > 0) {
			c = input.charAt(0);
		}
		_input = input;
	}

	protected void match(final char ch) {
		if (ch == c) {
			consume();
		} else {
			throw new ParsingException(format(
				"Got invalid character '%s' at position '%d'; expected '%s'",
				c, pos, ch
			));
		}
	}

	protected void consume() {
		if (pos + 1 >= _input.length()) {
			c = EOF;
		} else {
			c = _input.charAt(++pos);
		}
	}

	protected final boolean isNonEof(final char ch) {
		return ch != EOF;
	}

}
