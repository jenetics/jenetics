package io.jenetics.incubator.parser;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.function.Function;

public class IterableTokenizer<A, V> implements Tokenizer<V> {

	private final Iterator<A> _values;
	private final Function<? super A, Token<V>> _converter;

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
