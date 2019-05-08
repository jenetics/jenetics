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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.IO;
import io.jenetics.util.ISeq;

import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeRewriteRuleTest {

	private static final ISeq<String> RULES = ISeq.of(
		"sub($x,$x) -> 0",
		"add($x,$x) -> mul(2,$x)",
		"sub($x,0) -> $x",
		"add($x,0) -> $x",
		"add(0,$x) -> $x",
		"div($x,$x) -> 1",
		"mul($x,0) -> 0",
		"mul(0,$x) -> 0",
		"mul($x,1) -> $x",
		"mul(1,$x) -> $x",
		"mul($x,$x) -> pow($x,2)",
		"pow($x,0) -> 1",
		"pos($x,1) -> $x"
	);

	private static final ISeq<TreeRewriter<String>> REWRITERS = RULES
		.map(TreeRewriteRule::parse);

	@Test(dataProvider = "trees")
	public void rewrite(final String treeString, final String rewrittenTreeString) {
		final TreeNode<String> tree = TreeNode.parse(treeString);

		TreeRewriter.rewrite(tree, REWRITERS);
		Assert.assertEquals(
			tree.toParenthesesString(),
			TreeNode.parse(rewrittenTreeString).toParenthesesString()
		);
	}

	@DataProvider
	public Object[][] trees() {
		return new Object[][] {
			{"sub(1,1)", "0"},
			{"sub(x,x)", "0"},
			{"sub(x(4,3),x(4,3))", "0"},
			{"sub(x(4,3(3,4)),x(4,3(3,4)))", "0"},
			{"add(1,1)", "2"},
			{"add(x,x)", "mul(2,x)"},
			{"sub(sin(4),0)", "sin(4)"},
			{"div(sin(4),sin(4))", "1"},
			{"sub(add(sub(sub(sin(x),y),0),tan(z)),0)", "add(sub(sin(x),y),tan(z))"}
		};
	}

	@Test
	public void serialize() throws IOException {
		final TreeRewriteRule<String> rule =
			TreeRewriteRule.parse("mul($x,$x) -> pow($x,2)");

		final byte[] data = IO.object.toByteArray(rule);
		Assert.assertEquals(IO.object.fromByteArray(data), rule);
	}

}
