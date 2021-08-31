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
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.IO;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class FlatTreeNodeTest extends TreeTestBase<Integer, FlatTreeNode<Integer>> {

	public FlatTreeNode<Integer> newTree(final int levels, final RandomGenerator random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		TreeNodeTest.fill(root, levels, random);
		return FlatTreeNode.ofTree(root);
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
			FlatTreeNode.ofTree(tree).flattenedNodes();
		assert Tree.equals(tree, nodes.get(0));

		final TreeNode<Integer> unflattened = TreeNode.ofTree(nodes.get(0));

		Assert.assertEquals(unflattened, tree);
		assert tree.equals(unflattened);
		assert unflattened.equals(tree);
	}

	private void print(final Tree<?, ?> tree) {
		System.out.println(tree);
		tree.breadthFirstStream().forEach(n ->
			System.out.println("" + n.parent().map(Tree::value) + "->" + n.value()));
	}

	@Test(dataProvider = "methods")
	public void methodResults(final Function<Tree<?, ?>, Object> method) {
		final TreeNode<Integer> tree = TreeNode.of(0);
		TreeNodeTest.fill(tree, 2, new Random(345));
		final FlatTreeNode<Integer> flatTree = FlatTreeNode.ofTree(tree);

		final Iterator<? extends Tree<?, ?>> it1 = tree.iterator();
		final Iterator<? extends Tree<?, ?>> it2 = flatTree.iterator();
		while (it1.hasNext()) {
			final Tree<?, ?> node1 = it1.next();
			final Tree<?, ?> node2 = it2.next();
			Assert.assertEquals(method.apply(node1), method.apply(node2));
		}
	}

	@DataProvider
	public Object[][] methods() {
		return new Object[][] {
			{(Function<Tree<?, ?>, Object>)Tree::toParenthesesString},
			{(Function<Tree<?, ?>, Object>)Tree::level},
			{(Function<Tree<?, ?>, Object>)Tree::childCount},
			{(Function<Tree<?, ?>, Object>)Tree::childPath},
			{(Function<Tree<?, ?>, Object>)Tree::isLeaf},
			{(Function<Tree<?, ?>, Object>)t -> t.root().value()},
			{(Function<Tree<?, ?>, Object>)t -> t.parent().map(Tree::value).orElse(null)}
		};
	}

	@Test
	public void serialize() throws IOException {
		final FlatTreeNode<Integer> tree = newTree(6, new Random());
		final byte[] data = IO.object.toByteArray(tree);
		Assert.assertEquals(IO.object.fromByteArray(data), tree);
	}

	@Test
	public void serializeSubTree() throws IOException {
		final FlatTreeNode<Integer> tree = newTree(6, new Random(234));
		final FlatTreeNode<Integer> node = tree.childAtPath(0, 1, 0)
			.orElseThrow(AssertionError::new);

		final byte[] data = IO.object.toByteArray(node);
		Assert.assertEquals(IO.object.fromByteArray(data), node);
	}

}
