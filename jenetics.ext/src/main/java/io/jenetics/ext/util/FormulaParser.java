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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
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
public final class FormulaParser<T> {

	private final T _lparen;
	private final T _rparen;
	private final T _comma;
	private final Predicate<? super T> _uops;
	private final Predicate<? super T> _identifiers;
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
	 * @param identifiers the token type representing identifier, like variable
	 *        names, constants or numbers
	 * @param functions predicate which tests whether a given identifier value
	 *        represents a known function name
	 */
	private FormulaParser(
		final T lparen,
		final T rparen,
		final T comma,
		final List<? extends Set<? extends T>> bops,
		final Predicate<? super T> uops,
		final Predicate<? super T> identifiers,
		final Predicate<? super T> functions
	) {
		_lparen = lparen;
		_rparen = rparen;
		_comma = comma;
		_uops = requireNonNull(uops);
		_identifiers = requireNonNull(identifiers);
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
			while (Objects.equals(_comma, parser.LT(1))) {
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
		if (token != null && _uops.test(token)) {
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
			(_identifiers.test(token) || _functions.test(token));
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
	private <V> Tree<V, ?> parse(
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
	private Tree<T, ?> parse(final Iterator<? extends T> tokens) {
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

	public static <T> Builder<T> builder() {
		return new Builder<>();
	}


	/* *************************************************************************
	 * FormulaParser helper classes
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

	/* *************************************************************************
	 * FormulaParser builder class
	 * ************************************************************************/


	public static final class Builder<T> {

		private T _lparen;
		private T _rparen;
		private T _comma;
		private List<? extends Set<? extends T>> _bops = List.of();
		private Predicate<? super T> _uops = token -> false;
		private Predicate<? super T> _identifiers = token -> false;
		private Predicate<? super T> _functions = token -> false;


		private Builder() {
		}

		public Builder<T> lparen(final T lparen) {
			_lparen = requireNonNull(lparen);
			return this;
		}

		public Builder<T> rparen(final T rparen) {
			_rparen = requireNonNull(rparen);
			return this;
		}

		public Builder<T> comma(final T comma) {
			_comma = requireNonNull(comma);
			return this;
		}

		public Builder<T> unaryOperators(final Predicate<? super T> operators) {
			_uops = requireNonNull(operators);
			return this;
		}

		public Builder<T> unaryOperators(final Set<? extends T> operators) {
			return unaryOperators(Set.copyOf(operators)::contains);
		}

		@SafeVarargs
		public final Builder<T> unaryOperators(final T... operators) {
			return unaryOperators(Set.of(operators));
		}

		public Builder<T>
		binaryOperators(final List<? extends Set<? extends T>> operators) {
			_bops = operators.stream()
				.map(Set::copyOf)
				.toList();
			return this;
		}

		public Builder<T>
		binaryOperators(final Consumer<? super BinaryOperators<T>> operators) {
			final var builder = new BinaryOperators<T>();
			operators.accept(builder);
			_bops = builder.build();
			return this;
		}

		public Builder<T> identifiers(final Predicate<? super T> identifiers) {
			_identifiers = requireNonNull(identifiers);
			return this;
		}

		public Builder<T> identifiers(final Set<? extends T> identifiers) {
			return identifiers(Set.copyOf(identifiers)::contains);
		}

		@SafeVarargs
		public final Builder<T> identifiers(final T... identifiers) {
			return identifiers(Set.of(identifiers));
		}

		public Builder<T> functions(final Predicate<? super T> functions) {
			_functions = requireNonNull(functions);
			return this;
		}

		public Builder<T> functions(final Set<? extends T> functions) {
			return functions(Set.copyOf(functions)::contains);
		}

		@SafeVarargs
		public final Builder<T> functions(final T... functions) {
			return functions(Set.of(functions));
		}

		public FormulaParser<T> build() {
			if (_lparen == null) {
				throw new IllegalStateException("'lparen' must not be null.");
			}
			if (_rparen == null) {
				throw new IllegalStateException("'rparen' must not be null.");
			}
			if (_comma == null) {
				throw new IllegalStateException("'comma' must not be null.");
			}

			return new FormulaParser<>(
				_lparen,
				_rparen,
				_comma,
				_bops,
				_uops,
				_identifiers,
				_functions
			);
		}

		public static final class BinaryOperators<T> {
			private final Map<Integer, Set<T>> _operations = new HashMap<>();

			private BinaryOperators() {
			}

			@SafeVarargs
			public final BinaryOperators<T> add(
				final int precedence,
				final T... operators
			) {
				final var ops = _operations.get(precedence);
				if (ops != null) {
					final Set<T> set = new HashSet<>();
					set.addAll(ops);
					set.addAll(List.of(operators));

					_operations.put(precedence, Set.copyOf(set));
				} else {
					_operations.put(precedence, Set.of(operators));
				}

				return this;
			}

			List<Set<T>> build() {
				return _operations.entrySet().stream()
					.sorted(Entry.comparingByKey())
					.map(Entry::getValue)
					.toList();
			}

		}
	}

}
