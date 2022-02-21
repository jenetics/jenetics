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
package io.jenetics.ext;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SingleNodeCrossoverTest {

	public TreeNode<Integer> newTree(final int levels, final RandomGenerator random) {
		final TreeNode<Integer> root = TreeNode.of(random.nextInt(1000));
		fill(root, levels, random);
		return root;
	}

	private static void fill(
		final TreeNode<Integer> node,
		final int level,
		final RandomGenerator random
	) {
		if (level > 0) {
			for (int i = 0, n = random.nextInt(5); i < n; ++i) {
				final TreeNode<Integer> child = TreeNode.of();
				child.value(random.nextInt());

				if (random.nextDouble() < 0.8) {
					fill(child, level - 1, random);
				}

				node.attach(child);
			}
		}
	}

	@Test
	public void crossover() throws IOException {
		final TreeNode<String> tree1 = TreeNode.of("0")
			.attach(TreeNode.of("1")
				.attach("4", "5"))
			.attach(TreeNode.of("2")
				.attach("6"))
			.attach(TreeNode.of("3")
				.attach(TreeNode.of("7")
					.attach("10", "11"))
				.attach(TreeNode.of("8"))
				.attach(TreeNode.of("9")));

		final TreeNode<String> tree2 = TreeNode.of("a")
			.attach(TreeNode.of("b")
				.attach("e", "f"))
			.attach(TreeNode.of("c")
				.attach("g"))
			.attach(TreeNode.of("d")
				.attach(TreeNode.of("h")
					.attach("k", "l"))
				.attach(TreeNode.of("i"))
				.attach(TreeNode.of("j")));

		final int size1 = tree1.size();
		final int size2 = tree2.size();

		/*
		Files.write(
			Paths.get("/home/fwilhelm/Workspace/SingleNodeCrossover-t1.dot"),
			Trees.toDottyString("t1", tree1).getBytes()
		);
		Files.write(
			Paths.get("/home/fwilhelm/Workspace/SingleNodeCrossover-ta.dot"),
			Trees.toDottyString("ta", tree2).getBytes()
		);
		*/

		//System.out.println(tree1);
		//System.out.println("------------------");
		//System.out.println(tree2);
		SingleNodeCrossover.swap(tree1, tree2);
		//System.out.println(tree1);
		//System.out.println("------------------");
		//System.out.println(tree2);

		/*
		Files.write(
			Paths.get("/home/fwilhelm/Workspace/SingleNodeCrossover-t1a.dot"),
			Trees.toDottyString("t1a", tree1).getBytes()
		);
		Files.write(
			Paths.get("/home/fwilhelm/Workspace/SingleNodeCrossover-ta1.dot"),
			Trees.toDottyString("ta1", tree2).getBytes()
		);
		*/

		Assert.assertEquals(tree1.size() + tree2.size(), size1 + size2);
		tree1.breadthFirstStream().forEach(n -> Objects.requireNonNull(n.value()));
		tree2.breadthFirstStream().forEach(n -> Objects.requireNonNull(n.value()));
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
