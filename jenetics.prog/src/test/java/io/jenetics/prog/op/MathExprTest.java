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

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import java.io.IOException;
import java.util.Random;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.IO;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

import io.jenetics.ext.util.Tree;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MathExprTest {

	static final ISeq<Op<Double>> OPERATIONS = ISeq.of(MathOp.values());

	static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("u", 0),
		Var.of("v", 1),
		Var.of("w", 2),
		Var.of("x", 3),
		Var.of("y", 4),
		Var.of("z", 5),
		Const.of(1.0)
	);

	@Test
	public void vars() {
		final MathExpr expr = MathExpr.parse("a*b*c + a/4 - sin(b*a) + d*d*b");
		Assert.assertEquals(expr.vars().map(Var::name), ISeq.of("a", "b", "c", "d"));
	}

	@Test
	public void format() {
		final String expr = "(((5.0 - 6.0*x) - (3.0 + 4.0)) + sin(x)^34.0) + (1.0 + sin(x*5.0)/4.0)/6.0";
		Assert.assertEquals(MathExpr.format(MathExpr.parse(expr).toTree()), expr);
	}

	@Test
	public void format1() {
		final String expr = "(asin(z)*x)/1.0";
		Assert.assertEquals(MathExpr.format(MathExpr.parse(expr).toTree()), expr);
	}

	@Test
	public void parse() {
		Assert.assertEquals(
			MathExpr.eval("3*4"),
			12.0
		);
		Assert.assertEquals(
			MathExpr.eval("3*4*x", 2),
			24.0
		);
		Assert.assertEquals(
			MathExpr.eval("3*4*x + y", 2, 4),
			28.0
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void parseError1() {
		MathExpr.parse("x~y");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void parseError2() {
		MathExpr.parse("xy**g");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void evalError1() {
		MathExpr.eval("x+y");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void evalError2() {
		MathExpr.eval("x+y", 2);
	}

	@Test
	public void specialEval1() {
		final String expr = "(((((x - x) + (1.0*x))*((8.0 - 8.0)*(9.0*x))) + " +
			"(((8.0 - 9.0) + (x + x)) + ((x - 6.0)*(5.0*0.0)))) - ((((8.0 + 4.0)" +
			" - (x + x))*((x - x)*(6.0 - 9.0)))*(((x - 7.0) - (6.0 - x))*((2.0*x) " +
			"+ (x + 8.0)))))";

		final double arg = new Random().nextDouble();
		Assert.assertEquals(
			MathExpr.eval(expr, arg),
			MathExpr.eval("(-1.0 + (x + x))", arg)
		);
	}

	@Test
	public void specialEval2() {
		Assert.assertEquals(
			MathExpr.eval("2*z + 3*x - y", 3, 2, 1),
			9.0
		);
	}

	@Test
	public void specialEval3() {
		final String expr = "5 + 6*x + sin(x)^34 + (1 + sin(x*5)/4)/6";

		Assert.assertEquals(
			MathExpr.eval(expr, 4.32),
			31.170600453465315
		);
		Assert.assertEquals(
			MathExpr.eval(expr, 4.32),
			5.0 + 6*4.32 + pow(sin(4.32), 34) + (1 + sin(4.32*5)/4.0)/6.0
		);
	}

	@Test(dataProvider = "functionData")
	public void eval(final String expression, final F3 f, final double[] x) {
		Assert.assertEquals(
			MathExpr.eval(expression, x),
			f.apply(x[0], x[1], x[2])
		);

		Assert.assertEquals(
			MathExpr.eval(MathExpr.parse(expression).toString(), x),
			f.apply(x[0], x[1], x[2])
		);
	}

	@DataProvider(name = "functionData")
	public Object[][] functionData() {
		return new Object[][] {
			{"x*x + sin(z) - cos(x)*y*pow(z*x + y, x)", (F3)MathExprTest::f1, new double[]{1, 2, 3}},
			{"x*x + sin(z) - cos(x)*y*pow(z*x + y, x)", (F3)MathExprTest::f1, new double[]{1.5, 65, 13}},
			{"x*x + sin(z) - cos(x)*y*pow(z*x + y, x)", (F3)MathExprTest::f1, new double[]{10, 0, 53}},
			{"x*x + sin(z) - cos(x)*y*pow(z*x + y, x)", (F3)MathExprTest::f1, new double[]{11, 23, 39}},

			{"x*x + sin(z) - cos(x)*y*pow(z*x + y, pow(z*x + y, x))", (F3)MathExprTest::f2, new double[]{1.5, 2.6, 3.9}},
			{"x*x + sin(z) - cos(x)*y*pow(z*x + y, pow(pow(z*x + y, pow(z*x + y, x)), x))", (F3)MathExprTest::f3, new double[]{1.5, 2.6, 3.9}}
		};
	}

	private static double f1(final double x, final double y, final double z) {
		return x*x + sin(z) - cos(x)*y*pow(z*x + y, x);
	}

	private static double f2(final double x, final double y, final double z) {
		return x*x + sin(z) - cos(x)*y*pow(z*x + y, pow(z*x + y, x));
	}

	private static double f3(final double x, final double y, final double z) {
		return x*x + sin(z) - cos(x)*y*pow(z*x + y, pow(pow(z*x + y, pow(z*x + y, x)), x));
	}

	interface F3 {
		double apply(final double x, final double y, final double z);
	}

	@Test(dataProvider = "ast")
	public void toStringAndParse(final Tree<? extends Op<Double>, ?> tree) {
		final String expression = new MathExpr(tree).toString();
		final MathExpr expr = MathExpr.parse(expression);

		Assert.assertEquals(expr.toTree(), tree);
		Assert.assertEquals(expr.toString(), expression);
	}

	@DataProvider(name = "ast")
	public Object[][] ast() {
		final Random random = new Random(1233);
		return Stream.generate(() -> Program.of(20, OPERATIONS, TERMINALS, random))
			.limit(13)
			.map(p -> new Object[]{p})
			.toArray(Object[][]::new);
	}

	@Test(dataProvider = "ast")
	public void evalSimplified(final Tree<? extends Op<Double>, ?> tree) {
		final MathExpr expr = new MathExpr(tree);
		final Seq<Var<Double>> vars = expr.vars();
		final double[] args = new Random().doubles(vars.size()).toArray();
		final double value = expr.eval(args);

		final MathExpr simplified = expr.simplify();

		if (!expr.equals(simplified)) {
			Assert.assertEquals(
				simplified.eval(args),
				expr.eval(args)
			);
		}
	}

	@Test
	public void evalSimplifiedFromString() {
		final MathExpr expr = MathExpr.parse("x + 0 - y*1");
		System.out.println(expr.eval(10, 1));
		System.out.println(expr.simplify().eval(10, 1));
	}

	@Test(dataProvider = "ast")
	public void serialize(final Tree<? extends Op<Double>, ?> tree) throws IOException {
		final MathExpr object = new MathExpr(tree);
		final byte[] data = IO.object.toByteArray(object);
		Assert.assertEquals(IO.object.fromByteArray(data), object);
	}

}
