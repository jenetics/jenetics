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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.rewriting.TreePattern;
import io.jenetics.ext.rewriting.TreeRewriteRule;
import io.jenetics.ext.rewriting.TreeRewriter;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MathExprRewriterTest {

	@Test
	public void rewriting() {
		final TreeRewriter<Op<Double>> rewriter = TreeRewriteRule
			.parse("div($x,$x) -> 1", MathOp::toMathOp);

		final TreePattern<Op<Double>> pattern = TreePattern
			.compile("div($x,$x)", MathOp::toMathOp);

		final Tree<Op<Double>, ?> expr = MathExpr.parse("2/2").toTree();
		final TreeNode<Op<Double>> tree = TreeNode.ofTree(expr);
		rewriter.rewrite(tree);
	}

	@Test(dataProvider = "expressions")
	public void rewrite(final String expr, final String simplified) {
		Assert.assertEquals(
			MathExpr.parse(expr).simplify(),
			MathExpr.parse(simplified)
		);
	}

	@DataProvider
	public Object[][] expressions() {
		return new Object[][] {
			// X_DIV_X
			{"x/x", "1.0"},
			{"sin(pow(x, y))/sin(pow(x, y))", "1.0"},

			// X_SUB_X
			{"x-x", "0.0"},
			{"sin(pow(x, y)) - sin(pow(x, y))", "0.0"},

			// X_ADD_X
			{"x+x", "2*x"},
			{"sin(x)*tan(y) + sin(x)*tan(y)", "2*(sin(x)*tan(y))"},

			// SUB_ZERO
			{"x - 0", "x"},
			{"sin(x) - y - 0 + tan(z) - 0", "sin(x) - y + tan(z)"},

			// ADD_ZERO
			{"x + 0", "x"},
			{"0 + x", "x"},
			{"0 + x + 0", "x"},
			{"sin(x) - y + 0 + tan(z) + 0", "sin(x) - y + tan(z)"},

			// MUL_ZERO
			{"tan(x)*0", "0.0"},
			{"0*pow(x, x)", "0.0"},
			{"y*(pow(x, 0) - 1)", "0.0"},
			{"(pow(x, 0) - 1)*sin(43)", "0.0"},
			{"(pow(x, 0) - 1)*sin(y)", "0.0"},
			{"cos(z)*sin(0)", "0.0"},

			// MUL_ONE
			{"x * 1", "x"},
			{"1 * x", "x"},
			{"1 * x * 1", "x"},
			{"1.0*((x - ((x - x*x) - (x - x*x)*(x - x*x))*x) - (x - x*x)*x)",
				"(x - ((x - x^2.0) - (x - x^2.0)^2.0)*x) - (x - x^2.0)*x"},
			{"sin(x) - y * 1 + tan(z) + 0", "sin(x) - y + tan(z)"},

			// X_MUL_X
			{"x*x", "x^2"},
			{"(sin(x)*tan(y))*(sin(x)*tan(y))", "(sin(x)*tan(y))^2"},

			// POW_ZERO
			{"pow(x*y, 0)", "1.0"},
			{"pow(sin(x*y)*cos(z), 0)", "1.0"},
			{"pow(sin(x*y)*cos(k), x - x)", "1.0"},
			{"pow(sin(x*y)*cos(k), sin(x - x*1 - 0 + 0)/1 + 0 - sin(0)) + 1", "2.0"},

			// POW_ONE
			{"pow(x*y, 1)", "x*y"},
			{"pow(sin(x*y)*cos(z), 1)", "sin(x*y)*cos(z)"},
			{"pow(sin(x*y)*cos(k), x/x)", "sin(x*y)*cos(k)"},

			// Constant
			{"4.0 + 4.0 + x*(5.0 + 13.0)", "8.0 + (x*18.0)"},
			{"sin(0)", "0"},
			{"sin(PI/2)", "1"},
			{"sin(π/2)", "1"},
			{"sin(x - x)", "0"},
			{"3*4*x", "12*x"},
			{"x + x*(((7.0 - 3.0) - (7.0 - (8.0 - 4.0)*x))*x)",
				"x + x*((4.0 - (7.0 - 4.0*x))*x)"},
			{"((x - 0.0) - ((x + 8.0) - (4.0 - x))) + (((5.0 - 4.0)*(6.0 - 6.0) + 1.0) - " +
				"((((x + x) + 5.0) - (9.0 - 2.0)) - ((4.0 - x)*x + (x + x))))",
				"(x - ((x + 8.0) - (4.0 - x))) + (1.0 - (((2.0*x + 5.0) - 7.0) - ((4.0 - x)*x + 2.0*x)))"}
		};
	}

}
