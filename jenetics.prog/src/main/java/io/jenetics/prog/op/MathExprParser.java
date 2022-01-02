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

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.ext.util.TreeNode;

/**
 * Simple parser for mathematical expressions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
final class MathExprParser {

	private static final Map<String, Const<Double>> CONST = Map.of(
		"PI", MathOp.PI,
		"π", MathOp.PI
	);

	/**
	 * Contains the token regex and the token kind;
	 */
	record TokenDesc(Pattern regex, int type) {}

	/**
	 * Represents expression token.
	 */
	record Token(int token, String sequence, int pos) {
		static final int EPSILON = 0;
		static final int PLUS = 1;
		static final int MINUS = 2;
		static final int MUL = 3;
		static final int DIV = 4;
		static final int MOD = 5;
		static final int POWER = 6;
		static final int FUNCTION = 7;
		static final int OPEN_BRACKET = 8;
		static final int CLOSE_BRACKET = 9;
		static final int NUMBER = 10;
		static final int VARIABLE = 11;
		static final int COMMA = 12;

		@Override
		public String toString() {
			return format("['%s', %s]", sequence, pos);
		}
	}

	/**
	 * The math expression tokenizer.
	 */
	static final class Tokenizer {
		static final Tokenizer MATH_OP = new Tokenizer();
		static {
			MATH_OP.add("\\+", Token.PLUS);
			MATH_OP.add("-", Token.MINUS);
			MATH_OP.add("\\*", Token.MUL);
			MATH_OP.add("/", Token.DIV);
			MATH_OP.add("%", Token.MOD);
			MATH_OP.add("\\^", Token.POWER);
			MATH_OP.add("\\,", Token.COMMA);

			final String functions = Stream.of(MathOp.values())
				.map(MathOp::toString)
				.collect(Collectors.joining("|"));
			MATH_OP.add("(" + functions + ")(?!\\w)", Token.FUNCTION);

			MATH_OP.add("\\(", Token.OPEN_BRACKET);
			MATH_OP.add("\\)", Token.CLOSE_BRACKET);
			MATH_OP.add("(?:\\d+\\.?|\\.\\d)\\d*(?:[Ee][-+]?\\d+)?", Token.NUMBER);
			MATH_OP.add("[a-zA-Zπ]\\w*", Token.VARIABLE);
		}

		private final Deque<TokenDesc> _infos = new LinkedList<>();

		private Tokenizer() {
		}

		private void add(String regex, int token) {
			_infos.add(new TokenDesc(Pattern.compile("^(" + regex+")"), token));
		}

		Deque<Token> tokenize(final String expression) {
			final Deque<Token> tokens = new LinkedList<>();

			String string = expression.trim();
			final int totalLength = string.length();
			while (!string.isEmpty()) {
				final int remaining = string.length();
				boolean match = false;
				for (TokenDesc info : _infos) {
					final Matcher m = info.regex.matcher(string);
					if (m.find()) {
						final String tok = m.group().trim();
						string = m.replaceFirst("").trim();
						tokens.add(new Token(info.type, tok, totalLength - remaining));

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

	private static final Op<Double> LIST_OP = Const.of(Double.NaN);

	private final Deque<Token> _tokens;
	private Token _next;

	private MathExprParser(final Deque<Token> tokens) {
		_tokens = requireNonNull(tokens);
		_next = _tokens.getFirst();
	}

	static TreeNode<Op<Double>> parse(final String expr) {
		if (expr.trim().isEmpty()) {
			throw new IllegalArgumentException(
				"Expression string is empty: " + expr
			);
		}
		return new MathExprParser(Tokenizer.MATH_OP.tokenize(expr)).parse();
	}

	private TreeNode<Op<Double>> parse() {
		final TreeNode<Op<Double>> expr = expression();
		if (_next.token != Token.EPSILON) {
			throw new IllegalArgumentException(format(
				"Unexpected symbol %s found.", _next
			));
		}

		Var.reindex(expr);
		return expr;
	}

	private TreeNode<Op<Double>> expression() {
		return term_10_op_sum(signed_term_10_sum());
	}

	private TreeNode<Op<Double>> term_10_op_sum(final TreeNode<Op<Double>> expr) {
		TreeNode<Op<Double>> result = expr;

		if (_next.token == Token.PLUS) {
			final TreeNode<Op<Double>> add = TreeNode
				.<Op<Double>>of(MathOp.ADD)
				.attach(expr);

			nextToken();
			add.attach(term_10_sum());
			result = term_10_op_sum(add);
		} else if (_next.token == Token.MINUS) {
			final TreeNode<Op<Double>> sub = TreeNode
				.<Op<Double>>of(MathOp.SUB)
				.attach(expr);

			nextToken();
			sub.attach(term_10_sum());
			result = term_10_op_sum(sub);
		}

		return result;
	}

	private TreeNode<Op<Double>> signed_term_10_sum() {
		if (_next.token == Token.MINUS) {
			nextToken();
			return TreeNode
				.<Op<Double>>of(MathOp.NEG)
				.attach(term_10_sum());
		} else if (_next.token == Token.PLUS) {
			nextToken();
		}

		return term_10_sum();
	}

	private TreeNode<Op<Double>> term_10_sum() {
		return term_11_op_mult(term_11_mult());
	}

	private TreeNode<Op<Double>> term_11_op_mult(final TreeNode<Op<Double>> expr) {
		TreeNode<Op<Double>> result = expr;

		if (_next.token == Token.MUL) {
			final TreeNode<Op<Double>> prod = TreeNode
				.<Op<Double>>of(MathOp.MUL)
				.attach(expr);

			nextToken();
			prod.attach(signed_term_11_mult());
			result = term_11_op_mult(prod);
		} else if (_next.token == Token.DIV) {
			final TreeNode<Op<Double>> prod = TreeNode
				.<Op<Double>>of(MathOp.DIV)
				.attach(expr);

			nextToken();
			prod.attach(signed_term_11_mult());
			result = term_11_op_mult(prod);
		} else if (_next.token == Token.MOD) {
			final TreeNode<Op<Double>> prod = TreeNode
				.<Op<Double>>of(MathOp.MOD)
				.attach(expr);

			nextToken();
			prod.attach(signed_term_11_mult());
			result = term_11_op_mult(prod);
		}

		return result;
	}

	private TreeNode<Op<Double>> signed_term_11_mult() {
		if (_next.token == Token.MINUS) {
			nextToken();
			return TreeNode
				.<Op<Double>>of(MathOp.NEG)
				.attach(term_11_mult());
		} else if (_next.token == Token.PLUS) {
			nextToken();
		}

		return term_11_mult();
	}

	private TreeNode<Op<Double>> term_11_mult() {
		return factorOp(argument());
	}

	private TreeNode<Op<Double>> factorOp(final TreeNode<Op<Double>> expr) {
		TreeNode<Op<Double>> result = expr;

		if (_next.token == Token.POWER) {
			nextToken();

			result = TreeNode.<Op<Double>>of(MathOp.POW)
				.attach(expr)
				.attach(signed_term_11_mult());
		}

		return result;
	}

	private TreeNode<Op<Double>> argument() {
		if (_next.token == Token.FUNCTION) {
			final Op<Double> function = ofName(_next.sequence)
				.orElseThrow(() -> new IllegalArgumentException(format(
					"Unknown function '%s' found", _next.sequence)));

			nextToken();
			final TreeNode<Op<Double>> node = TreeNode.of(function);
			list(argument(), new ArrayList<>()).forEach(node::attach);
			return node;
		} else if (_next.token == Token.COMMA ||
			_next.token == Token.OPEN_BRACKET)
		{
			nextToken();
			TreeNode<Op<Double>> expr = expression();
			if (_next.token == Token.COMMA) {
				expr = TreeNode
					.of(LIST_OP)
					.attach(expr)
					.attach(argument());

				return expr;
			}

			if (_next.token != Token.CLOSE_BRACKET) {
				throw new IllegalArgumentException(format(
					"Closing brackets expected: %s", _next));
			}

			nextToken();
			return expr;
		}

		return value();
	}

	private static Optional<MathOp> ofName(final String name) {
		return Stream.of(MathOp.values())
			.filter(op -> op.name().equalsIgnoreCase(name))
			.findFirst();
	}

	private static List<TreeNode<Op<Double>>> list(
		final TreeNode<Op<Double>> tree,
		final List<TreeNode<Op<Double>>> list
	) {
		if (tree.value() == LIST_OP) {
			tree.childStream().forEach(child -> list(child, list));
		} else {
			list.add(tree);
		}
		return list;
	}

	private TreeNode<Op<Double>> value() {
		final String value = _next.sequence;

		if (_next.token == Token.NUMBER) {
			final TreeNode<Op<Double>> node =
				TreeNode.of(Const.of(Double.valueOf(value)));

			nextToken();
			return node;
		}

		if (_next.token == Token.VARIABLE) {
			final TreeNode<Op<Double>> node = CONST.containsKey(value)
				? TreeNode.of(CONST.get(value))
				: TreeNode.of(Var.of(value, 0));

			nextToken();
			return node;
		}

		if (_next.token == Token.EPSILON) {
			throw new IllegalArgumentException("Unexpected end of input.");
		} else {
			throw new IllegalArgumentException(format(
				"Unexpected symbol %s found.", _next
			));
		}
	}

	private void nextToken() {
		_tokens.pop();
		_next = _tokens.isEmpty()
			? new Token(Token.EPSILON, "", -1)
			: _tokens.getFirst();
	}
}
