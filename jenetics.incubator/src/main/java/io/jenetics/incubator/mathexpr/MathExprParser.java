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

import io.jenetics.incubator.parser.Parser;
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
 * @param <T> the token value type used as input for the parser
 * @param <V> the type of the parsed AST
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public class MathExprParser<T, V> extends Parser<T>  {

	private final MathExprParsing<T, V> _parsing;

	/**
	 * Creates a new parser of mathematical expressions.
	 *
	 * @param tokenizer the tokenizer used by the parser
	 * @param parsing the parsing <em>configuration</em>
	 */
	public MathExprParser(
		final Tokenizer<T> tokenizer,
		final MathExprParsing<T, V> parsing
	) {
		super(tokenizer, 1);
		_parsing = requireNonNull(parsing);
	}

	/**
	 * Return the parsed expression.
	 *
	 * @return the parsed expression
	 */
	public TreeNode<V> parse() {
		return _parsing.parse(this);
	}

	/**
	 * Create a new parser for a mathematical expression string.
	 *
	 * @param expr the expression string
	 * @param parsing the parsing <em>configuration</em>
	 * @param <V> the type of the parsed AST
	 * @return a new parser for a mathematical expression string
	 */
	public static <V> MathExprParser<String, V>
	of(final String expr, final MathExprParsing<String, V> parsing) {
		return new MathExprParser<>(new MathStringTokenizer(expr), parsing);
	}

}
