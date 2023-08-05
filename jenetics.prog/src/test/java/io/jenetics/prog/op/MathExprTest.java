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
import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.prog.op.MathExprTestData.EXPRESSIONS;
import static io.jenetics.prog.op.MathExprTestData.FUNCTIONS;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.IO;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.MathExprTestData.Fun3;

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
		Assert.assertEquals(MathExpr.format(MathExpr.parse(expr).tree()), expr);
	}

	@Test
	public void format1() {
		final String expr = "(asin(z)*x)/1.0";
		Assert.assertEquals(MathExpr.format(MathExpr.parse(expr).tree()), expr);
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
		MathExpr.parse("xy***g");
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

		Assert.assertEquals(expr.tree(), tree);
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
		final MathExpr expr = MathExpr.parse("x + 0 - (-y)*1");
		assertThat(expr.eval(10, 1)).isEqualTo(11.0);
		assertThat(expr.simplify().eval(10, 1)).isEqualTo(11.0);
	}

	@Test(dataProvider = "ast")
	public void serialize(final Tree<? extends Op<Double>, ?> tree) throws IOException {
		final MathExpr object = new MathExpr(tree);
		final byte[] data = IO.object.toByteArray(object);
		Assert.assertEquals(IO.object.fromByteArray(data), object);
	}


	///

	private static final List<String> SIMPLE_EXPRESSIONS = List.of(
		"5 + 6*x + sin(x)^34 + (1 + sin(x*5)/4)/6",
		"2*z + 3*x - y",
		"x*x + sin(z) - cos(x)*y*(z*x + y)^x",
		"x*x + sin(z) - cos(x)*y*pow(z*x + y, (z*x + y)^x)",
		"x*x + sin(z) - cos(x)*y*pow(z*x + y, pow((z*x + y)^pow(z*x + y, x), x))"
	);

	private static final List<Fun3> SIMPLE_FUNCTIONS = List.of(
		MathExprTest::simple_fun_0,
		MathExprTest::simple_fun_1,
		MathExprTest::simple_fun_2,
		MathExprTest::simple_fun_3,
		MathExprTest::simple_fun_4
	);

	@Test(dataProvider = "basicExpressions")
	public void parseBasicExpressions(final String expr, final String expected) {
		final var tree = MathExpr.parse(expr);
		assertThat(tree.tree().toParenthesesString()).isEqualTo(expected);
	}

	@DataProvider
	public Object[][] basicExpressions() {
		return new Object[][] {
			{"3", "3.0"},
			{"-3", "neg(3.0)"},
			{"+3", "id(3.0)"},
			{"x", "x"},
			{"x*x+y", "add(mul(x,x),y)"},
			{"x+x*y", "add(x,mul(x,y))"},
			{"sin(x)", "sin(x)"},
			{"sub(sub(3.0,2.0),1.0)", "sub(sub(3.0,2.0),1.0)"},
			{"3-2-1", "sub(sub(3.0,2.0),1.0)"},
			{"3-2*1", "sub(3.0,mul(2.0,1.0))"},
			{"5*x^y*8", "mul(mul(5.0,pow(x,y)),8.0)"},
			{"x**3", "pow(x,3.0)"},
			{"sin(x)**y", "pow(sin(x),y)"},
			{"pow(x,x)**y", "pow(pow(x,x),y)"},
			{"pow((z*x + y)^pow(z*x + y, x), x)", "pow(pow(add(mul(z,x),y),pow(add(mul(z,x),y),x)),x)"},
			{"pow((z*x + y)**pow(z*x + y, x), x)", "pow(pow(add(mul(z,x),y),pow(add(mul(z,x),y),x)),x)"}
		};
	}

	@Test(dataProvider = "basicFunctions")
	public void evalBasicFunctions(final String expr, final MathExprTestData.Fun3 fun) {
		final double r1 = fun.apply(1.0, 2.0, 3.0);
		final double r2 = io.jenetics.prog.op.MathExpr.eval(expr, 1.0, 2.0, 3.0);
		final double r3 = MathExpr.eval(expr, 1.0, 2.0, 3.0);

		if (Double.isFinite(r3)) {
			assertThat(r3).isEqualTo(r1);
			assertThat(r2).isEqualTo(r1);
		} else if (Double.isNaN(r1)) {
			assertThat(r3).isNaN();
			assertThat(r2).isNaN();
		} else if (Double.isInfinite(r1)) {
			assertThat(r3).isInfinite();
			assertThat(r2).isInfinite();
		} else {
			throw new AssertionError();
		}
	}

	@DataProvider
	public Object[][] basicFunctions() {
		return IntStream.range(0, SIMPLE_EXPRESSIONS.size())
			.mapToObj(i -> new Object[] { SIMPLE_EXPRESSIONS.get(i), SIMPLE_FUNCTIONS.get(i) })
			.toArray(Object[][]::new);
	}

	@Test(dataProvider = "functions")
	public void evalFunctions(final String expr, final MathExprTestData.Fun3 fun) {
		final double r1 = fun.apply(1.0, 2.0, 3.0);
		final double r2 = io.jenetics.prog.op.MathExpr.eval(expr, 1.0, 2.0, 3.0);
		final double r3 = MathExpr.eval(expr, 1.0, 2.0, 3.0);

		if (Double.isFinite(r3)) {
			assertThat(r3).isEqualTo(r1);
			assertThat(r2).isEqualTo(r1);
		} else if (Double.isNaN(r1)) {
			assertThat(r3).isNaN();
			assertThat(r2).isNaN();
		} else if (Double.isInfinite(r1)) {
			assertThat(r3).isInfinite();
			assertThat(r2).isInfinite();
		} else {
			throw new AssertionError();
		}
	}

	@DataProvider
	public Object[][] functions() {
		return IntStream.range(0, EXPRESSIONS.size())
			.mapToObj(i -> new Object[] { EXPRESSIONS.get(i), FUNCTIONS.get(i) })
			.toArray(Object[][]::new);
	}

	private static double simple_fun_0(final double x, final double y, final double z) {
		return 5.0 + 6*x + pow(sin(x), 34) + (1 + sin(x*5)/4.0)/6.0;
	}

	private static double simple_fun_1(final double x, final double y, final double z) {
		return 2*z + 3*x - y;
	}

	private static double simple_fun_2(final double x, final double y, final double z) {
		return x*x + sin(z) - cos(x)*y*pow(z*x + y, x);
	}

	private static double simple_fun_3(final double x, final double y, final double z) {
		return x*x + sin(z) - cos(x)*y*pow(z*x + y, pow(z*x + y, x));
	}

	private static double simple_fun_4(final double x, final double y, final double z) {
		return x*x + sin(z) - cos(x)*y*pow(z*x + y, pow(pow(z*x + y, pow(z*x + y, x)), x));
	}

}
