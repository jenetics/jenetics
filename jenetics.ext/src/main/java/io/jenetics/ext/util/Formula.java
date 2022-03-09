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
package io.jenetics.ext.util;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import io.jenetics.ext.internal.parser.BaseParser;
import io.jenetics.ext.internal.parser.ParsingException;
import io.jenetics.ext.internal.parser.Token;
import io.jenetics.ext.internal.parser.Tokenizer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class Formula<T> {

	/* *************************************************************************
	 * Main class implementation.
	 * ************************************************************************/

	private final T _lparen;
	private final T _rparen;
	private final T _comma;
	private final Set<? extends T> _uops;
	private final Set<T> _identifier;
	private final Set<T> _functions;

	private final Term<T> _term;

	public Formula(
		final T lparen,
		final T rparen,
		final T comma,
		final List<? extends Set<? extends T>> bops,
		final Set<? extends T> uops,
		final Set<? extends T> identifier,
		final Set<? extends T> functions
	) {
		_lparen = requireNonNull(lparen);
		_rparen = requireNonNull(rparen);
		_comma = requireNonNull(comma);
		_uops = Set.copyOf(uops);
		_identifier = Set.copyOf(identifier);
		_functions = Set.copyOf(functions);

		final Term<T> oterm = OpTerm.build(bops);
		final Term<T> fterm = new Term<T>() {
			@Override
			TreeNode<T> term(final BaseParser<T> parser) {
				return function(parser);
			}
		};
		if (oterm != null) {
			oterm.append(fterm);
			_term = oterm;
		} else {
			_term = fterm;
		}
	}

	private TreeNode<T> function(final BaseParser<T> parser) {
		if (isFun(parser.LT(1))) {
			final var token = parser.match(parser.LT(1));
			var node = TreeNode.of(token);

			parser.match(_lparen);
			node.attach(_term.expr(parser));
			while (Objects.equals(parser.LT(1), _comma)) {
				parser.consume();
				node.attach(_term.expr(parser));
			}
			parser.match(_rparen);

			return node;
		} else if (Objects.equals(parser.LT(1), _lparen)) {
			parser.consume();
			final var node = _term.expr(parser);
			parser.match(_rparen);
			return node;
		} else {
			return unary(() -> atom(parser), parser);
		}
	}

	private TreeNode<T> atom(final BaseParser<T> parser) {
		final var token = parser.LT(1);

		if (isAtom(parser.LT(1))) {
			parser.consume();
			return TreeNode.of(token);
		} else if (parser.LT(1) == Token.EOF) {
			throw new ParsingException("Unexpected end of input.");
		} else {
			throw new ParsingException(
				"Unexpected symbol found: %s.".formatted(parser.LT(1))
			);
		}
	}

	private TreeNode<T> unary(
		final Supplier<TreeNode<T>> other,
		final BaseParser<T> parser
	) {
		if (_uops.contains(parser.LT(1))) {
			final var token = parser.match(parser.LT(1));
			return TreeNode.of(token).attach(other.get());
		} else {
			return other.get();
		}
	}

	private boolean isFun(final T token) {
		return _functions.contains(token);
	}

	private boolean isAtom(final T token) {
		return _identifier.contains(token) || _functions.contains(token);
	}

	public Tree<T, ?> parse(final Iterable<? extends T> tokens) {
		final var tokenizer = new IterableTokenizer<T>(tokens);
		final var parser = new BaseParser<>(tokenizer, 1);
		return _term.expr(parser);
	}

	/* *************************************************************************
	 * Formula helper classes
	 * ************************************************************************/

	private static class IterableTokenizer<T> implements Tokenizer<T> {

		private final Iterator<? extends T> _values;

		/**
		 * Creates a new tokenizer adapter
		 *
		 * @param values the source values to adapt
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public IterableTokenizer(final Iterable<? extends T> values) {
			_values = values.iterator();
		}

		@Override
		public T next() {
			return _values.hasNext() ? _values.next() : null;
		}

	}


	/**
	 * General term object to be parsed.
	 *
	 * @param <T> the token value type used as input for the parser
	 */
	private static abstract class Term<T> {
		Term<T> _next;
		Term<T> _last;

		TreeNode<T> op(final TreeNode<T> expr, final BaseParser<T> parser) {
			return expr;
		}

		abstract TreeNode<T> term(final BaseParser<T> parser);

		TreeNode<T> expr(final BaseParser<T> parser) {
			return op(term(parser), parser);
		}

		void append(final Term<T> term) {
			if (_next == null) {
				_next = term;
				_last = term;
			} else {
				_last.append(term);
			}
		}
	}

	/**
	 * Represents a binary (mathematical) operation.
	 *
	 * @param <T> the token value type used as input for the parser
	 */
	private static class OpTerm<T> extends Term<T> {
		private final Set<? extends T> _tokens;

		OpTerm(final Set<? extends T> tokens) {
			_tokens = requireNonNull(tokens);
		}

		@Override
		TreeNode<T> op(final TreeNode<T> expr, final BaseParser<T> parser) {
			var result = expr;
			if (_tokens.contains(parser.LT(1))) {
				final T token = parser.match(parser.LT(1));

				final TreeNode<T> node = TreeNode.of(token)
					.attach(expr)
					.attach(term(parser));

				result = op(node, parser);
			}
			return result;
		}

		@Override
		TreeNode<T> term(final BaseParser<T> parser) {
			return _next.op(_next.term(parser), parser);
		}

		/**
		 * Builds a linked chain of binary operations. Operations with lower
		 * <em>precedence</em> are at the beginning of the chain and operations
		 * with higher <em>precedence</em> are appended to the end of the linked
		 * operation term chain.
		 *
		 * @param ops the list of binary operations with a given precedence
		 * @param <T> the token value type used as input for the parser
		 * @return the linked operation term
		 */
		static <T> OpTerm<T> build(final List<? extends Set<? extends T>> ops) {
			OpTerm<T> start = null;
			for (var tokens : ops) {
				final OpTerm<T> term = new OpTerm<>(tokens);
				if (start == null) {
					start = term;
				} else {
					start.append(term);
				}
			}

			return start;
		}
	}

}
