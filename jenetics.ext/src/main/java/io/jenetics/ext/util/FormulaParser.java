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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.jenetics.ext.internal.parser.BaseParser;
import io.jenetics.ext.internal.parser.ParsingException;

/**
 * General parser <em>configuration</em> of mathematical expressions, aka
 * formulas, parser. The input for the parser is a sequence of tokens.
 *
 * @param <T> the token value type used as input for the parser
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class FormulaParser<T> {

	/* *************************************************************************
	 * Main class implementation.
	 * ************************************************************************/

	private final T _lparen;
	private final T _rparen;
	private final T _comma;
	private final Set<? extends T> _uops;
	private final Predicate<? super T> _identifier;
	private final Predicate<? super T> _functions;

	private final Term<T> _term;

	/**
	 * Creates a new general expression parser object. The parser is not bound
	 * to a specific source and target type or concrete token types.
	 *
	 * @param lparen the token type specifying the left parentheses, '('
	 * @param rparen the token type specifying the right parentheses, ')'
	 * @param comma the token type specifying the function parameter separator,
	 *        ','
	 * @param bops the list of binary operators, according its
	 *        precedence. The first list element contains the operations with
	 *        the lowest precedence and the last list element contains the
	 *        operations with the highest precedence.
	 * @param uops the token types representing the unary operations
	 * @param identifier the token type representing identifier, like variable
	 *        names, constants or numbers
	 * @param functions predicate which tests whether a given identifier value
	 *        represents a known function name
	 */
	public FormulaParser(
		final T lparen,
		final T rparen,
		final T comma,
		final List<? extends Set<? extends T>> bops,
		final Set<? extends T> uops,
		final Predicate<? super T> identifier,
		final Predicate<? super T> functions
	) {
		_lparen = requireNonNull(lparen);
		_rparen = requireNonNull(rparen);
		_comma = requireNonNull(comma);
		_uops = Set.copyOf(uops);
		_identifier = requireNonNull(identifier);
		_functions = requireNonNull(functions);

		final Term<T> oterm = BopTerm.build(bops);
		final Term<T> fterm = new Term<>() {
			@Override
			<V> TreeNode<V> term(
				final BaseParser<T> parser,
				final Function<? super T, ? extends V> mapper
			) {
				return function(parser, mapper);
			}
		};
		if (oterm != null) {
			oterm.append(fterm);
			_term = oterm;
		} else {
			_term = fterm;
		}
	}

	/**
	 * Creates a new general expression parser object. The parser is not bound
	 * to a specific source and target type or concrete token types.
	 *
	 * @param lparen the token type specifying the left parentheses, '('
	 * @param rparen the token type specifying the right parentheses, ')'
	 * @param comma the token type specifying the function parameter separator,
	 *        ','
	 * @param bops the list of binary operators, according its
	 *        precedence. The first list element contains the operations with
	 *        the lowest precedence and the last list element contains the
	 *        operations with the highest precedence.
	 * @param uops the token types representing the unary operations
	 * @param identifier the set of identifier, like variable names, constants
	 *        or numbers
	 * @param functions predicate which tests whether a given identifier value
	 *        represents a known function name
	 */
	public FormulaParser(
		final T lparen,
		final T rparen,
		final T comma,
		final List<? extends Set<? extends T>> bops,
		final Set<? extends T> uops,
		final Set<? extends T> identifier,
		final Set<? extends T> functions
	) {
		this(
			lparen, rparen, comma, bops, uops,
			Set.copyOf(identifier)::contains,
			Set.copyOf(functions)::contains
		);
	}

	private <V> TreeNode<V> function(
		final BaseParser<T> parser,
		final Function<? super T, ? extends V> mapper
	) {
		final var token = parser.LT(1);
		if (isFun(token)) {
			parser.consume();
			final var node = TreeNode.<V>of(mapper.apply(token));

			parser.match(_lparen);
			node.attach(_term.expr(parser, mapper));
			while (_comma.equals(parser.LT(1))) {
				parser.consume();
				node.attach(_term.expr(parser, mapper));
			}
			parser.match(_rparen);

			return node;
		} else if (_lparen.equals(token)) {
			parser.consume();
			final TreeNode<V> node = _term.expr(parser, mapper);
			parser.match(_rparen);
			return node;
		} else {
			return unary(() -> atom(parser, mapper), parser, mapper);
		}
	}

	private <V> TreeNode<V> atom(
		final BaseParser<T> parser,
		final Function<? super T, ? extends V> mapper
	) {
		final var token = parser.LT(1);

		if (isAtom(token)) {
			parser.consume();
			return TreeNode.of(mapper.apply(token));
		} else if (token == null) {
			throw new ParsingException("Unexpected end of input.");
		} else {
			throw new ParsingException(
				"Unexpected symbol found: %s.".formatted(parser.LT(1))
			);
		}
	}

	private <V> TreeNode<V> unary(
		final Supplier<TreeNode<V>> other,
		final BaseParser<T> parser,
		final Function<? super T, ? extends V> mapper
	) {
		final var token = parser.LT(1);
		if (token != null && _uops.contains(token)) {
			parser.consume();
			return TreeNode.<V>of(mapper.apply(token)).attach(other.get());
		} else {
			return other.get();
		}
	}

	private boolean isFun(final T token) {
		return token != null && _functions.test(token);
	}

	private boolean isAtom(final T token) {
		return token != null &&
			(_identifier.test(token) || _functions.test(token));
	}

	/**
	 * Parses the given token sequence according {@code this} formula definition.
	 *
	 * @param tokens the tokens which forms the formula
	 * @param mapper the mapper function which maps the token type to the parse
	 *        tree value type
	 * @return the parsed formula as tree
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <V> Tree<V, ?> parse(
		final Iterator<? extends T> tokens,
		final Function<? super T, ? extends V> mapper
	) {
		requireNonNull(tokens);
		requireNonNull(mapper);

		final var parser = new BaseParser<T>(
			() -> tokens.hasNext() ? tokens.next() : null,
			1
		);
		return _term.expr(parser, mapper);
	}

	/**
	 * Parses the given token sequence according {@code this} formula definition.
	 *
	 * @param tokens the tokens which forms the formula
	 * @return the parsed formula as tree
	 * @throws NullPointerException if the arguments is {@code null}
	 */
	public Tree<T, ?> parse(final Iterator<? extends T> tokens) {
		return parse(tokens, Function.identity());
	}

	/**
	 * Parses the given token sequence according {@code this} formula definition.
	 *
	 * @param tokens the tokens which forms the formula
	 * @param mapper the mapper function which maps the token type to the parse
	 *        tree value type
	 * @return the parsed formula as tree
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <V> Tree<V, ?> parse(
		final Iterable<? extends T> tokens,
		final Function<? super T, ? extends V> mapper
	) {
		return parse(tokens.iterator(), mapper);
	}

	/**
	 * Parses the given token sequence according {@code this} formula definition.
	 *
	 * @param tokens the tokens which forms the formula
	 * @return the parsed formula as tree
	 * @throws NullPointerException if the arguments is {@code null}
	 */
	public Tree<T, ?> parse(final Iterable<? extends T> tokens) {
		return parse(tokens, Function.identity());
	}


	/* *************************************************************************
	 * Formula helper classes
	 * ************************************************************************/

	/**
	 * General term object to be parsed.
	 *
	 * @param <T> the token value type used as input for the parser
	 */
	private static abstract class Term<T> {
		Term<T> _next;
		Term<T> _last;

		<V> TreeNode<V> op(
			final TreeNode<V> expr,
			final BaseParser<T> parser,
			final Function<? super T, ? extends V> mapper
		) {
			return expr;
		}

		abstract <V> TreeNode<V> term(
			final BaseParser<T> parser,
			final Function<? super T, ? extends V> mapper
		);

		<V> TreeNode<V> expr(
			final BaseParser<T> parser,
			final Function<? super T, ? extends V> mapper
		) {
			return op(term(parser, mapper), parser, mapper);
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
	private static class BopTerm<T> extends Term<T> {
		private final Set<? extends T> _tokens;

		BopTerm(final Set<? extends T> tokens) {
			_tokens = requireNonNull(tokens);
		}

		@Override
		<V> TreeNode<V> op(
			final TreeNode<V> expr,
			final BaseParser<T> parser,
			final Function<? super T, ? extends V> mapper
		) {
			var result = expr;

			final var token = parser.LT(1);
			if (token != null && _tokens.contains(token)) {
				parser.consume();

				final TreeNode<V> node = TreeNode.<V>of(mapper.apply(token))
					.attach(expr)
					.attach(term(parser, mapper));

				result = op(node, parser, mapper);
			}
			return result;
		}

		@Override
		<V> TreeNode<V> term(
			final BaseParser<T> parser,
			final Function<? super T, ? extends V> mapper
		) {
			return _next.op(_next.term(parser, mapper), parser, mapper);
		}

		/**
		 * Builds a linked chain of binary operations. Operations with lower
		 * <em>precedence</em> are at the beginning of the chain and operations
		 * with higher <em>precedence</em> are appended to the end of the linked
		 * operation term chain.
		 *
		 * @param bops the list of binary operations with a given precedence
		 * @param <T> the token value type used as input for the parser
		 * @return the linked operation term
		 */
		static <T> BopTerm<T> build(final List<? extends Set<? extends T>> bops) {
			BopTerm<T> start = null;
			for (var tokens : bops) {
				final BopTerm<T> term = new BopTerm<>(tokens);
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
