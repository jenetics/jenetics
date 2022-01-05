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

import io.jenetics.prog.op.Program;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.stream.IntStream;

import static io.jenetics.incubator.mathexpr.MathExprTestData.EXPRESSIONS;
import static io.jenetics.incubator.mathexpr.MathExprTestData.FUNCTIONS;


public class MathExprTest {

	@Test(dataProvider = "expressions")
	public void eval(final String expr, final MathExprTestData.Fun3 fun) {
		final double r1 = fun.apply(1.0, 2.0, 3.0);
		final double r2 = io.jenetics.prog.op.MathExpr.eval(expr, 1.0, 2.0, 3.0);
		final double r3 = Program.eval(MathExpr.parse(expr), 1.0, 2.0, 3.0);

		System.out.println(r1 + " = " + r2 + " = " + r3);
	}

	@DataProvider
	public Object[][] expressions() {
		return IntStream.range(0, EXPRESSIONS.size())
			.mapToObj(i -> new Object[] { EXPRESSIONS.get(i), FUNCTIONS.get(i) })
			.toArray(Object[][]::new);
	}

}
