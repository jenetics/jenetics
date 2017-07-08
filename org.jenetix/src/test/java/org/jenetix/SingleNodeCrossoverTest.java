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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetix;

import java.util.Objects;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SingleNodeCrossoverTest {

	public TreeNode<Integer> newTree(final int levels, final Random random) {
		final TreeNode<Integer> root = TreeNode.of(random.nextInt(1000));
		fill(root, levels, random);
		return root;
	}

	private static void fill(
		final TreeNode<Integer> node,
		final int level,
		final Random random
	) {
		if (level > 0) {
			for (int i = 0, n = random.nextInt(5); i < n; ++i) {
				final TreeNode<Integer> child = TreeNode.of();
				child.setValue(random.nextInt());

				if (random.nextDouble() < 0.8) {
					fill(child, level - 1, random);
				}

				node.attach(child);
			}
		}
	}

	@Test
	public void crossover() {
		final TreeNode<Integer> tree1 = TreeNode.of(0)
			.attach(TreeNode.of(1)
				.attach(4, 5))
			.attach(TreeNode.of(2)
				.attach(6))
			.attach(TreeNode.of(3)
				.attach(TreeNode.of(7)
					.attach(10, 11))
				.attach(TreeNode.of(8))
				.attach(TreeNode.of(9)));

		final TreeNode<Integer> tree2 = TreeNode.of(20)
			.attach(TreeNode.of(21)
				.attach(24, 25))
			.attach(TreeNode.of(22)
				.attach(26))
			.attach(TreeNode.of(23)
				.attach(TreeNode.of(27)
					.attach(30, 31))
				.attach(TreeNode.of(28))
				.attach(TreeNode.of(29)));

		final int size1 = tree1.size();
		final int size2 = tree2.size();

		//System.out.println(tree1);
		//System.out.println("------------------");
		//System.out.println(tree2);
		SingleNodeCrossover.swap(tree1, tree2);
		//System.out.println(tree1);
		//System.out.println("------------------");
		//System.out.println(tree2);

		Assert.assertEquals(tree1.size() + tree2.size(), size1 + size2);
		tree1.breadthFirstStream().forEach(n -> Objects.requireNonNull(n.getValue()));
		tree2.breadthFirstStream().forEach(n -> Objects.requireNonNull(n.getValue()));
	}

	@Test(dataProvider = "treeLevels")
	public void crossover1(final int level1, final int level2) {
		final Random random = new Random();

		final TreeNode<Integer> tree1 = newTree(level1, random);
		final TreeNode<Integer> tree2 = newTree(level2, random);
		final int size1 = tree1.size();
		final int size2 = tree2.size();

		SingleNodeCrossover.swap(tree1, tree2);

		Assert.assertEquals(tree1.size() + tree2.size(), size1 + size2);
	}

	@DataProvider(name = "treeLevels")
	public Object[][] treeLevels() {
		return new Object[][] {
			{0, 0},
			{1, 0},
			{0, 1},
			{1, 1},
			{2, 1},
			{10, 5},
			{15, 11}
		};
	}

}
