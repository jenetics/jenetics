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

	private TreeNode<String> _tree = TreeNode.of();

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
		return term_10_op_sum(signed_term_10_sum());
	}

	///////// SUM /////////////

	private TreeNode<String> signed_term_10_sum() {
		if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var value = match(LT(1).type()).value();
			return TreeNode.of(value).attach(term_10_sum());
		} else {
			return term_10_sum();
		}
	}

	private TreeNode<String> term_10_op_sum(final TreeNode<String> expr) {
		var result = expr;
		if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var value = match(LT(1).type()).value();
			final var node = TreeNode.of(value).attach(expr);

			node.attach(term_10_sum());
			result = term_10_op_sum(node);
		}

		return result;
	}

	private TreeNode<String> term_10_sum() {
		return term_11_op_mult(signed_term_11_mult());
	}

	///////////////////////////

	private TreeNode<String> signed_term_11_mult() {
		if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var value = match(LT(1).type()).value();
			return TreeNode.of(value).attach(term_11_mult());
		} else {
			return term_11_mult();
		}
	}

	private TreeNode<String> term_11_op_mult(final TreeNode<String> expr) {
		var result = expr;
		if (LA(1) == TIMES.code() || LA(1) == DIV.code()) {
			final var value = match(LT(1).type()).value();
			final var node = TreeNode.of(value).attach(expr);

			node.attach(term_11_mult());
			result = term_11_op_mult(node);
		}

		return result;
	}

	private TreeNode<String> term_11_mult() {
		return term_11_op_mult(signed_term_12_pow());
	}

	///////////////////////////////////////////////

	private TreeNode<String> signed_term_12_pow() {
		if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var value = match(LT(1).type()).value();
			return TreeNode.of(value).attach(term_11_mult());
		} else {
			return term_12_pow();
		}
	}

	private TreeNode<String> term_12_op_pow(final TreeNode<String> expr) {
		var result = expr;
		if (LA(1) == POW.code()) {
			final var value = match(LT(1).type()).value();
			final var node = TreeNode.of(value).attach(expr);

			node.attach(term_12_pow());
			result = term_12_op_pow(node);
		}

		return result;
	}

	private TreeNode<String> term_12_pow() {
		return term_12_op_pow(function());
	}

	///////////////////////////////////////////////

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
			final var node = function();
			match(RPAREN);
			return node;
		} else {
			return signed_atom();
		}
	}


	///////////////////////////////////////////////

	private TreeNode<String> signed_atom() {
		if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var value = match(LT(1).type()).value();
			return TreeNode.of(value).attach(atom());
		} else {
			return atom();
		}
	}

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

	/*
	private TreeNode<String> expr() {
		var term = term_11();
		if (term == null || LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var a = expr();
			if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
				final var symbol = LT(1).value();
				consume();

				final var b = term_11();
				final var node = TreeNode.of(symbol);
				node.attach(a);
				node.attach(b);
				if (term != null) {
					term.attach(node);
				} else {
					term = node;
				}
			}
		}

		return term;
	}

	private TreeNode<String> term11_sum(final TreeNode<String> expr) {
		var term = expr;

		if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {

		}

		return expr;
	}

	private TreeNode<String> term14_sign() {
		if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var value = match(LT(1).type()).value();
			return TreeNode.of(value).attach(term());
		} else {
			return term();
		}
	}

	private TreeNode<String> term_11() {
		var term = term_12();
		if (term == null) {
			final var a = term_11();
			if (LA(1) == TIMES.code() || LA(1) == DIV.code()) {
				final var symbol = LT(1).value();
				consume();

				final var b = term_12();
				term = TreeNode.of(symbol);
				term.attach(a);
				term.attach(b);
			}
		}

		return term;
	}

	private TreeNode<String> term_12() {
		var term = term_13();
		if (term == null) {
			final var a = term_12();
			if (LA(1) == POW.code()) {
				final var symbol = match(POW).value();
				final var b = term_13();
				term = TreeNode.of(symbol);
				term.attach(a);
				term.attach(b);
			}
		}

		return term;
	}

	private TreeNode<String> term_13() {
		var term = term_14();
		if (term == null) {
			if (LA(1) == LPAREN.code()) {
				match(LPAREN);
				term = expr();
				match(RPAREN);
			}
		}

		return term;
	}

	private TreeNode<String> term_14() {
		TreeNode<String> prefix = null;
		while (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
			final var value = match(LT(1).type()).value();
			final var node = TreeNode.of(value);

			if (prefix != null) {
				prefix.attach(node);
			}
			prefix = node;
		}

		final var term = term_16();
		if (prefix == null) {
			prefix = term;
		} else if (term != null ) {
			prefix.attach(term);
		}

		return prefix;
	}

	private TreeNode<String> term_16() {
		if (isFun(LT(1))) {
			final var name = match(ID).value();
			match(LPAREN);

			final var fun = TreeNode.of(name);
			fun.attach(expr());
			while (LA(1) == COMMA.code()) {
				match(COMMA);
				fun.attach(expr());
			}
			match(RPAREN);
			return fun;
		} else if (isAtom(LT(1))) {
			return atom();
		} else {
			return null;
		}
	}

	private TreeNode<String> atom() {
		if (isAtom(LT(1))) {
			final var value = match(LT(1).type()).value();
			return TreeNode.of(value);
		} else {
			return null;
		}
	}

	private TreeNode<String> args() {

	}
	 */

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
