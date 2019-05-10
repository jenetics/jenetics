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

import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConstExprRewriterTest {

	@Test(dataProvider = "expressions")
	public void rewrite(final String expr, final double value) {
		final TreeNode<Op<Double>> tree = MathExpr.parse(expr).toTree();
		new ConstExprRewriter().rewrite(tree);

		Assert.assertEquals(tree.getValue(), Const.of(value));
	}

	@DataProvider
	public Object[][] expressions() {
		return new Object[][] {
			{"1+2+3+4", 10.0},
			{"1+2*(6+7)", 27.0},
			{"sin(0)", 0.0},
			{"cos(0)", 1.0},
			{"cos(0) + sin(0)", 1.0},
			{"cos(0)*sin(0)", 0.0}
		};
	}

	@Test
	public void ephemeralConst() {
		final TreeNode<Op<Double>> tree = MathExpr.parse("1+2+3")
			.toTree()
			.map(n -> n instanceof Const
				? EphemeralConst.of(((Const<Double>) n)::value)
				: n);

		new ConstExprRewriter().rewrite(tree);
		Assert.assertEquals(tree.getValue(), Const.of(6.0));
	}

}
