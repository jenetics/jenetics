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

import java.util.Optional;
import java.util.stream.Stream;

import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
enum Simplifier {

	X_DIV_X {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.DIV &&
				node.getChild(0).equals(node.getChild(1));
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			node.removeAllChildren();
			node.setValue(Const.of(1.0));
		}
	},

	X_SUB_X {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.SUB &&
				node.getChild(0).equals(node.getChild(1));
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			node.removeAllChildren();
			node.setValue(Const.of(0.0));
		}
	},

	CONST_EXPR {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return
				node.getValue() instanceof MathOp &&
					node.childStream()
						.allMatch(child -> child.getValue() instanceof Const<?>);
		}

		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			final Double[] args = node.childStream()
				.map(child -> ((Const<Double>)child.getValue()).value())
				.toArray(Double[]::new);

			final Double value = node.getValue().apply(args);
			node.removeAllChildren();
			node.setValue(Const.of(value));
		}
	};

	static TreeNode<Op<Double>>
	prune(final TreeNode<Op<Double>> node) {
		final Optional<Simplifier> simplifier= Stream.of(values())
			.filter(s -> s.matches(node))
			.findFirst();

		simplifier.ifPresent(s -> s.simplify(node));

		//if (simplifier.isPresent()) {
		//	simplifier.get().simplify(node);
		//} else {
		for (int i = 0, n = node.childCount(); i < n; ++i) {
			prune(node.getChild(i));
		}
		//}

		return node;
	}

	abstract boolean matches(final TreeNode<Op<Double>> node);

	abstract public void simplify(final TreeNode<Op<Double>> node);

}
