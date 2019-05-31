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
package io.jenetics.ext.rewriting;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.IO;

import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TRSTest {

	@Test
	public void normalForm() {
		final TRS<String> trs = TRS.parse(
			"add ( 0, $x ) -> $x ",
			"add(S($x),$y) -> S(add($x,$y))",
			"mul(0,$x) -> 0",
			"mul(S($x),$y) -> add(mul($x,$y),$y)"
		);

		final TreeNode<String> tree = TreeNode.parse("add(S(0),S(mul(S(0),S(S(0)))))");
		trs.rewrite(tree);
		Assert.assertEquals(tree, TreeNode.parse("S(S(S(S(0))))"));
	}

	@Test
	public void serialize() throws IOException {
		final TRS<String> trs = TRS.parse(
			"add(0,$x) -> $x",
			"add(S($x),$y) -> S(add($x,$y))",
			"mul(0,$x) -> 0",
			"mul(S($x),$y) -> add(mul($x,$y),$y)"
		);

		final byte[] data = IO.object.toByteArray(trs);
		Assert.assertEquals(IO.object.fromByteArray(data), trs);
	}

}
