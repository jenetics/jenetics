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

import java.io.IOException;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.IO;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class FlatTreeNodeTest extends TreeTestBase<Integer, FlatTreeNode<Integer>> {

	public FlatTreeNode<Integer> newTree(final int levels, final Random random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		fill(root, levels, random);
		return FlatTreeNode.of(root);
	}

	private static void fill(
		final TreeNode<Integer> node,
		final int level,
		final Random random
	) {
		for (int i = 0, n = random.nextInt(3); i < n; ++i) {
			final TreeNode<Integer> child = TreeNode.of();
			child.setValue(random.nextInt());

			if (random.nextDouble() < 0.8 && level > 0) {
				fill(child, level - 1, random);
			}

			node.attach(child);
		}
	}

	@Test
	public void getParent() {
		final TreeNode<Integer> tree = TreeNode.of(0)
			.attach(TreeNode.of(1)
				.attach(4, 5))
			.attach(TreeNode.of(2)
				.attach(6))
			.attach(TreeNode.of(3)
				.attach(TreeNode.of(7)
					.attach(10, 11))
				.attach(TreeNode.of(8))
				.attach(TreeNode.of(9)));

		final ISeq<FlatTreeNode<Integer>> nodes =
			FlatTreeNode.of(tree).flattenedNodes();

		final TreeNode<Integer> unflattened = TreeNode.ofTree(nodes.get(0));

		//print(FlatTree.of(tree));
		//FlatTree.of(tree).childStream().map(t -> t.getValue()).forEach(System.out::println);
		//print(tree);

		//System.out.println(FlatTree.of(tree.getChild(1).firstLeaf()).getValue());
		//System.out.println(tree.firstLeaf().getValue());
		//System.out.println(tree);
	}

	private void print(final Tree<?, ?> tree) {
		System.out.println(tree);
		tree.breadthFirstStream().forEach(n -> {
			System.out.println("" + n.getParent().map(t -> t.getValue()) + "->" + n.getValue());
		});

		/*
		System.out.println(tree.getParent().map(t -> t.getValue()));
		System.out.println(tree.getChild(0).getParent().map(t -> t.getValue()));
		System.out.println(tree.getChild(0).getChild(0).getParent().map(t -> t.getValue()));
		System.out.println(tree.getChild(2).getChild(0).getChild(0).getParent().map(t -> t.getValue()));
		*/
	}

	@Test
	public void serialize() throws IOException {
		final FlatTreeNode<Integer> tree = newTree(6, new Random());
		final byte[] data = IO.object.toByteArray(tree);
		Assert.assertEquals(IO.object.fromByteArray(data), tree);
	}

}
