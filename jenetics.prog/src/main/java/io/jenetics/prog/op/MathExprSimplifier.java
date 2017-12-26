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

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
enum MathExprSimplifier {

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

	X_ADD_X {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.ADD &&
				node.getChild(0).equals(node.getChild(1));
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			final TreeNode<Op<Double>> sub = node.getChild(0);

			node.removeAllChildren();
			node.setValue(MathOp.MUL);
			node.attach(Const.of(2.0));
			node.attach(sub);
		}
	},

	SUB_ZERO {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.SUB &&
				equals(node, 1, 0.0);
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			final TreeNode<Op<Double>> sub = node.getChild(0);

			node.removeAllChildren();
			node.setValue(sub.getValue());
			sub.childStream()
				.collect(ISeq.toISeq())
				.forEach(node::attach);
		}
	},

	ADD_ZERO {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.ADD &&
				(equals(node, 0, 0.0) ||
				equals(node, 1, 0.0));
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			final TreeNode<Op<Double>> sub = equals(node, 0, 0.0)
				? node.getChild(1)
				: node.getChild(0);

			node.removeAllChildren();
			node.setValue(sub.getValue());
			sub.childStream()
				.collect(ISeq.toISeq())
				.forEach(node::attach);
		}
	},

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

	MUL_ZERO {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.MUL &&
				node.childCount() == 2 &&
				(equals(node, 0, 0.0) ||
				equals(node, 1, 0.0));
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			node.removeAllChildren();
			node.setValue(Const.of(0.0));
		}
	},

	MUL_ONE {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.MUL &&
				(equals(node, 0, 1.0) ||
				equals(node, 1, 1.0));
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			final TreeNode<Op<Double>> sub = equals(node, 0, 1.0)
				? node.getChild(1)
				: node.getChild(0);

			node.removeAllChildren();
			node.setValue(sub.getValue());
			sub.childStream()
				.collect(ISeq.toISeq())
				.forEach(node::attach);
		}
	},

	X_MUL_X {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.MUL &&
				node.getChild(0).equals(node.getChild(1));
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			final TreeNode<Op<Double>> sub = node.getChild(0);

			node.removeAllChildren();
			node.setValue(MathOp.POW);
			node.attach(sub);
			node.attach(Const.of(2.0));
		}
	},

	POW_ZERO {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.POW &&
				node.childCount() == 2 &&
				equals(node, 1, 0.0);
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			node.removeAllChildren();
			node.setValue(Const.of(1.0));
		}
	},

	POW_ONE {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.POW &&
				node.childCount() == 2 &&
				equals(node, 1, 1.0);
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			final TreeNode<Op<Double>> sub = node.getChild(0);

			node.removeAllChildren();
			node.setValue(sub.getValue());
			sub.childStream()
				.collect(ISeq.toISeq())
				.forEach(node::attach);
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

	static TreeNode<Op<Double>> prune(final TreeNode<Op<Double>> node) {
		while (_prune(node));
		return node;
	}

	private static boolean
	_prune(final TreeNode<Op<Double>> node) {
		final Optional<MathExprSimplifier> simplifier= Stream.of(values())
			.filter(s -> s.matches(node))
			.findFirst();

		simplifier.ifPresent(s -> s.simplify(node));
		return simplifier.isPresent() | node.childStream()
			.mapToInt(child -> _prune(child) ? 1 : 0)
			.sum() > 0;
	}

	abstract boolean matches(final TreeNode<Op<Double>> node);

	abstract public void simplify(final TreeNode<Op<Double>> node);

	static boolean equals(
		final Tree<? extends Op<Double>, ?> node,
		final int index,
		final double value
	) {
		return node.getChild(index).getValue() instanceof Const<?> &&
			((Const)node.getChild(index).getValue()).value().equals(value);
	}

}
