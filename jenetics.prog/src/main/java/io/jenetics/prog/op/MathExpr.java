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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;
import static io.jenetics.prog.op.Numbers.box;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.TreeSet;
import java.util.function.Function;

import io.jenetics.internal.util.Lazy;
import io.jenetics.util.ISeq;

import io.jenetics.ext.rewriting.TreeRewriteRule;
import io.jenetics.ext.rewriting.TreeRewriter;
import io.jenetics.ext.util.FlatTreeNode;
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
 * @version 5.0
 * @since 4.1
 */
public final class MathExpr
	implements
		Function<Double[], Double>,
		Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * This tree-rewriter rewrites constant expressions to its single value.
	 *
	 * <pre>{@code
	 * final TreeNode<Op<Double>> tree = MathExpr.parseTree("1 + 2*(6 + 7)");
	 * MathExpr.CONST_REWRITER.rewrite(tree);
	 * assertEquals(tree.getValue(), Const.of(27.0));
	 * }</pre>
	 *
	 * @since 5.0
	 */
	public static final TreeRewriter<Op<Double>> CONST_REWRITER =
		ConstRewriter.DOUBLE;

	/**
	 * This rewriter implements some common arithmetic identities, in exactly
	 * this order.
	 * <pre> {@code
	 *     sub($x,$x) ->  0
	 *     sub($x,0)  ->  $x
	 *     add($x,0)  ->  $x
	 *     add(0,$x)  ->  $x
	 *     add($x,$x) ->  mul(2,$x)
	 *     div($x,$x) ->  1
	 *     div(0,$x)  ->  0
	 *     mul($x,0)  ->  0
	 *     mul(0,$x)  ->  0
	 *     mul($x,1)  ->  $x
	 *     mul(1,$x)  ->  $x
	 *     mul($x,$x) ->  pow($x,2)
	 *     pow($x,0)  ->  1
	 *     pow(0,$x)  ->  0
	 *     pow($x,1)  ->  $x
	 *     pow(1,$x)  ->  1
	 * }</pre>
	 *
	 * @since 5.0
	 */
	public static final TreeRewriter<Op<Double>> ARITHMETIC_REWRITER =
		TreeRewriter.concat(
			compile("sub($x,$x) -> 0"),
			compile("sub($x,0) -> $x"),
			compile("add($x,0) -> $x"),
			compile("add(0,$x) -> $x"),
			compile("add($x,$x) -> mul(2,$x)"),
			compile("div($x,$x) -> 1"),
			compile("div(0,$x) -> 0"),
			compile("mul($x,0) -> 0"),
			compile("mul(0,$x) -> 0"),
			compile("mul($x,1) -> $x"),
			compile("mul(1,$x) -> $x"),
			compile("mul($x,$x) -> pow($x,2)"),
			compile("pow($x,0) -> 1"),
			compile("pow(0,$x) -> 0"),
			compile("pow($x,1) -> $x"),
			compile("pow(1,$x) -> 1")
		);

	private static TreeRewriter<Op<Double>> compile(final String rule) {
		return TreeRewriteRule.parse(rule, MathOp::toMathOp);
	}

	/**
	 * Combination of the {@link #ARITHMETIC_REWRITER} and the
	 * {@link #CONST_REWRITER}, in this specific order.
	 *
	 * @since 5.0
	 */
	public static final TreeRewriter<Op<Double>> REWRITER = TreeRewriter.concat(
		ARITHMETIC_REWRITER,
		CONST_REWRITER
	);

	private final Tree<? extends Op<Double>, ?> _tree;

	private final Lazy<ISeq<Var<Double>>> _vars;

	// Primary constructor.
	private MathExpr(final Tree<? extends Op<Double>, ?> tree, boolean primary) {
		_tree = requireNonNull(tree);
		_vars = Lazy.of(() -> ISeq.of(
			_tree.stream()
				.filter(node -> node.value() instanceof Var)
				.map(node -> (Var<Double>)node.value())
				.collect(toCollection(() -> new TreeSet<>(comparing(Var::name))))
		));
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
		this(FlatTreeNode.ofTree(tree), true);
		Program.check(tree);
	}

	/**
	 * Return the variable list of this <em>math</em> expression.
	 *
	 * @return the variable list of this <em>math</em> expression
	 */
	public ISeq<Var<Double>> vars() {
		return _vars.get();
	}

	/**
	 * Return the math expression as operation tree.
	 *
	 * @return a new expression tree
	 */
	public TreeNode<Op<Double>> toTree() {
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
		final double val = apply(box(args));
		return val == -0.0 ? 0.0 : val;
	}

	@Override
	public int hashCode() {
		return Tree.hashCode(_tree);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof MathExpr other &&
			Tree.equals(other._tree, _tree);
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
		return format(_tree);
	}

	/**
	 * Simplifying {@code this} expression by applying the given {@code rewriter}
	 * and the given rewrite {@code limit}.
	 *
	 * @param rewriter the rewriter used for simplifying {@code this} expression
	 * @param limit the rewrite limit
	 * @return a newly created math expression object
	 * @throws NullPointerException if the {@code rewriter} is {@code null}
	 * @throws IllegalArgumentException if the {@code limit} is smaller than
	 *         zero
	 */
	public MathExpr simplify(
		final TreeRewriter<Op<Double>> rewriter,
		final int limit
	) {
		final TreeNode<Op<Double>> tree = toTree();
		rewriter.rewrite(tree, limit);
		return new MathExpr(FlatTreeNode.ofTree(tree), true);
	}

	/**
	 * Simplifying {@code this} expression by applying the given {@code rewriter}.
	 *
	 * @param rewriter the rewriter used for simplifying {@code this} expression
	 * @return a newly created math expression object
	 * @throws NullPointerException if the {@code rewriter} is {@code null}
	 */
	public MathExpr simplify(final TreeRewriter<Op<Double>> rewriter) {
		return simplify(rewriter, Integer.MAX_VALUE);
	}

	/**
	 * Simplifies {@code this} expression by applying the default
	 * {@link #REWRITER}.
	 *
	 * @return a newly created math expression object
	 */
	public MathExpr simplify() {
		return simplify(REWRITER);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.MATH_EXPR, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		final byte[] data = toString().getBytes(UTF_8);
		writeInt(data.length, out);
		out.write(data);
	}

	static MathExpr read(final DataInput in) throws IOException {
		final byte[] data = new byte[readInt(in)];
		in.readFully(data);
		return parse(new String(data, UTF_8));
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
	 *  assert MathExpr.format(tree.tree()).equals(expr);
	 * }</pre>
	 *
	 * @since 4.3
	 *
	 * @param tree the tree object to convert to a string
	 * @return a new expression string
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public static String format(final Tree<? extends Op<Double>, ?> tree) {
		return MathExprFormatter.format(tree);
	}

	/**
	 * Parses the given {@code expression} into a AST tree.
	 *
	 * @param expression the expression string
	 * @return the tree representation of the given {@code expression}
	 * @throws NullPointerException if the given {@code expression} is {@code null}
	 * @throws IllegalArgumentException if the given expression is invalid or
	 *         can't be parsed.
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
	 * final Tree<? extends Op<Double>, ?> tree = MathExpr
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
	public static TreeNode<Op<Double>> parseTree(final String expression) {
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
	 * @throws NullPointerException if the given {@code expression} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public static double eval(final String expression, final double... args) {
		return parse(expression).eval(args);
	}

	/**
	 * Evaluates the given {@code expression} with the given arguments.
	 *
	 * @see #apply(Double[])
	 * @see #eval(double...)
	 * @see #eval(String, double...)
	 *
	 * @since 4.4
	 *
	 * @param expression the expression to evaluate
	 * @param args the expression arguments, in alphabetical order
	 * @return the evaluation result
	 * @throws NullPointerException if the given {@code expression} is
	 *         {@code null}
	 */
	public static double eval(
		final Tree<? extends Op<Double>, ?> expression,
		final double... args
	) {
		return new MathExpr(expression, true).eval(args);
	}

	/**
	 * Applies the {@link #REWRITER} to the given (mutable) {@code tree}. The
	 * tree rewrite is done in place.
	 *
	 * @see TreeRewriter#rewrite(TreeNode, int)
	 *
	 * @since 5.0
	 *
	 * @param tree the tree to be rewritten
	 * @param limit the maximal number this rewrite rule is applied to the given
	 *        tree. This guarantees the termination of the rewrite method.
	 * @return the number of rewrites applied to the input {@code tree}
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 * @throws IllegalArgumentException if the {@code limit} is smaller than
	 *         one
	 */
	public static int rewrite(final TreeNode<Op<Double>> tree, final int limit) {
		return REWRITER.rewrite(tree, limit);
	}

	/**
	 * Applies the {@link #REWRITER} to the given (mutable) {@code tree}. The
	 * tree rewrite is done in place. The limit of the applied rewrites is set
	 * unlimited ({@link Integer#MAX_VALUE}).
	 *
	 * @see #rewrite(TreeNode, int)
	 * @see TreeRewriter#rewrite(TreeNode)
	 *
	 * @since 5.0
	 *
	 * @param tree the tree to be rewritten
	 * @return {@code true} if the tree has been changed (rewritten) by this
	 *         method, {@code false} if the tree hasn't been changed
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public static int rewrite(final TreeNode<Op<Double>> tree) {
		return rewrite(tree, Integer.MAX_VALUE);
	}

}
