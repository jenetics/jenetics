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

import java.util.Objects;

import io.jenetics.incubator.parser.Token.Type;

public abstract class Parser {

	private final Tokenizer _tokenizer;
	private Token _lookahead;

	protected Parser(final Tokenizer tokenizer) {
		_tokenizer = requireNonNull(tokenizer);
	}

	protected void match(final Type type) {
		if (Objects.equals(_lookahead.type(), type)) {
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
