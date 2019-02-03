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
package io.jenetics.ext.internal.util;

import static io.jenetics.ext.internal.util.TreeRewriteRule.compile;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PatternTreeRewriteTest {

	private static final ISeq<TreeRewriteRule> RULES = ISeq.of(
		compile("sub(<x>,<x>) -> 0"),
		compile("add(<x>,<x>) -> mul(2,<x>)"),
		compile("sub(<x>,0) -> <x>"),
		compile("add(<x>,0) -> <x>"),
		compile("add(0,<x>) -> <x>"),
		compile("div(<x>,<x>) -> 1"),
		compile("mul(<x>,0) -> 0"),
		compile("mul(0,<x>) -> 0"),
		compile("mul(<x>,1) -> <x>"),
		compile("mul(1,<x>) -> <x>"),
		compile("mul(<x>,<x>) -> pow(<x>,2)"),
		compile("pow(<x>,0) -> 1"),
		compile("pos(<x>,1) -> <x>")
	);

	private static final PatternTreeRewriter<String> REWRITER =
		PatternTreeRewriter.of(RULES);

	@Test(dataProvider = "trees")
	public void rewrite(final String treeString, final String rewrittenTreeString) {
		final TreeNode<String> tree = TreeNode.parse(treeString);
		//final TreeNode<String> rewrittenTree = TreeNode.parse(rewrittenTreeString);

		REWRITER.rewrite(tree);

		System.out.println(tree.toParenthesesString());
	}

	@DataProvider
	public Object[][] trees() {
		return new Object[][] {
			{"sub(1,1)", "0"}
		};
	}

}
