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
package io.jenetics.incubator.mathexpr;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.incubator.mathexpr.MathExprTestData.EXPRESSIONS;
import static io.jenetics.incubator.mathexpr.MathExprTestData.FUNCTIONS;

import java.util.List;
import java.util.stream.IntStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.mathexpr.MathExprTestData.Fun3;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MathExprTest {

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
		assertThat(tree.toParenthesesString()).isEqualTo(expected);
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
