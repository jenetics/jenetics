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

import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.COMMA;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.DIV;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.ID;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.LPAREN;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.MINUS;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.NUMBER;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.OP;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.RPAREN;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.TIMES;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import io.jenetics.incubator.parser.Parser;
import io.jenetics.incubator.parser.ParsingException;
import io.jenetics.incubator.parser.Token;

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
public class MathExprParser extends Parser<Token>  {

	interface Precedence {
		TreeNode<String> termOp(final TreeNode<String> expr);
		TreeNode<String> term();

		default TreeNode<String> expr() {
			return termOp(term());
		}
	}

	static final List<List<Token.Type>> OPS = List.of(
		List.of(PLUS, MINUS),
		List.of(TIMES, DIV),
		List.of(POW)
	);

	private final Set<String> _variables;
	private final Set<String> _functions;

	private final Precedence _precedence;

	public MathExprParser(
		final MathExprTokenizer tokenizer,
		final Set<String> variables,
		final Set<String> functions
	) {
		super(tokenizer, 1);
		_variables = Set.copyOf(variables);
		_functions = Set.copyOf(functions);

		var ops = OPS.get(OPS.size() - 1);
		Precedence pre = new Precedence() {
			@Override
			public TreeNode<String> termOp(final TreeNode<String> expr) {
				return term_op(expr, ops, this::term);
			}
			@Override
			public TreeNode<String> term() {
				return termOp(signed(MathExprParser.this::function));
			}
		};

		for (int i = 1; i < OPS.size(); ++i) {
			final var currentOps = OPS.get(OPS.size() - i - 1);
			final var lastPre = pre;
			pre = new Precedence() {
				@Override
				public TreeNode<String> termOp(final TreeNode<String> expr) {
					return term_op(expr, currentOps, this::term);
				}
				@Override
				public TreeNode<String> term() {
					return termOp(lastPre.term());
				}
			};
		}

		_precedence = pre;
	}

	public TreeNode<String> parse() {
		//return expr();
		return _precedence.expr();
	}



	//////////////// EXPR START
	private TreeNode<String> expr() {
		return term_op_10(term_10());
	}

	///////// SUM operations /////////////

	private TreeNode<String> term_op_10(final TreeNode<String> expr) {
		return term_op(expr, List.of(PLUS, MINUS), this::term_10);
	}

	private TreeNode<String> term_10() {
		return term_op_10(term_11());
	}

	///////////// MULT operations //////////////

	private TreeNode<String> term_op_11(final TreeNode<String> expr) {
		return term_op(expr, List.of(TIMES, DIV), this::term_11);
	}

	private TreeNode<String> term_11() {
		return term_op_11(term_12());
	}

	//////////////////// POW operations ///////////////////////////

	private TreeNode<String> term_op_12(final TreeNode<String> expr) {
		return term_op(expr, List.of(POW), this::term_12);
	}

	private TreeNode<String> term_12() {
		return term_op_12(signed(this::function));
	}

	/////////////////// functions ////////////////////////////

	private TreeNode<String> function() {
		if (isFun(LT(1))) {
			final var value = match(LT(1).type()).value();
			var node = TreeNode.of(value);

			match(LPAREN);
			node.attach(expr());
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

	private TreeNode<String> atom() {
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

	private TreeNode<String> signed(final Supplier<TreeNode<String>> other) {
		if (LA(1) == MINUS.code()) {
			final var value = match(MINUS).value();

			if (LA(1) == NUMBER.code()) {
				return TreeNode.of(value + match(NUMBER).value());
			} else {
				return TreeNode.of(value).attach(other.get());
			}
		} else if (LA(1) == PLUS.code()) {
			final var value = match(PLUS).value();

			if (LA(1) == NUMBER.code()) {
				return TreeNode.of(match(NUMBER).value());
			} else {
				return TreeNode.of(value).attach(other.get());
			}
		} else {
			return other.get();
		}
	}

	private boolean isVar(final Token token) {
		return token.type().code() == ID.code() &&
			_variables.contains(token.value());
	}

	private boolean isFun(final Token token) {
		return token.type().code() == ID.code() &&
			_functions.contains(token.value());
	}

	private boolean isAtom(final Token token) {
		return token.type().code() == NUMBER.code() ||
			isVar(token);
	}


	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////

	private TreeNode<String> term_op(
		final TreeNode<String> expr,
		final List<Token.Type> tokens,
		final Supplier<TreeNode<String>> term
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
