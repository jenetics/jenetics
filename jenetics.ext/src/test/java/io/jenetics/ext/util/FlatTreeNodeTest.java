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
import java.util.function.Function;

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

	public FlatTreeNode<Integer> newTree(final int levels, final Random random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		TreeNodeTest.fill(root, levels, random);
		return FlatTreeNode.of(root);
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
		assert Tree.equals(tree, nodes.get(0));

		final TreeNode<Integer> unflattened = TreeNode.ofTree(nodes.get(0));

		Assert.assertEquals(unflattened, tree);
		assert tree.equals(unflattened);
		assert unflattened.equals(tree);

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

	@Test(dataProvider = "methods")
	public void methodResults(final Function<Tree<Integer, ?>, Object> method) {
		final TreeNode<Integer> tree = TreeNode.of(0);
		TreeNodeTest.fill(tree, 3, new Random(345));

		final FlatTreeNode<Integer> flatTree = FlatTreeNode.of(tree);

		for (Tree<Integer, ?> node : tree) {
			Assert.assertEquals(method.apply(tree), method.apply(flatTree));
		}
	}

	@DataProvider
	public Object[][] methods() {
		return new Object[][] {
			{(Function<Tree<Integer, ?>, Object>)Tree::toParenthesesString},
			{(Function<Tree<Integer, ?>, Object>)Tree::level},
			{(Function<Tree<Integer, ?>, Object>)Tree::childCount},
			{(Function<Tree<Integer, ?>, Object>)Tree::childPath},
			{(Function<Tree<Integer, ?>, Object>)Tree::isLeaf},
			{(Function<Tree<Integer, ?>, Object>)t -> t.getRoot().getValue()},
			{(Function<Tree<Integer, ?>, Object>)t -> t.getParent().map(Tree::getValue).orElse(null)}
		};
	}


	@Test
	public void serialize() throws IOException {
		final FlatTreeNode<Integer> tree = newTree(6, new Random());
		final byte[] data = IO.object.toByteArray(tree);
		Assert.assertEquals(IO.object.fromByteArray(data), tree);
	}

}
