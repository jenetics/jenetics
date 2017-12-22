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

import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MathOpExprTest {

	static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		MathOp.ADD,
		MathOp.SUB,
		MathOp.MUL,
		MathOp.DIV,
		MathOp.EXP,
		MathOp.SIN,
		MathOp.COS,
		MathOp.MIN,
		MathOp.MAX,
		MathOp.ABS,
		MathOp.POW
	);

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
	public void eval() {
		final MathOpExpr expr = MathOpExpr.parse("5 + 6*x + sin(x)^34 + (1 + sin(x)/4)/6");
		System.out.println(expr.apply(0.5));

		System.out.println(expr);
	}

	@Test(dataProvider = "ast")
	public void toStringAndParse(final Tree<? extends Op<Double>, ?> tree) {
		final String expression = new MathOpExpr(tree).toString();
		final MathOpExpr expr = MathOpExpr.parse(expression);
		Assert.assertEquals(expr.toString(), expression);
	}

	@DataProvider(name = "ast")
	public Object[][] ast() {
		return Stream.generate(() -> Program.of(7, OPERATIONS, TERMINALS))
			.limit(100)
			.map(p -> new Object[]{p})
			.toArray(Object[][]::new);
	}

}
