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
import static io.jenetics.ext.util.Escaping.escape;

import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class ParenthesesTrees {
	private ParenthesesTrees() {}

	/**
	 * Return a compact string representation of the given tree. The tree
	 * <pre>
	 *  mul
	 *  ├── div
	 *  │   ├── cos
	 *  │   │   └── 1.0
	 *  │   └── cos
	 *  │       └── π
	 *  └── sin
	 *      └── mul
	 *          ├── 1.0
	 *          └── z
	 *  </pre>
	 * is printed as
	 * <pre>
	 *  mul(div(cos(1.0), cos(π)), sin(mul(1.0, z)))
	 * </pre>
	 *
	 * @param tree the input tree
	 * @return the string representation of the given tree
	 */
	static <V, T extends Tree<V, T>> String toParenthesesString(
		final T tree,
		final Function<? super V, String> mapper
	) {
		requireNonNull(mapper);

		if (tree != null) {
			final StringBuilder out = new StringBuilder();
			toParenthesesString(out, tree, mapper);
			return out.toString();
		} else {
			return "null";
		}
	}

	private static  <V, T extends Tree<V, T>> void toParenthesesString(
		final StringBuilder out,
		final T tree,
		final Function<? super V, String> mapper
	) {
		out.append(escape(mapper.apply(tree.getValue())));
		if (!tree.isLeaf()) {
			out.append("(");
			toParenthesesString(out, tree.getChild(0), mapper);
			for (int i = 1; i < tree.childCount(); ++i) {
				out.append(",");
				toParenthesesString(out, tree.getChild(i), mapper);
			}
			out.append(")");
		}
	}

}
