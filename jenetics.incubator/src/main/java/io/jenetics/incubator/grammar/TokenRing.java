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
package io.jenetics.incubator.grammar;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ring-buffer for storing lookup tokens.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
final class TokenRing<T extends Token> {
	private final Token[] _tokens;

	private int _pos = 0;

	TokenRing(final int k) {
		_tokens = new Token[k];
	}

	void add(final T token) {
		_tokens[_pos] = token;
		_pos = (_pos + 1)%_tokens.length;
	}

	@SuppressWarnings("unchecked")
	public T LT(final int i) {
		return (T)_tokens[(_pos + i - 1)%_tokens.length];
	}

	@Override
	public String toString() {
		return IntStream.rangeClosed(1, _tokens.length)
			.mapToObj(i -> i + ":'" + LT(i).value() + "'")
			.collect(Collectors.joining(", ", "[", "]"));
	}

}
