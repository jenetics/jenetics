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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * Helper methods for creating parentheses tree strings.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.3
 * @since 4.3
 */
final class ParenthesesTrees {
	private ParenthesesTrees() {}

	static final char ESCAPE_CHAR = '\\';

	private static final char[] PROTECTED_CHARS = { '(', ')', ',' };

	static String escape(final String value) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (isProtectedChar(c)) {
				result.append(ESCAPE_CHAR);
			}
			result.append(c);
		}

		return result.toString();
	}

	private static boolean isProtectedChar(final char c) {
		for (int i = 0; i < PROTECTED_CHARS.length; ++i) {
			if (c == PROTECTED_CHARS[i]) {
				return true;
			}
		}

		return false;
	}


	static String unescape(final String value) {
		final StringBuilder result = new StringBuilder();

		boolean escaping = false;
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (c == ESCAPE_CHAR &&
				!escaping &&
				i + 1 < value.length() &&
				isProtectedChar(value.charAt(i + 1)))
			{
				escaping = true;
				continue;
			}

			if (escaping) {
				escaping = false;
			}
			result.append(c);
		}

		return result.toString();
	}

	/* *************************************************************************
	 * To string methods.
	 **************************************************************************/

	/**
	 * Return a compact string representation of the given tree.
	 * <pre>
	 *  mul(div(cos(1.0), cos(π)), sin(mul(1.0, z)))
	 * </pre>
	 *
	 * @param tree the input tree
	 * @return the string representation of the given tree
	 */
	static <V, T extends Tree<V, T>> String toString(
		final T tree,
		final Function<? super V, String> mapper
	) {
		requireNonNull(mapper);

		if (tree != null) {
			final StringBuilder out = new StringBuilder();
			toString(out, tree, mapper);
			return out.toString();
		} else {
			return "null";
		}
	}

	private static  <V, T extends Tree<V, T>> void toString(
		final StringBuilder out,
		final T tree,
		final Function<? super V, String> mapper
	) {
		out.append(escape(mapper.apply(tree.getValue())));
		if (!tree.isLeaf()) {
			out.append("(");
			toString(out, tree.getChild(0), mapper);
			for (int i = 1; i < tree.childCount(); ++i) {
				out.append(",");
				toString(out, tree.getChild(i), mapper);
			}
			out.append(")");
		}
	}

}



