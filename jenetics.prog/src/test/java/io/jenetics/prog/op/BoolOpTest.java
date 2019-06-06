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
import org.testng.annotations.Test;

import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BoolOpTest {

	@Test
	public void and() {
		Assert.assertEquals(BoolOp.AND.eval(false, false), false);
		Assert.assertEquals(BoolOp.AND.eval(false, true), false);
		Assert.assertEquals(BoolOp.AND.eval(true, false), false);
		Assert.assertEquals(BoolOp.AND.eval(true, true), true);
	}

	@Test
	public void or() {
		Assert.assertEquals(BoolOp.OR.eval(false, false), false);
		Assert.assertEquals(BoolOp.OR.eval(false, true), true);
		Assert.assertEquals(BoolOp.OR.eval(true, false), true);
		Assert.assertEquals(BoolOp.OR.eval(true, true), true);
	}

	@Test
	public void xor() {
		Assert.assertEquals(BoolOp.XOR.eval(false, false), false);
		Assert.assertEquals(BoolOp.XOR.eval(false, true), true);
		Assert.assertEquals(BoolOp.XOR.eval(true, false), true);
		Assert.assertEquals(BoolOp.XOR.eval(true, true), false);
	}

	@Test
	public void imp() {
		Assert.assertEquals(BoolOp.IMP.eval(false, false), true);
		Assert.assertEquals(BoolOp.IMP.eval(false, true), true);
		Assert.assertEquals(BoolOp.IMP.eval(true, false), false);
		Assert.assertEquals(BoolOp.IMP.eval(true, true), true);
	}

	@Test
	public void eq() {
		Assert.assertEquals(BoolOp.EQU.eval(false, false), true);
		Assert.assertEquals(BoolOp.EQU.eval(false, true), false);
		Assert.assertEquals(BoolOp.EQU.eval(true, false), false);
		Assert.assertEquals(BoolOp.EQU.eval(true, true), true);
	}

	@Test
	public void not() {
		Assert.assertEquals(BoolOp.NOT.eval(false), true);
		Assert.assertEquals(BoolOp.NOT.eval(true), false);
	}

	@Test
	public void toBoolOp() {
		final TreeNode<Op<Boolean>> tree = TreeNode.parse(
			"and(xor(x[0],y[1]),equ(y[1],x[0]))",
			BoolOp::toBoolOp
		);
		Assert.assertFalse(Program.eval(tree, true, true));
	}

}
