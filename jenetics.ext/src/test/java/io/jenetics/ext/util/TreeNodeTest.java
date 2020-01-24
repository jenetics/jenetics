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

import io.jenetics.ext.util.Tree.Path;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeNodeTest extends TreeTestBase<Integer, TreeNode<Integer>> {

	public TreeNode<Integer> newTree(final int levels, final Random random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		fill(root, levels, random);
		return root;
	}

	public static void fill(
		final TreeNode<Integer> node,
		final int level,
		final Random random
	) {
		for (int i = 0, n = random.nextInt(3) + 1; i < n; ++i) {
			final TreeNode<Integer> child = TreeNode.of();
			child.value(random.nextInt());

			if (level > 0) {
				fill(child, level - 1, random);
			}

			node.attach(child);
		}
	}

	@Test
	public void childIterator() {
		final TreeNode<Integer> tree = TreeNode.of(0).attach(1, 2, 3, 4, 5);
		Assert.assertEquals(tree.childStream().count(), tree.childCount());
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

		tree.childAt(1).insert(0, tree1);
		Assert.assertFalse(equals(tree, stree));

		((DefaultMutableTreeNode)stree.getChildAt(1)).insert(stree1, 0);
		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void replace() {
		final Random random = new Random(124);

		final TreeNode<Integer> tree = newTree(5, random);
		final TreeNode<Integer> tree1 = newTree(2, random);

		final TreeNode<Integer> child = tree.childAtPath(0 , 1)
			.orElseThrow(AssertionError::new);
		Assert.assertNotEquals(child, tree1);

		child.replace(0, tree1);
		Assert.assertEquals(child.childAt(0), tree1);
	}

	@Test
	public void replaceAt() {
		final Random random = new Random(124);

		final TreeNode<Integer> tree = newTree(5, random);
		final TreeNode<Integer> tree1 = newTree(2, random);

		final TreeNode<Integer> child = tree.childAtPath(0 , 1, 0)
			.orElseThrow(AssertionError::new);

		tree.replaceAtPath(Path.of(0, 1, 0), tree1);
		Assert.assertEquals(
			tree.childAtPath(0, 1, 0).orElseThrow(AssertionError::new),
			tree1
		);
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

		final TreeNode<Integer> detached = tree.childAt(1).detach();
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
				.map(TreeNode::value)
				.toArray(String[]::new),
			tree.stream()
				.map(TreeNode::value)
				.map(Objects::toString)
				.toArray(String[]::new)
		);
	}

	@Test
	public void serialize() throws IOException {
		final TreeNode<Integer> tree = newTree(6, new Random(345));
		final byte[] data = IO.object.toByteArray(tree);
		System.out.println(data.length);
		Assert.assertEquals(IO.object.fromByteArray(data), tree);
	}

}
