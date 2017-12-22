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
package io.jenetics.prog.op;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.op.Tokenizer.Token;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Parser {

	private final Map<String, Integer> _variables = new HashMap<>();

	private final Deque<Token> _tokens;
	private Token _lookahead;

	private Parser(final Deque<Token> tokens) {
		_tokens = requireNonNull(tokens);
		_lookahead = _tokens.getFirst();
	}

	public static TreeNode<Op<Double>> parse(final String expr) {
		return new Parser(Tokenizer.MATH_OP.tokenize(expr)).parse();
	}

	private TreeNode<Op<Double>> parse() {
		final TreeNode<Op<Double>> expr = expression();
		if (_lookahead.token != Token.EPSILON) {
			throw new ParserException(format(
				"Unexpected symbol %s found.", _lookahead
			));
		}

		return expr;
	}

	private TreeNode<Op<Double>> expression() {
		return sumOp(signedTerm());
	}

	private TreeNode<Op<Double>> sumOp(final TreeNode<Op<Double>> expr) {
		TreeNode<Op<Double>> result = expr;

		if (_lookahead.token == Token.PLUS) {
			final TreeNode<Op<Double>> add = TreeNode
				.<Op<Double>>of(MathOp.ADD)
				.attach(expr);

			nextToken();
			add.attach(term());
			result = sumOp(add);
		} else if (_lookahead.token == Token.MINUS) {
			final TreeNode<Op<Double>> sub = TreeNode
				.<Op<Double>>of(MathOp.SUB)
				.attach(expr);

			nextToken();
			sub.attach(term());
			result = sumOp(sub);
		}

		return result;
	}

	private TreeNode<Op<Double>> signedTerm() {
		if (_lookahead.token == Token.MINUS) {
			nextToken();
			return TreeNode
				.<Op<Double>>of(MathOp.NEG)
				.attach(term());
		} else if (_lookahead.token == Token.PLUS) {
			nextToken();
		}

		return term();
	}

	private TreeNode<Op<Double>> term() {
		return termOp(factor());
	}

	private TreeNode<Op<Double>> termOp(final TreeNode<Op<Double>> expr) {
		TreeNode<Op<Double>> result = expr;

		if (_lookahead.token == Token.MUL) {
			final TreeNode<Op<Double>> prod = TreeNode
				.<Op<Double>>of(MathOp.MUL)
				.attach(expr);

			nextToken();
			prod.attach(signedFactor());
			result = termOp(prod);
		} else if (_lookahead.token == Token.DIV) {
			final TreeNode<Op<Double>> prod = TreeNode
				.<Op<Double>>of(MathOp.DIV)
				.attach(expr);

			nextToken();
			prod.attach(signedFactor());
			result = termOp(prod);
		}

		return result;
	}

	private TreeNode<Op<Double>> signedFactor() {
		if (_lookahead.token == Token.MINUS) {
			nextToken();
			return TreeNode
				.<Op<Double>>of(MathOp.NEG)
				.attach(factor());
		} else if (_lookahead.token == Token.PLUS) {
			nextToken();
		}

		return factor();
	}

	private TreeNode<Op<Double>> factor() {
		return factorOp(argument());
	}

	private TreeNode<Op<Double>> factorOp(final TreeNode<Op<Double>> expr) {
		TreeNode<Op<Double>> result = expr;

		if (_lookahead.token == Token.POWER) {
			nextToken();

			result = TreeNode.<Op<Double>>of(MathOp.POW)
				.attach(expr)
				.attach(signedFactor());
		}

		return result;
	}

	private TreeNode<Op<Double>> argument() {
		if (_lookahead.token == Token.FUNCTION) {
			final Op<Double> function = MathOp.ofName(_lookahead.sequence)
				.orElseThrow(() -> new ParserException(format(
					"Unexpected Function '%s' found", _lookahead.sequence)));

			nextToken();
			return TreeNode.of(function).attach(argument());
		} else if (_lookahead.token == Token.OPEN_BRACKET) {
			nextToken();
			final TreeNode<Op<Double>> expr = expression();
			if (_lookahead.token != Token.CLOSE_BRACKET) {
				throw new ParserException("Closing brackets expected", _lookahead);
			}

			nextToken();
			return expr;
		}

		return value();
	}

	private TreeNode<Op<Double>> value() {
		final String value = _lookahead.sequence;

		if (_lookahead.token == Token.NUMBER) {
			final TreeNode<Op<Double>> node =
				TreeNode.of(Const.of(Double.valueOf(value)));

			nextToken();
			return node;
		}

		if (_lookahead.token == Token.VARIABLE) {
			final TreeNode<Op<Double>> node = TreeNode.of(Var.of(value, 0));
			nextToken();
			return node;
		}

		if (_lookahead.token == Token.EPSILON) {
			throw new ParserException("Unexpected end of input.");
		} else {
			throw new ParserException(format(
				"Unexpected symbol %s found.", _lookahead
			));
		}
	}

	private void nextToken() {
		_tokens.pop();
		_lookahead = _tokens.isEmpty()
			? new Token(Token.EPSILON, "", -1)
			: _tokens.getFirst();
	}
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Tokenizer {

	static final class TokenInfo {
		final Pattern regex;
		final int token;
		TokenInfo(final Pattern regex, final int token) {
			this.regex = regex;
			this.token = token;
		}
	}

	static final class Token {
		static final int EPSILON = 0;
		static final int PLUS = 1;
		static final int MINUS = 2;
		static final int MUL = 3;
		static final int DIV = 4;
		static final int POWER = 5;
		static final int FUNCTION = 6;
		static final int OPEN_BRACKET = 7;
		static final int CLOSE_BRACKET = 8;
		static final int NUMBER = 9;
		static final int VARIABLE = 10;


		final int token;
		final String sequence;
		final int pos;

		Token(final int token, final String sequence, final int pos) {
			this.token = token;
			this.sequence = sequence;
			this.pos = pos;
		}

		@Override
		public String toString() {
			return format("[%s, %s, %s]", token, sequence, pos);
		}
	}

	static final Tokenizer MATH_OP = new Tokenizer();
	static {
		MATH_OP.add("\\+", Token.PLUS);
		MATH_OP.add("-", Token.MINUS);
		MATH_OP.add("\\*", Token.MUL);
		MATH_OP.add("/", Token.DIV);
		MATH_OP.add("\\^", Token.POWER);

		String funcs = "sin|cos|tan|asin|acos|atan|sqrt|exp|ln|log|log2|abs";
		MATH_OP.add("(" + funcs + ")(?!\\w)", Token.FUNCTION);

		MATH_OP.add("\\(", Token.OPEN_BRACKET);
		MATH_OP.add("\\)", Token.CLOSE_BRACKET);
		MATH_OP.add("(?:\\d+\\.?|\\.\\d)\\d*(?:[Ee][-+]?\\d+)?", Token.NUMBER);
		MATH_OP.add("[a-zA-Z]\\w*", Token.VARIABLE);
	}

	private final Deque<TokenInfo> _infos = new LinkedList<>();

	private Tokenizer() {
	}

	private void add(String regex, int token) {
		_infos.add(new TokenInfo(Pattern.compile("^(" + regex+")"), token));
	}

	public Deque<Token> tokenize(final String expression) {
		final Deque<Token> tokens = new LinkedList<>();

		String string = expression.trim();
		final int totalLength = string.length();
		while (!string.isEmpty()) {
			int remaining = string.length();
			boolean match = false;
			for (TokenInfo info : _infos) {
				Matcher m = info.regex.matcher(string);
				if (m.find()) {
					final String tok = m.group().trim();
					string = m.replaceFirst("").trim();
					tokens.add(new Token(info.token, tok, totalLength - remaining));

					match = true;
					break;
				}
			}

			if (!match) {
				throw new IllegalArgumentException(
					"Unexpected character in input: " + string
				);
			}
		}

		return tokens;
	}

}
