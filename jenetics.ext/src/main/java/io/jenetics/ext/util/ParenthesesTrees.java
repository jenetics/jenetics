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

import io.jenetics.ext.internal.Escaper;

/**
 * Helper methods for creating parentheses tree strings.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.3
 * @since 4.3
 */
final class ParenthesesTrees {
	private ParenthesesTrees() {}

	private static final char[] PROTECTED_CHARS = { '(', ')', ',' };
	static final char ESCAPE_CHAR = '\\';

	private static final Escaper ESCAPER = new Escaper(ESCAPE_CHAR, PROTECTED_CHARS);

	static String escape(final CharSequence value) {
		return ESCAPER.escape(value);
	}

	static String unescape(final CharSequence value) {
		return ESCAPER.unescape(value);
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
	 * @param mapper the string mapper function
	 * @return the string representation of the given tree
	 */
	static <V> String toString(
		final Tree<V, ?> tree,
		final Function<? super V, ? extends CharSequence> mapper
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

	private static <V> void toString(
		final StringBuilder out,
		final Tree<V, ?> tree,
		final Function<? super V, ? extends CharSequence> mapper
	) {
		out.append(escape(mapper.apply(tree.value())));
		if (!tree.isLeaf()) {
			out.append("(");
			toString(out, tree.childAt(0), mapper);
			for (int i = 1; i < tree.childCount(); ++i) {
				out.append(",");
				toString(out, tree.childAt(i), mapper);
			}
			out.append(")");
		}
	}

}



