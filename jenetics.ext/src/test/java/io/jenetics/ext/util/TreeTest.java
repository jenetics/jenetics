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
package io.jenetics.ext.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.util.Tree.Path;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeTest {

	private static final Tree<String, ?> TREE = TreeNode.of("1")
		.attach("2")
		.attach(TreeNode.of("3")
			.attach(TreeNode.of("4")
				.attach("5")
				.attach("6"))
			.attach(TreeNode.of("7")
				.attach("8")
				.attach("9")))
		.attach(TreeNode.of("10"));

	@Test
	public void streamOrderEqualIteratorOrder() {
		final Tree<String, ?> tree = TreeNode.parse(
			"FUN1(0,((FUN1(2,FUN1((FUN1(5,(FUN2(y,x)*x))/(FUN2(y,FUN1(8,4))*" +
			"FUN1(x,6))),4))/1)+((8-(FUN1(x,y)/y))-FUN2(((FUN1(y,(y+(FUN1(2,x)+" +
			"FUN1(3,2))))+FUN2(8,x))+9),FUN2(x,9)))))"
		);

		assertThat(tree.size()).isEqualTo(43);
		assertThat(tree.stream().count()).isEqualTo(43);
		final var it = tree.iterator();
		tree.stream().forEach(v -> assertThat(v).isEqualTo(it.next()));
	}

	@Test
	public void toParenthesesTree1() {
		final TreeNode<Integer> tree = TreeNode.of(0)
			.attach(TreeNode.of(1)
				.attach(4, 5))
			.attach(TreeNode.of(2)
				.attach(6))
			.attach(TreeNode.of(3)
				.attach(TreeNode.of(7)
					.attach(10, 11))
				.attach(8)
				.attach(9));

		final String parentheses = tree.toParenthesesString();
		Assert.assertEquals(
			TreeNode
				.parse(parentheses)
				.map(Integer::parseInt),
			tree
		);
	}

	@Test
	public void toParenthesesTree2() {
		final Tree<String, ?> tree = TreeNode.of("(root)")
			.attach(",", "(", ")");
		final String string = tree.toParenthesesString();

		Assert.assertEquals(string, "\\(root\\)(\\,,\\(,\\))");
	}

	@Test
	public void toParenthesesTree3() {
		final TreeNode<Integer> tree = TreeNode.of(0)
			.attach(TreeNode.of(1)
				.attach(4, 5))
			.attach(TreeNode.of(2)
				.attach(6))
			.attach(TreeNode.of(3)
				.attach(TreeNode.of(7)
					.attach(10, 11))
				.attach(8)
				.attach(9));

		final Tree<Integer, ?> parsed = TreeNode
			.parse("0(1(4,5),2(6),3(7(10,11),8,9))")
			.map(Integer::parseInt);

		Assert.assertEquals(parsed, tree);
	}

	@Test(dataProvider = "paths")
	public void childByPath(final int[] path, final String result)  {
		Assert.assertEquals(
			TREE.childAtPath(Path.of(path)).map(Tree::value),
			Optional.ofNullable(result)
		);
	}

	@DataProvider(name = "paths")
	public Object[][] paths() {
		return new Object[][] {
			{new int[0], "1"},
			{new int[]{0}, "2"},
			{new int[]{1}, "3"},
			{new int[]{1, 0}, "4"},
			{new int[]{1, 1}, "7"},
			{new int[]{1, 1, 0}, "8"},
			{new int[]{1, 1, 1}, "9"},
			{new int[]{2}, "10"},
			{new int[]{10}, null},
			{new int[]{0, 0, 0, 0}, null}
		};
	}

	@Test
	public void childPath() {
		TREE.forEach(node -> {
			final Path path = node.childPath();
			Assert.assertEquals(
				TREE.childAtPath(path).orElseThrow(AssertionError::new),
				node
			);
		});
	}

	@Test(dataProvider = "paths")
	public void path(final int[] path, final String result) {
		final Tree<String, ?> child = TREE.childAtPath(path).orElse(null);

		if (result == null) {
			Assert.assertNull(child);
		} else {
			Assert.assertSame(TREE.childAtPath(child.path()).get(), child);
		}
	}

	@Test
	public void reduce() {
		final Tree<String, ?> formula = TreeNode.parse(
			"add(sub(6, div(230, 10)), mul(5, 6))",
			String::trim
		);
		final double result = formula.reduce(new Double[0], (op, args) ->
			switch (op) {
				case "add" -> args[0] + args[1];
				case "sub" -> args[0] - args[1];
				case "mul" -> args[0] * args[1];
				case "div" -> args[0] / args[1];
				default -> Double.parseDouble(op);
			}
		);

		assertThat(result).isEqualTo(13.0);
	}

}
