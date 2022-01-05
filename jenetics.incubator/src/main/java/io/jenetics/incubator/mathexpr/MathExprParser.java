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
package io.jenetics.incubator.mathexpr;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import io.jenetics.incubator.parser.Parser;
import io.jenetics.incubator.parser.ParsingException;
import io.jenetics.incubator.parser.Token;
import io.jenetics.incubator.parser.Tokenizer;

import io.jenetics.ext.util.TreeNode;

/**
 * Parser for simple arithmetic expressions.
 *
 * <pre>{@code
 * expression:
 *       expression POW expression
 *    |  expression (TIMES | DIV)  expression
 *    |  expression (PLUS | MINUS) expression
 *    |  LPAREN expression RPAREN
 *    |  fun LPAREN expression (COMMA expression)* RPAREN
 *    |  (PLUS | MINUS)? atom;
 *
 * atom: NUMBER | var;
 * var: ID
 * fun: ID
 *
 * expr: term | expr + term | expr - term
 * term: fact | term * fact | term / fact
 * fact: atom | ( expr )
 * atom: NUMBER
 *
 *
 * expr: term_09? term_10
 * term_09: (PLUS | MINUS)
 * term_10: term_11 | expr PLUS term_11 | expr MINUS term_11
 * term_11: term_12 | term_11 TIMES term_12 | term_11 DIV term_12
 * term_12: term_13 | term_12 POW term_13
 * term_13: term_14 | LPAREN expr RPAREN
 * term_16: fun LPAREN expr (COMMA, expr)* RPAREN | atom
 * args:
 * atom:    NUMBER | var
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public class MathExprParser<T, V> extends Parser<T>  {

	/**
	 * Parsing interface which obeys the operation precedence.
	 */
	private interface OpTerm<V> {
		TreeNode<V> op(final TreeNode<V> expr);
		TreeNode<V> term();
		default TreeNode<V> expr() {
			return op(term());
		}
	}

	private final Function<? super Token<T>, ? extends V> _converter;
	private final Token.Type _lparen;
	private final Token.Type _rparen;
	private final Token.Type _comma;
	private final List<? extends Set<? extends Token.Type>> _binaries;
	private final Set<? extends Token.Type> _unaries;
	private final Token.Type _number;
	private final Token.Type _identifier;
	private final Set<? extends T> _functions;

	private final OpTerm<V> _term;

	public MathExprParser(
		final Tokenizer<T> tokenizer,
		final Function<? super Token<T>, ? extends V> converter,
		final Token.Type lparen,
		final Token.Type rparen,
		final Token.Type comma,
		final List<? extends Set<? extends Token.Type>> binaries,
		final Set<? extends Token.Type> unaries,
		final Token.Type number,
		final Token.Type identifier,
		final Set<? extends T> functions
	) {
		super(tokenizer, 1);

		_converter = requireNonNull(converter);
		_lparen = requireNonNull(lparen);
		_rparen = requireNonNull(rparen);
		_comma = requireNonNull(comma);
		_binaries = List.copyOf(binaries);
		_unaries = Set.copyOf(unaries);
		_number = requireNonNull(number);
		_identifier = requireNonNull(identifier);
		_functions = Set.copyOf(functions);

		// Initialize the operation parsing, according its precedence.
		OpTerm<V> term = new OpTerm<V>() {
			@Override
			public TreeNode<V> op(final TreeNode<V> expr) {
				return MathExprParser.this.term(
					expr,
					_binaries.get(_binaries.size() - 1),
					this::term
				);
			}
			@Override
			public TreeNode<V> term() {
				return op(unary(MathExprParser.this::function));
			}
		};

		for (int i = 1; i < _binaries.size(); ++i) {
			final var op = _binaries.get(_binaries.size() - i - 1);
			final var last = term;

			term = new OpTerm<V>() {
				@Override
				public TreeNode<V> op(final TreeNode<V> expr) {
					return MathExprParser.this.term(expr, op, this::term);
				}
				@Override
				public TreeNode<V> term() {
					return op(last.term());
				}
			};
		}

		_term = term;
	}

	/**
	 * Return the parsed expression.
	 *
	 * @return the parsed expression
	 */
	public TreeNode<V> parse() {
		return _term.expr();
	}

	private TreeNode<V> function() {
		if (isFun(LT(1))) {
			final var token = match(LT(1).type());
			var node = TreeNode.<V>of(_converter.apply(token));

			match(_lparen);
			node.attach(_term.expr());
			while (LA(1) == _comma.code()) {
				consume();
				node.attach(_term.expr());
			}
			match(_rparen);

			return node;
		} else if (LA(1) == _lparen.code()) {
			consume();
			final var node = _term.expr();
			match(_rparen);
			return node;
		} else {
			return unary(this::atom);
		}
	}

	private TreeNode<V> atom() {
		final var token = LT(1);

		if (isAtom(LT(1))) {
			consume();
			return TreeNode.of(_converter.apply(token));
		} else if (LT(1) == Token.EOF) {
			throw new ParsingException("Unexpected end of input.");
		} else {
			throw new ParsingException(
				"Unexpected symbol found: %s.".formatted(LT(1))
			);
		}
	}

	private TreeNode<V> unary(final Supplier<TreeNode<V>> other) {
		if (_unaries.contains(LT(1).type())) {
			final var token = match(LT(1).type());
			return TreeNode.<V>of(_converter.apply(token)).attach(other.get());
		} else {
			return other.get();
		}
	}

	private boolean isFun(final Token<T> token) {
		return token.type().code() == _identifier.code() &&
			_functions.contains(token.value());
	}

	private boolean isAtom(final Token<T> token) {
		return token.type().code() == _number.code() ||
			token.type().code() == _identifier.code();
	}


	private TreeNode<V> term(
		final TreeNode<V> expr,
		final Set<? extends Token.Type> tokens,
		final Supplier<TreeNode<V>> term
	) {
		var result = expr;

		if (tokens.contains(LT(1).type())) {
			final var token = match(LT(1).type());
			final var node = TreeNode.<V>of(_converter.apply(token))
				.attach(term.get())
				.attach(expr);

			result = term(node, tokens, term);
		}

		return result;
	}

}
