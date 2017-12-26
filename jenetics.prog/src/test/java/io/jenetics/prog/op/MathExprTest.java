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
import static java.lang.Math.exp;
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
	/*
	{
		signum(abs(min((max((log10(sqr(y))*min(asin(y), cos(y))), asin(asin(min(1.0, x))))%((sin(rint(w))/(max(y, x)%sinh(1.0)))^(tanh(asin(y))%((z + u) - sin(y))))), sqr(sqr(abs(max(tan(x), cosh(v))))))));
	}*/

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

		//System.out.println(MathExpr.parse("max(x - x, abs(y))").tree());
		//System.out.println(MathExpr.parse("4.0 + 4.0 + (x*(5.0 + 13.0))").simplify().tree());
		//final MathExpr expr = MathExpr.parse("x*x + sin(z) - cos(x)*y*pow(z*x + y, pow(pow(z*x + y, pow(z*x + y, x)), x))");
		//System.out.println(expr);
		//System.out.println(expr.tree());
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

	static interface F3 {
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
		return Stream.generate(() -> Program.of(20, OPERATIONS, TERMINALS, new Random(125)))
			.limit(7)
			.map(p -> new Object[]{p})
			.toArray(Object[][]::new);
	}

	@Test(dataProvider = "simplifiedExpressions")
	public void simplify(final String expr, final String simplified) {
		Assert.assertEquals(
			MathExpr.parse(expr).simplify(),
			MathExpr.parse(simplified)
		);
	}

	@DataProvider(name = "simplifiedExpressions")
	public Object[][] simplifiedExpressions() {
		return new Object[][] {
			// X_DIV_X
			{"x/x", "1.0"},
			{"sin(pow(x, y))/sin(pow(x, y))", "1.0"},

			// X_SUB_X
			{"x-x", "0.0"},
			{"sin(pow(x, y)) - sin(pow(x, y))", "0.0"},

			// MUL_ZERO
			{"tan(x)*0", "0.0"},
			{"0*pow(x, x)", "0.0"},
			{"y*(pow(x, 0) - 1)", "0.0"},
			{"(pow(x, 0) - 1)*sin(43)", "0.0"},
			{"(pow(x, 0) - 1)*sin(y)", "0.0"},
			{"cos(z)*sin(0)", "0.0"},

			// POW_ZERO
			{"pow(x*y, 0)", "1.0"},
			{"pow(sin(x*y)*cos(z), 0)", "1.0"},
			{"pow(sin(x*y)*cos(k), x - x)", "1.0"},

			// Constant
			{"4.0 + 4.0 + x*(5.0 + 13.0)", "8.0 + (x*18.0)"},
			{"sin(0)", "0"},
			{"sin(x - x)", "0"},
			{"((x - 0.0)*((((9.0 - 8.0) + (1.0 + x)) - ((x*0.0) - (1.0 - x))) - (0.0 + (x - 0.0))))", "x"}
		};
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


	@Test(dataProvider = "ast")
	public void serialize(final Tree<? extends Op<Double>, ?> tree) throws IOException {
		final MathExpr object = new MathExpr(tree);
		final byte[] data = IO.object.toByteArray(object);
		Assert.assertEquals(IO.object.fromByteArray(data), object);
	}

}
