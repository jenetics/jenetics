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
import static io.jenetics.incubator.parser.Token.Type.EOF;

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
		expression();

		return _tree;
	}

	private void expression() {
		System.out.println(LT(1));

		// NUMBER
		if (LA(1) == NUMBER.code()) {
			if (_tree.isEmpty()) {
				_tree = TreeNode.of(LT(1).value());
			} else {
				_tree.attach(LT(1).value());
			}

			consume();
			if (LA(1) != EOF.code()) {
				expression();
			}

		// var | fun
		} else if (LA(1) == ID.code()) {
			final var value = LT(1).value();
			consume();
			if (LA(1) != EOF.code()) {
				expression();
			}

			// var
			if (_variables.contains(value)) {
				_tree.attach(value);

			// fun
			} else if (_functions.contains(value)) {
				match(LPAREN);
				expression();
				while (LA(1) == COMMA.code()) {
					match(COMMA);
					expression();
				}
				match(RPAREN);
			}

		} else if (LA(1) == LPAREN.code()) {
			match(LPAREN);
			expression();
			match(RPAREN);
		} else {
			//expression();
			if (LA(1) == PLUS.code()) {
				match(PLUS);
				System.out.println("PLUS");
				final var node = TreeNode.of("+");
				_tree.detach();
				node.attach(_tree);
				_tree = node;
			} else if (LA(1) == MINUS.code()) {
				match(MINUS);
			} else if (LA(1) == TIMES.code()) {
				match(TIMES);
				System.out.println("TIMES");
				final var node = TreeNode.of("*");
				if (_tree.childCount() > 0) {
					final var child = _tree.childAt(_tree.childCount() - 1);
					child.detach();
					node.attach(child);
					_tree.attach(node);
				} else {
					_tree.detach();
					node.attach(_tree);
					_tree = node;
				}
			} else if (LA(1) == DIV.code()) {
				match (DIV);
			} else if (LA(1) == POW.code()) {
				match (POW);
			}
			expression();
		}
	}

	private void atom() {

	}

}
