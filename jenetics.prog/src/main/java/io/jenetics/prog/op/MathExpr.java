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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * This class allows you to create a math operation tree from an expression
 * string. The expression string may only contain functions/operations defined
 * in {@link MathOp}.
 *
 * <pre>{@code
 * final MathExpr expr = MathExpr.parse("5 + 6*x + sin(x)^34 + (1 + sin(x*5)/4)/6");
 * final double result = expr.eval(4.32);
 * assert result == 31.170600453465315;
 *
 * assert 12.0 == MathExpr.eval("3*4");
 * assert 24.0 == MathExpr.eval("3*4*x", 2);
 * assert 28.0 == MathExpr.eval("3*4*x + y", 2, 4);
 * }</pre>
 *
 * @see MathOp
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public final class MathExpr
	implements
		Function<Double[], Double>,
		Serializable
{

	private static final long serialVersionUID = 1L;

	private static final Map<MathOp, String> INFIX_OPS = new EnumMap<>(MathOp.class);
	static {
		INFIX_OPS.put(MathOp.ADD, " + ");
		INFIX_OPS.put(MathOp.SUB, " - ");
		INFIX_OPS.put(MathOp.MUL, "*");
		INFIX_OPS.put(MathOp.DIV, "/");
		INFIX_OPS.put(MathOp.MOD, "%");
		INFIX_OPS.put(MathOp.POW, "^");
	}

	private final Tree<? extends Op<Double>, ?> _tree;

	// Primary constructor.
	private MathExpr(final Tree<? extends Op<Double>, ?> tree, boolean primary) {
		_tree = requireNonNull(tree);
	}

	/**
	 * Create a new {@code MathExpr} object from the given operation tree.
	 *
	 * @param tree the underlying operation tree
	 * @throws NullPointerException if the given {@code program} is {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public MathExpr(final Tree<? extends Op<Double>, ?> tree) {
		this(TreeNode.ofTree(tree), true);
		Program.check(tree);
	}

	/**
	 * Return the variable list of this <em>math</em> expression.
	 *
	 * @return the variable list of this <em>math</em> expression
	 */
	public ISeq<Var<Double>> vars() {
		return ISeq.of(
			_tree.stream()
				.filter(node -> node.getValue() instanceof Var<?>)
				.map(node -> (Var<Double>)node.getValue())
				.collect(Collectors.toCollection(() ->
					new TreeSet<>(Comparator.comparing(Var::name))))
		);
	}

	/**
	 * Return the math expression as operation tree.
	 *
	 * @return a new expression tree
	 */
	public Tree<? extends Op<Double>, ?> toTree() {
		return TreeNode.ofTree(_tree);
	}

	/**
	 * @see #eval(double...)
	 * @see #eval(String, double...)
	 */
	@Override
	public Double apply(final Double[] args) {
		return Program.eval(_tree, args);
	}

	/**
	 * Convenient method, which lets you apply the program function without
	 * explicitly create a wrapper array.
	 *
	 * <pre>{@code
	 *  final double result = MathExpr.parse("2*z + 3*x - y").eval(3, 2, 1);
	 *  assert result == 9.0;
	 * }</pre>
	 *
	 * @see #apply(Double[])
	 * @see #eval(String, double...)
	 *
	 * @param args the function arguments
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 * @throws IllegalArgumentException if the length of the arguments array
	 *         is smaller than the program arity
	 */
	public double eval(final double... args) {
		return apply(DoubleStream.of(args).boxed().toArray(Double[]::new));
	}

	@Override
	public int hashCode() {
		return _tree.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof MathExpr &&
			Objects.equals(((MathExpr) obj)._tree, _tree);
	}

	/**
	 * Return the string representation of this {@code MathExpr} object. The
	 * string returned by this method can be parsed again and will result in the
	 * same expression object.
	 * <pre>{@code
	 *  final String expr = "5.0 + 6.0*x + sin(x)^34.0 + (1.0 + sin(x*5.0)/4.0) + 6.5";
	 *  final MathExpr tree = MathExpr.parse(expr);
	 *  assert tree.toString().equals(expr);
	 * }</pre>
	 *
	 * @return the expression string
	 */
	@Override
	public String toString() {
		return toString(_tree);
	}

	private static String toString(
		final Tree<? extends Op<Double>, ?> tree,
		final StringBuilder out
	) {
		final Op<Double> op = tree.getValue();
		if (INFIX_OPS.containsKey(op)) {
			infix(INFIX_OPS.get(op), tree, out);
		} else {
			out.append(op);
			if (!tree.isLeaf()) {
				final boolean brackets = true;

				if (brackets) out.append("(");
				toString(tree.getChild(0), out);
				for (int i = 1; i < tree.childCount(); ++i) {
					out.append(", ");
					toString(tree.getChild(i), out);
				}
				if (brackets) out.append(")");
			}
		}

		return out.toString();
	}

	private static void infix(
		final String op,
		final Tree<? extends Op<Double>, ?> tree,
		final StringBuilder out
	) {
		final boolean brackets = true;

		if (brackets) out.append("(");
		toString(tree.getChild(0), out);
		out.append(op);
		toString(tree.getChild(1), out);
		if (brackets) out.append(")");
	}

	/**
	 * Tries to simplify {@code this} math expression.
	 *
	 * <pre>{@code
	 * final MathExpr expr = MathExpr.parse("4.0 + 4.0 + x*(5.0 + 13.0)");
	 * final MathExpr simplified = expr.simplify()
	 * System.out.println(simplified);
	 * }</pre>
	 * The simplified expression will be look like this: {@code 8.0 + (x*18.0)}.
	 *
	 * @see #prune(TreeNode)
	 * @see #simplify(Tree)
	 *
	 * @return a new simplified math expression
	 */
	public MathExpr simplify() {
		return new MathExpr(simplify(_tree));
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.MATH_EXPR, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		final byte[] data = toString().getBytes("UTF-8");
		out.writeInt(data.length);
		out.write(data);
	}

	static MathExpr read(final DataInput in) throws IOException {
		final byte[] data = new byte[in.readInt()];
		in.readFully(data);
		return parse(new String(data, "UTF-8"));
	}


	/* *************************************************************************
	 * Static helper methods.
	 * ************************************************************************/

	/**
	 * Return the string representation of the given {@code tree} object. The
	 * string returned by this method can be parsed again and will result in the
	 * same expression object.
	 * <pre>{@code
	 *  final String expr = "5.0 + 6.0*x + sin(x)^34.0 + (1.0 + sin(x*5.0)/4.0) + 6.5";
	 *  final MathExpr tree = MathExpr.parse(expr);
	 *  assert MathExpr.toString(tree.tree()).equals(expr);
	 * }</pre>
	 *
	 * @param tree the tree object to convert to a string
	 * @return a new expression string
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public static String toString(final Tree<? extends Op<Double>, ?> tree) {
		return toString(tree, new StringBuilder());
	}

	/**
	 * Parses the given {@code expression} into a AST tree.
	 *
	 * @param expression the expression string
	 * @return the tree representation of the given {@code expression}
	 */
	public static MathExpr parse(final String expression) {
		final Tree<? extends Op<Double>, ?> tree = parseTree(expression);
		Program.check(tree);
		return new MathExpr(tree, true);
	}

	/**
	 * Parses the given mathematical expression string and returns the
	 * mathematical expression tree. The expression may contain all functions
	 * defined in {@link MathOp}.
	 * <pre>{@code
	 * final TreeNode<Op<Double>> tree = MathExpr
	 *     .parseTree("5 + 6*x + sin(x)^34 + (1 + sin(x*5)/4)/6");
	 * }</pre>
	 * The example above will lead to the following tree:
	 * <pre> {@code
	 *  add
	 *  ├── add
	 *  │   ├── add
	 *  │   │   ├── 5.0
	 *  │   │   └── mul
	 *  │   │       ├── 6.0
	 *  │   │       └── x
	 *  │   └── pow
	 *  │       ├── sin
	 *  │       │   └── x
	 *  │       └── 34.0
	 *  └── div
	 *      ├── add
	 *      │   ├── 1.0
	 *      │   └── div
	 *      │       ├── sin
	 *      │       │   └── mul
	 *      │       │       ├── x
	 *      │       │       └── 5.0
	 *      │       └── 4.0
	 *      └── 6.0
	 * }</pre>
	 *
	 * @param expression the expression string
	 * @return the parsed expression tree
	 * @throws NullPointerException if the given {@code expression} is {@code null}
	 * @throws IllegalArgumentException if the given expression is invalid or
	 *         can't be parsed.
	 */
	public static Tree<? extends Op<Double>, ?>
	parseTree(final String expression) {
		return MathExprParser.parse(expression);
	}

	/**
	 * Evaluates the given {@code expression} with the given arguments.
	 *
	 * <pre>{@code
	 *  final double result = MathExpr.eval("2*z + 3*x - y", 3, 2, 1);
	 *  assert result == 9.0;
	 * }</pre>
	 *
	 * @see #apply(Double[])
	 * @see #eval(double...)
	 *
	 * @param expression the expression to evaluate
	 * @param args the expression arguments, in alphabetical order
	 * @return the evaluation result
	 * @throws NullPointerException if the given {@code program} is {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public static double eval(final String expression, final double... args) {
		return parse(expression).eval(args);
	}

	/**
	 * Tries to simplify the given math tree.
	 *
	 * <pre>{@code
	 * final Tree<? extends Op<Double>, ?> tree =
	 *     MathExpr.parseTree("4.0 + 4.0 + x*(5.0 + 13.0)");
	 * final Tree<? extends Op<Double>, ?> simplified = MathExpr.simplify(tree)
	 * System.out.println(simplified);
	 * }</pre>
	 * The simplified tree will be look like this:
	 * <pre> {@code
	 *  add
	 *  ├── 8.0
	 *  └── mul
	 *      ├── x
	 *      └── 18.0
	 * }</pre>
	 *
	 * @see #prune(TreeNode)
	 * @see #simplify()
	 *
	 * @param tree the math tree to simplify
	 * @return the new simplified tree
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public static Tree<? extends Op<Double>, ?>
	simplify(final Tree<? extends Op<Double>, ?> tree) {
		return MathExprSimplifier.prune(TreeNode.ofTree(tree));
	}

	/**
	 * Tries to simplify the given math tree in place.
	 *
	 * @see #simplify(Tree)
	 * @see #simplify()
	 *
	 * @param tree the math tree to simplify
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public static void prune(final TreeNode<Op<Double>> tree) {
		MathExprSimplifier.prune(tree);
	}

}
