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
import static io.jenetics.internal.util.Hashes.hash;

import java.util.Objects;

/**
 * A compiled representation of a <em>tree</em> pattern. A tree pattern,
 * specified as a string, must first be compiled into an instance of this class.
 * The resulting pattern can then be used to create a {@code TreeMatcher} object
 * that can match arbitrary trees against the tree pattern. All of the state
 * involved in performing a match resides in the matcher, so many matchers can
 * share the same pattern.
 * <p>
 * The string representation of a tree pattern is a parenthesis tree string,
 * with a special wildcard syntax for arbitrary sub-trees:
 * <pre>{@code
 *     add(<x>,0)
 *     mul(1,<y>)
 * }</pre>
 * The identifier of such sub-trees are put into angle brackets.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class TreePattern {







	private final Tree<Node, ?> _tree;


	private TreePattern(final Tree<Node, ?> tree) {
		_tree = requireNonNull(tree);
	}

	Tree<Node, ?> tree() {
		return _tree;
	}

	static TreePattern compile(final String expr) {
		final TreeNode<Node> tree = TreeNode.parse(expr, TreePattern::toNode);
		return new TreePattern(tree);
	}

	private static Node toNode(final String value) {
		return value.startsWith("<") && value.endsWith(">")
			? Node.var(value.substring(1, value.length() - 1))
			: Node.val(value);
	}



	/* *************************************************************************
	 * Helper classes
	 ***************************************************************************/

	static abstract class Node {
		private final String _value;

		Node(final String value) {
			_value = value;
		}

		String value() {
			return _value;
		}

		@Override
		public int hashCode() {
			return hash(getClass(), hash(_value));
		}

		@Override
		public boolean equals(final Object obj) {
			return obj != null &&
				getClass() == obj.getClass() &&
				Objects.equals(_value, ((Node)obj)._value);
		}

		@Override
		public String toString() {
			return Objects.toString(_value);
		}

		static Val val(final String value) {
			return new Val(value);
		}

		static Var var(final String name) {
			return new Var(name);
		}
	}

	static final class Val extends Node {
		private Val(final String value) {
			super(value);
		}
	}

	static final class Var extends Node {
		private Var(final String value) {
			super(value);
		}

		@Override
		public String toString() {
			return format("<%s>", value());
		}
	}

}
