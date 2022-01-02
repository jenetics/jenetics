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
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.RPAREN;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.TIMES;

import java.util.Set;
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

	private final Set<String> _variables;
	private final Set<String> _functions;

	public MathExprParser(
		final MathExprTokenizer tokenizer,
		final Set<String> variables,
		final Set<String> functions
	) {
		super(tokenizer, 1);
		_variables = Set.copyOf(variables);
		_functions = Set.copyOf(functions);
	}

	public TreeNode<String> parse() {
		return expr();
	}

	//////////////// EXPR START
	private TreeNode<String> expr() {
		return term_10_op_sum(term_10_sum());
	}

	///////// SUM operations /////////////

	private TreeNode<String> term_10_op_sum(final TreeNode<String> expr) {
		var result = expr;

		if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var value = match(LT(1).type()).value();
			final var node = TreeNode.of(value)
				.attach(expr)
				.attach(term_10_sum());

			result = term_10_op_sum(node);
		}

		return result;
	}

	private TreeNode<String> term_10_sum() {
		return term_11_op_mult(term_11_mult());
	}

	///////////// MULT operations //////////////

	private TreeNode<String> term_11_op_mult(final TreeNode<String> expr) {
		var result = expr;
		if (LA(1) == TIMES.code() || LA(1) == DIV.code()) {
			final var value = match(LT(1).type()).value();
			final var node = TreeNode.of(value)
				.attach(expr)
				.attach(term_11_mult());

			result = term_11_op_mult(node);
		}

		return result;
	}

	private TreeNode<String> term_11_mult() {
		return term_12_op_pow(term_12_pow());
	}

	//////////////////// POW operations ///////////////////////////

	private TreeNode<String> term_12_op_pow(final TreeNode<String> expr) {
		var result = expr;
		if (LA(1) == POW.code()) {
			final var value = match(LT(1).type()).value();
			final var node = TreeNode.of(value)
				.attach(expr)
				.attach(term_12_pow());

			result = term_12_op_pow(node);
		}

		return result;
	}

	private TreeNode<String> term_12_pow() {
		return term_12_op_pow(signed(this::function));
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

}
