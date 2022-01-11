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

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Adapter class which lets you treat an {@link Iterable} of type {@code A} as
 * a {@link Tokenizer} with token type {@code V}.
 *
 * @param <A> the <em>raw</em> token type
 * @param <V> the <em>transformed</em> token value type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public class IterableTokenizer<A, V> implements Tokenizer<V> {

	private final Iterator<A> _values;
	private final Function<? super A, Token<V>> _converter;

	/**
	 * Creates a new tokenizer adapter
	 *
	 * @param values the source values to adapt
	 * @param converter the conversion function, which converts the source values
	 *        to token values
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public IterableTokenizer(
		final Iterable<A> values,
		final Function<? super A, Token<V>> converter
	) {
		_values = values.iterator();
		_converter = requireNonNull(converter);
	}

	@Override
	public Token<V> next() {
		return _values.hasNext()
			? _converter.apply(_values.next())
			: Token.eof();
	}

}
