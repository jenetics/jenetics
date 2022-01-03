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

import static io.jenetics.incubator.mathexpr.MathTokenType.COMMA;
import static io.jenetics.incubator.mathexpr.MathTokenType.DIV;
import static io.jenetics.incubator.mathexpr.MathTokenType.ID;
import static io.jenetics.incubator.mathexpr.MathTokenType.LPAREN;
import static io.jenetics.incubator.mathexpr.MathTokenType.MINUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.NUMBER;
import static io.jenetics.incubator.mathexpr.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathTokenType.RPAREN;
import static io.jenetics.incubator.mathexpr.MathTokenType.TIMES;

import java.util.List;
import java.util.Set;
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
public class MathExprParser<V> extends Parser<V>  {

	public record Pred(int level, Token.Type ops) {}

	public record Config(
		Token.Type lparen,
		Token.Type rparen,
		Token.Type comma,
		Token.Type fun,
		Token.Type atom,
		Set<Pred> ops,
		Set<Token.Type> unary
	) {}

	public static final Config DEFAULT_CONFIG = new Config(
		LPAREN,
		RPAREN,
		COMMA,
		null,
		null,
		Set.of(
			new Pred(11, PLUS),
			new Pred(11, MINUS),
			new Pred(14, TIMES),
			new Pred(14, DIV),
			new Pred(15, POW)
		),
		Set.of()
	);

	interface Precedence<V> {
		TreeNode<V> termOp(final TreeNode<V> expr);
		TreeNode<V> term();

		default TreeNode<V> expr() {
			return termOp(term());
		}
	}

	static final List<List<Token.Type>> OPS = List.of(
		List.of(PLUS, MINUS),
		List.of(TIMES, DIV),
		List.of(POW)
	);

	static final List<Token.Type> UNARY_OPS = List.of(
		PLUS, MINUS
	);

	private final Set<V> _variables;
	private final Set<V> _functions;

	private final Precedence<V> _precedence;

	public MathExprParser(
		final Tokenizer<V> tokenizer,
		final Set<V> variables,
		final Set<V> functions
	) {
		super(tokenizer, 1);
		_variables = Set.copyOf(variables);
		_functions = Set.copyOf(functions);

		var ops = OPS.get(OPS.size() - 1);
		Precedence<V> pre = new Precedence<V>() {
			@Override
			public TreeNode<V> termOp(final TreeNode<V> expr) {
				return term_op(expr, ops, this::term);
			}
			@Override
			public TreeNode<V> term() {
				return termOp(signed(MathExprParser.this::function));
			}
		};

		for (int i = 1; i < OPS.size(); ++i) {
			final var currentOps = OPS.get(OPS.size() - i - 1);
			final var lastPre = pre;
			pre = new Precedence<V>() {
				@Override
				public TreeNode<V> termOp(final TreeNode<V> expr) {
					return term_op(expr, currentOps, this::term);
				}
				@Override
				public TreeNode<V> term() {
					return termOp(lastPre.term());
				}
			};
		}

		_precedence = pre;
	}

	public TreeNode<V> parse() {
		//return expr();
		return _precedence.expr();
	}


//	//////////////// EXPR START
//	private TreeNode<V> expr() {
//		return term_op_10(term_10());
//	}
//
//	///////// SUM operations /////////////
//
//	private TreeNode<V> term_op_10(final TreeNode<V> expr) {
//		return term_op(expr, List.of(PLUS, MINUS), this::term_10);
//	}
//
//	private TreeNode<V> term_10() {
//		return term_op_10(term_11());
//	}
//
//	///////////// MULT operations //////////////
//
//	private TreeNode<V> term_op_11(final TreeNode<V> expr) {
//		return term_op(expr, List.of(TIMES, DIV), this::term_11);
//	}
//
//	private TreeNode<V> term_11() {
//		return term_op_11(term_12());
//	}
//
//	//////////////////// POW operations ///////////////////////////
//
//	private TreeNode<V> term_op_12(final TreeNode<V> expr) {
//		return term_op(expr, List.of(POW), this::term_12);
//	}
//
//	private TreeNode<V> term_12() {
//		return term_op_12(signed(this::function));
//	}

	/////////////////// functions ////////////////////////////

	private TreeNode<V> function() {
		if (isFun(LT(1))) {
			final var value = match(LT(1).type()).value();
			var node = TreeNode.of(value);

			match(LPAREN);
			//node.attach(expr());
			node.attach(_precedence.expr());
			while (LA(1) == COMMA.code()) {
				consume();
				node.attach(function());
			}
			match(RPAREN);

			return node;
		} else if (LA(1) == LPAREN.code()) {
			consume();
			final var node = signed(this::function);
			match(RPAREN);
			return node;
		} else {
			return signed(this::atom);
		}
	}


	///////////////////////////////////////////////

	private TreeNode<V> atom() {
		final var value = LT(1).value();

		if (isAtom(LT(1))) {
			consume();
			return TreeNode.of(value);
		} else if (LT(1) == Token.EOF) {
			throw new ParsingException("Unexpected end of input.");
		} else {
			throw new ParsingException(
				"Unexpected symbol found: %s.".formatted(LT(1))
			);
		}
	}

	private TreeNode<V> signed(final Supplier<TreeNode<V>> other) {
		if (matching(UNARY_OPS)) {
		//if (LA(1) == MINUS.code() || LA(1) == PLUS.code()) {
			final var value = match(LT(1).type()).value();
			return TreeNode.of(value).attach(other.get());
		} else {
			return other.get();
		}
	}

	private boolean isVar(final Token<V> token) {
		return token.type().code() == ID.code() &&
			_variables.contains(token.value());
	}

	private boolean isFun(final Token<V> token) {
		return token.type().code() == ID.code() &&
			_functions.contains(token.value());
	}

	private boolean isAtom(final Token<V> token) {
		return token.type().code() == NUMBER.code() ||
			isVar(token);
	}


	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////

	private TreeNode<V> term_op(
		final TreeNode<V> expr,
		final List<Token.Type> tokens,
		final Supplier<TreeNode<V>> term
	) {
		var result = expr;

		if (matching(tokens)) {
			final var value = match(LT(1).type()).value();
			final var node = TreeNode.of(value)
				.attach(expr)
				.attach(term.get());

			result = term_op(node, tokens, term);
		}

		return result;
	}

	private boolean matching(final List<Token.Type> tokens) {
		for (var token : tokens) {
			if (LA(1) == token.code()) {
				return true;
			}
		}
		return false;
	}

}
