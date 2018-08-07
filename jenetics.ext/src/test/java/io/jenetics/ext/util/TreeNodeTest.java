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
import java.util.Objects;
import java.util.Random;

import javax.swing.tree.DefaultMutableTreeNode;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeNodeTest extends TreeTestBase<Integer, TreeNode<Integer>> {

	public TreeNode<Integer> newTree(final int levels, final Random random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		fill(root, levels, random);
		return root;
	}

	private static void fill(
		final TreeNode<Integer> node,
		final int level,
		final Random random
	) {
		for (int i = 0, n = random.nextInt(3) + 1; i < n; ++i) {
			final TreeNode<Integer> child = TreeNode.of();
			child.setValue(random.nextInt());

			if (level > 0) {
				fill(child, level - 1, random);
			}

			node.attach(child);
		}
	}

	@Test
	public void remove() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		tree.remove(0);
		stree.remove(0);
		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void insert() {
		final Random random = new Random(123);

		final TreeNode<Integer> tree = newTree(5, random);
		final TreeNode<Integer> tree1 = newTree(2, random);

		random.setSeed(123);
		final DefaultMutableTreeNode stree = newSwingTree(5, random);
		final DefaultMutableTreeNode stree1 = newSwingTree(2, random);

		tree.getChild(1).insert(0, tree1);
		Assert.assertFalse(equals(tree, stree));

		((DefaultMutableTreeNode)stree.getChildAt(1)).insert(stree1, 0);
		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void detach() {
		final TreeNode<Integer> tree = TreeNode.of(0)
			.attach(TreeNode.of(1)
				.attach(TreeNode.of(3))
				.attach(TreeNode.of(4)))
			.attach(TreeNode.of(2)
				.attach(TreeNode.of(5))
				.attach(TreeNode.of(6)));

		Assert.assertEquals(tree.size(), 7);

		final TreeNode<Integer> detached = tree.getChild(1).detach();
		Assert.assertEquals(tree.size(), 4);
		Assert.assertEquals(detached.size(), 3);
	}

	@Test
	public void copy() {
		final TreeNode<Integer> tree = newTree(6, new Random(123));
		final TreeNode<Integer> copy = tree.copy();

		Assert.assertEquals(copy, tree);
	}

	@Test
	public void map() {
		final TreeNode<Integer> tree = TreeNode.of(0)
			.attach(TreeNode.of(1)
				.attach(TreeNode.of(3))
				.attach(TreeNode.of(4)))
			.attach(TreeNode.of(2)
				.attach(TreeNode.of(5))
				.attach(TreeNode.of(6)));

		final TreeNode<String> mapped = tree.map(Objects::toString);

		Assert.assertEquals(
			mapped.stream()
				.map(TreeNode::getValue)
				.toArray(String[]::new),
			tree.stream()
				.map(TreeNode::getValue)
				.map(Objects::toString)
				.toArray(String[]::new)
		);
	}

	@Test
	public void serialize() throws IOException {
		final TreeNode<Integer> tree = newTree(6, new Random());
		final byte[] data = IO.object.toByteArray(tree);
		Assert.assertEquals(IO.object.fromByteArray(data), tree);
	}

}
