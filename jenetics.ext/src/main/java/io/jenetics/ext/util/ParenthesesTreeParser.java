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
package io.jenetics.ext.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.ext.util.ParenthesesTrees.ESCAPE_CHAR;
import static io.jenetics.ext.util.ParenthesesTrees.unescape;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

/**
 * Parses an parentheses string into a {@code TreeNode<String>} object.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 4.3
 */
final class ParenthesesTreeParser {
	private ParenthesesTreeParser() {}

	/**
	 * Represents a parentheses tree string token.
	 */
	record Token(String seq, int pos) {}

	/**
	 * Tokenize the given parentheses string.
	 *
	 * @param value the parentheses string
	 * @return the parentheses string tokens
	 * @throws NullPointerException if the given {@code value} is {@code null}
	 */
	static List<Token> tokenize(final String value) {
		final List<Token> tokens = new ArrayList<>();

		char pc = '\0';
		int pos = 0;
		final StringBuilder token = new StringBuilder();
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (isTokenSeparator(c) && pc != ESCAPE_CHAR) {
				tokens.add(new Token(token.toString(), pos));
				tokens.add(new Token(Character.toString(c), i));
				token.setLength(0);
				pos = i;
			} else {
				token.append(c);
			}

			pc = c;
		}

		if (!token.isEmpty()) {
			tokens.add(new Token(token.toString(), pos));
		}

		return tokens;
	}

	private static boolean isTokenSeparator(final char c) {
		return c == '(' || c == ')' || c == ',';
	}

	/**
	 * Parses the given parentheses tree string
	 *
	 * @since 4.3
	 *
	 * @param <B> the tree node value type
	 * @param value the parentheses tree string
	 * @param mapper the mapper which converts the serialized string value to
	 *        the desired type
	 * @return the parsed tree object
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given parentheses tree string
	 *         doesn't represent a valid tree
	 */
	static <B> TreeNode<B> parse(
		final String value,
		final Function<? super String, ? extends B> mapper
	) {
		requireNonNull(value);
		requireNonNull(mapper);

		final TreeNode<B> root = TreeNode.of();
		final Deque<TreeNode<B>> parents = new ArrayDeque<>();

		TreeNode<B> current = root;
		for (Token token : tokenize(value.trim())) {
			switch (token.seq) {
				case "(" -> {
					if (current == null) {
						throw new IllegalArgumentException(format(
							"Illegal parentheses tree string: '%s'.",
							value
						));
					}
					final TreeNode<B> tn1 = TreeNode.of();
					current.attach(tn1);
					parents.push(current);
					current = tn1;
				}
				case "," -> {
					if (parents.isEmpty()) {
						throw new IllegalArgumentException(format(
							"Expect '(' at position %d.",
							token.pos
						));
					}
					final TreeNode<B> tn2 = TreeNode.of();
					assert parents.peek() != null;
					parents.peek().attach(tn2);
					current = tn2;
				}
				case ")" -> {
					if (parents.isEmpty()) {
						throw new IllegalArgumentException(format(
							"Unbalanced parentheses at position %d.",
							token.pos
						));
					}
					current = parents.pop();
					if (parents.isEmpty()) {
						current = null;
					}
				}
				default -> {
					if (current == null) {
						throw new IllegalArgumentException(format(
							"More than one root element at pos %d: '%s'.",
							token.pos, value
						));
					}
					if (current.value() == null) {
						current.value(mapper.apply(unescape(token.seq)));
					}
				}
			}
		}

		if (!parents.isEmpty()) {
			throw new IllegalArgumentException(
				"Unbalanced parentheses: " + value
			);
		}

		return root;
	}

}
