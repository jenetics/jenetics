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

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MathOpTest {

	@Test(dataProvider = "operations")
	public void toConst(
		final Const<Double> constant,
		final String string,
		final boolean matches
	) {
		final Optional<Const<Double>> c = MathOp.toConst(string);
		assertEquals(c.isPresent(), matches);
		if (matches) {
			assertEquals(c.orElseThrow(AssertionError::new), constant);
		}
	}

	@DataProvider
	public Object[][] operations() {
		return new Object[][] {
			{Const.of(1.0), "1", true},
			{Const.of(1.0), "1.0", true},
			{Const.of(1.0111), "1.0111", true},
			{Const.of(1.0), "b", false}
		};
	}

	@Test
	public void toMathOpWithReindex() {
		final TreeNode<Op<Double>> tree = TreeNode.parse(
			"add(mul(x,y),sub(y,x))",
			MathOp::toMathOp
		);

		assertEquals(Program.eval(tree, 10.0, 5.0).doubleValue(), 100.0);
		Var.reindex(tree);
		assertEquals(Program.eval(tree, 10.0, 5.0).doubleValue(), 45.0);
	}

	@Test
	public void toMathOpWithReindex2() {
		final TreeNode<Op<Double>> tree = TreeNode.parse(
			"add(mul(x,y),sub(y,x))",
			MathOp::toMathOp
		);

		assertEquals(Program.eval(tree, 10.0, 5.0).doubleValue(), 100.0);

		final Map<Var<Double>, Integer> indexes = new HashMap<>();
		indexes.put(Var.of("x"), 0);
		indexes.put(Var.of("y"), 1);
		Var.reindex(tree, indexes);

		assertEquals(Program.eval(tree, 10.0, 5.0).doubleValue(), 45.0);
	}

	@Test
	public void toMathOp() {
		final TreeNode<Op<Double>> tree = TreeNode.parse(
			"add(mul(x[0],y[1]),sub(y[1],x[0]))",
			MathOp::toMathOp
		);

		assertEquals(Program.eval(tree, 10.0, 5.0).doubleValue(), 45.0);
	}

}
