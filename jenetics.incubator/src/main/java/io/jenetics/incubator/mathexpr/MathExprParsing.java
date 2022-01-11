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
import static io.jenetics.incubator.mathexpr.MathTokenType.ATOM;
import static io.jenetics.incubator.mathexpr.MathTokenType.BINARY_OPERATOR;
import static io.jenetics.incubator.mathexpr.MathTokenType.COMMA;
import static io.jenetics.incubator.mathexpr.MathTokenType.DIV;
import static io.jenetics.incubator.mathexpr.MathTokenType.FUN;
import static io.jenetics.incubator.mathexpr.MathTokenType.IDENTIFIER;
import static io.jenetics.incubator.mathexpr.MathTokenType.LPAREN;
import static io.jenetics.incubator.mathexpr.MathTokenType.MINUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.MOD;
import static io.jenetics.incubator.mathexpr.MathTokenType.NUMBER;
import static io.jenetics.incubator.mathexpr.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathTokenType.RPAREN;
import static io.jenetics.incubator.mathexpr.MathTokenType.TIMES;
import static io.jenetics.incubator.mathexpr.MathTokenType.UNARY_OPERATOR;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.jenetics.incubator.parser.Parser;
import io.jenetics.incubator.parser.ParsingException;
import io.jenetics.incubator.parser.Token;
import io.jenetics.incubator.parser.Token.Type;

import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.op.MathOp;

/**
 * General parser <em>configuration</em> of mathematical expressions. This class
 * defines the actual parsing behaviour, which can be shared across different
 * parser instances.
 *
 * @param <T> the token value type used as input for the parser
 * @param <V> the type of the parsed AST
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public final class MathExprParsing<T, V> {

	/**
	 * General term object to be parsed.
	 *
	 * @param <T> the token value type used as input for the parser
	 * @param <V> the type of the parsed AST.
	 */
	private static abstract class Term<T, V> {
		Term<T, V> _next;
		Term<T, V> _last;

		TreeNode<V> op(final TreeNode<V> expr, final Parser<T> parser) {
			return expr;
		}

		abstract TreeNode<V> term(final Parser<T> parser);

		TreeNode<V> expr(final Parser<T> parser) {
			return op(term(parser), parser);
		}

		void append(final Term<T, V> term) {
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
	 * @param <V> the type of the parsed AST.
	 */
	private static class OpTerm<T, V> extends Term<T, V> {
		private final BiFunction<? super Token<T>, ? super Token.Type, ? extends V> _converter;
		private final Set<? extends Type> _tokens;

		OpTerm(
			final BiFunction<? super Token<T>, ? super Token.Type, ? extends V> converter,
			final Set<? extends Token.Type> tokens
		) {
			_converter = requireNonNull(converter);
			_tokens = requireNonNull(tokens);
		}

		@Override
		TreeNode<V> op(final TreeNode<V> expr, final Parser<T> parser) {
			var result = expr;
			if (_tokens.contains(parser.LT(1).type())) {
				final var token = parser.match(parser.LT(1).type());
				final var node = TreeNode
					.<V>of(_converter.apply(token, BINARY_OPERATOR))
					.attach(expr)
					.attach(term(parser));

				result = op(node, parser);
			}
			return result;
		}

		@Override
		TreeNode<V> term(final Parser<T> parser) {
			return _next.op(_next.term(parser), parser);
		}

		/**
		 * Builds a linked chain of binary operations. Operations with lower
		 * <em>precedence</em> are at the beginning of the chain and operations
		 * with higher <em>precedence</em> are appended to the end of the linked
		 * operation term chain.
		 *
		 * @param converter converter function, which converts a token to the
		 *        resulting object of type {@code V}.
		 * @param binaries the list of binary operations with a given precedence
		 * @param <T> the token value type used as input for the parser
		 * @param <V> the type of the parsed AST.
		 * @return the linked operation term
		 */
		static <T, V> OpTerm<T, V> build(
			final BiFunction<? super Token<T>, ? super Token.Type, ? extends V> converter,
			final List<? extends Set<? extends Type>> binaries
		) {
			OpTerm<T, V> start = null;
			for (var tokens : binaries) {
				final OpTerm<T, V> term = new OpTerm<>(converter, tokens);
				if (start == null) {
					start = term;
				} else {
					start.append(term);
				}
			}

			return start;
		}
	}

	private final BiFunction<? super Token<T>, ? super Token.Type, ? extends V> _converter;
	private final Token.Type _lparen;
	private final Token.Type _rparen;
	private final Token.Type _comma;
	private final Set<? extends Token.Type> _unaryOperators;
	private final Set<? extends Token.Type> _identifier;
	private final Predicate<? super T> _functions;

	private final Term<T, V> _term;

	/**
	 * Creates a new general expression parser object. The parser is not bound
	 * to a specific source and target type or concrete token types.
	 *
	 * @param converter the token value conversion function
	 * @param lparen the token type specifying the left parentheses, '('
	 * @param rparen the token type specifying the right parentheses, ')'
	 * @param comma the token type specifying the function parameter separator,
	 *        ','
	 * @param binaryOperators the list of binary operators, according its
	 *        precedence. The first list element contains the operations with
	 *        the lowest precedence and the last list element contains the
	 *        operations with the highest precedence.
	 * @param unaryOperations the token types representing the unary operations
	 * @param identifier the token type representing identifier, like variable
	 *        names, constants or numbers
	 * @param functions predicate which tests whether a given identifier value
	 *        represents a known function name
	 */
	public MathExprParsing(
		final BiFunction<? super Token<T>, ? super Token.Type, ? extends V> converter,
		final Token.Type lparen,
		final Token.Type rparen,
		final Token.Type comma,
		final List<? extends Set<? extends Token.Type>> binaryOperators,
		final Set<? extends Token.Type> unaryOperations,
		final Set<? extends Token.Type> identifier,
		final Predicate<? super T> functions
	) {
		_converter = requireNonNull(converter);
		_lparen = requireNonNull(lparen);
		_rparen = requireNonNull(rparen);
		_comma = requireNonNull(comma);
		_unaryOperators = Set.copyOf(unaryOperations);
		_identifier = Set.copyOf(identifier);
		_functions = requireNonNull(functions);


		final Term<T, V> oterm = OpTerm.build(converter, binaryOperators);
		final Term<T, V> fterm = new Term<T, V>() {
			@Override
			TreeNode<V> term(final Parser<T> parser) {
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

	public TreeNode<V> parse(final Parser<T> parser) {
		return _term.expr(parser);
	}

	private TreeNode<V> function(final Parser<T> parser) {
		if (isFun(parser.LT(1))) {
			final var token = parser.match(parser.LT(1).type());
			var node = TreeNode.<V>of(_converter.apply(token, FUN));

			parser.match(_lparen);
			node.attach(_term.expr(parser));
			while (parser.LA(1) == _comma.code()) {
				parser.consume();
				node.attach(_term.expr(parser));
			}
			parser.match(_rparen);

			return node;
		} else if (parser.LA(1) == _lparen.code()) {
			parser.consume();
			final var node = _term.expr(parser);
			parser.match(_rparen);
			return node;
		} else {
			return unary(() -> atom(parser), parser);
		}
	}

	private TreeNode<V> atom(final Parser<T> parser) {
		final var token = parser.LT(1);

		if (isAtom(parser.LT(1))) {
			parser.consume();
			return TreeNode.of(_converter.apply(token, ATOM));
		} else if (parser.LT(1) == Token.EOF) {
			throw new ParsingException("Unexpected end of input.");
		} else {
			throw new ParsingException(
				"Unexpected symbol found: %s.".formatted(parser.LT(1))
			);
		}
	}

	private TreeNode<V> unary(final Supplier<TreeNode<V>> other, final Parser<T> parser) {
		if (_unaryOperators.contains(parser.LT(1).type())) {
			final var token = parser.match(parser.LT(1).type());
			return TreeNode.<V>of(_converter.apply(token, UNARY_OPERATOR)).attach(other.get());
		} else {
			return other.get();
		}
	}

	private boolean isFun(final Token<T> token) {
		return _identifier.contains(token.type()) &&
			_functions.test(token.value());
	}

	private boolean isAtom(final Token<T> token) {
		return _identifier.contains(token.type());
	}

	/**
	 * Create a new parser with the typical token types, defined in the
	 * {@link MathTokenType} class
	 *
	 * @param converter the token value conversion function
	 * @param functions predicate which tests whether a given identifier value
	 *        represents a known function name
	 * @param <T> the token value type used as input for the parser
	 * @param <V> the type of the parsed AST.
	 * @return a new parser with the typical token types
	 */
	public static <T, V> MathExprParsing<T, V> of(
		final BiFunction<? super Token<T>, ? super Token.Type, ? extends V> converter,
		final Predicate<? super T> functions
	) {
		return new MathExprParsing<>(
			converter,
			LPAREN,
			RPAREN,
			COMMA,
			List.of(
				EnumSet.of(PLUS, MINUS),
				EnumSet.of(TIMES, DIV, MOD),
				EnumSet.of(POW)
			),
			EnumSet.of(PLUS, MINUS),
			EnumSet.of(IDENTIFIER, NUMBER),
			functions
		);
	}

}
