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
 *
 * expr:    term_11 | expr PLUS term_11 | expr MINUS term_11
 * term_11: term_12 | term_11 TIMES term_12 | term_11 DIV term_12
 * term_12: term_13 | term_12 POW term_13
 * term_13: term_14 | LPAREN expr RPAREN
 * term_14: (PLUS | MINUS)* term_16
 * term_16: fun LPAREN expr (COMMA, expr)* RPAREN | atom
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

	protected MathExprParser(
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

	private TreeNode<String> expr() {
		var term = term_11();
		if (term == null) {
			final var a = expr();
			if (LA(1) == PLUS.code() || LA(1) == MINUS.code()) {
				final var symbol = LT(1).value();
				consume();

				final var b = term_11();
				term = TreeNode.of(symbol);
				term.attach(a);
				term.attach(b);
			}
		}

		return term;
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
			_functions.contains(token.value());
	}

}
