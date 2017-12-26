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

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
enum Simplifier {

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

	/*
	GON {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return node.getValue() == MathOp.ADD &&
				node.getChild(0).getValue() == MathOp.POW &&
				node.getChild(1).getValue() == MathOp.POW &&
				node.getChild(0).getChild(0).getValue() == MathOp.SIN &&
				node.getChild(0).getChild(1).getValue() instanceof Const &&

				node.getChild(0).equals(node.getChild(1));
		}
		@Override
		public void simplify(final TreeNode<Op<Double>> node) {
			node.removeAllChildren();
			node.setValue(Const.of(1.0));
		}
	},
	*/

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

	private static final Template GON1 = Template.of("sin(x)^2 + cos(x)^2", "1");

	private static final Template MUL_0 = Template.of("0.0*x", "0.0");

	static TreeNode<Op<Double>> prune(final TreeNode<Op<Double>> node) {
		while (_prune(node));
		return node;
	}

	private static boolean
	_prune(final TreeNode<Op<Double>> node) {
		final Optional<Simplifier> simplifier= Stream.of(values())
			.filter(s -> s.matches(node))
			.findFirst();

		simplifier.ifPresent(s -> s.simplify(node));
		return simplifier.isPresent() | node.childStream()
			.mapToInt(child -> _prune(child) ? 1 : 0)
			.sum() > 0;
	}


	/*
	static TreeNode<Op<Double>> extract(
		final TreeNode<Op<Double>> a,
		final TreeNode<Op<Double>> b
	) {
		if (a.getValue() instanceof Var && a.getValue().name().equals("_")) {
			return b;
		} else {
			if (a.childCount() == b.childCount()) {
				if (!Objects.equals(a.getValue(), b.getValue())) {
					return null;
				}

				if (a.childCount() > 0) {
					equals = equals(a.childIterator(), b.childIterator());
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		return null;
	}

	private static TreeNode<Op<Double>> extract(
		final Iterator<TreeNode<Op<Double>>> a,
		final Iterator<TreeNode<Op<Double>>> b
	) {
		final
		while (a.hasNext()) {
			final TreeNode<Op<Double>> aa = a.next();

			if (aa.getValue() instanceof Var && aa.getValue().name().equals("_")) {
				return b.next();
			}
		}

		return null;
	}
	*/

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

	private static final class Template {
		private final TreeNode<Op<Double>> _template;
		private final TreeNode<Op<Double>> _replacement;

		Template(
			final TreeNode<Op<Double>> template,
			final TreeNode<Op<Double>> replacement
		) {
			_template = requireNonNull(template);
			_replacement = requireNonNull(replacement);
		}


		static Template of(final String template, final String replacement) {
			return new Template(Parser.parse(template), Parser.parse(replacement));
		}

	}

}
