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
package org.jenetix.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.swing.tree.DefaultMutableTreeNode;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MutableTreeNodeTest {

	private static final class TestMethod {
		final String _name;
		final Function<MutableTreeNode<Integer>, Object> _method1;
		final Function<DefaultMutableTreeNode, Object> _method2;

		private TestMethod(
			final String name,
			final Function<MutableTreeNode<Integer>, Object> method1,
			final Function<DefaultMutableTreeNode, Object> method2
		) {
			_name = requireNonNull(name);
			_method1 = requireNonNull(method1);
			_method2 = requireNonNull(method2);
		}

		@Override
		public String toString() {
			return format("NodeMethod[%s]", _name);
		}

		public static TestMethod of(
			final String name,
			final Function<MutableTreeNode<Integer>, Object> method1,
			final Function<DefaultMutableTreeNode, Object> method2
		) {
			return new TestMethod(name, method1, method2);
		}
	}


	public MutableTreeNode<Integer> newTree(final int levels, final Random random) {
		final MutableTreeNode<Integer> root = new MutableTreeNode<>(0);
		fill(root, 5, random);

		return root;
	}

	private void fill(
		final MutableTreeNode<Integer> node,
		final int level,
		final Random random
	) {
		for (int i = 0, n = random.nextInt(5); i < n; ++i) {
			final MutableTreeNode<Integer> child = new MutableTreeNode<>();
			child.setValue(random.nextInt());

			if (random.nextDouble() < 0.8 && level > 0) {
				fill(child, level - 1, random);
			}

			node.add(child);
		}
	}

	public DefaultMutableTreeNode newSwingTree(final int levels, final Random random) {
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode(0);
		fill(root, 5, random);

		return root;
	}

	private void fill(
		final DefaultMutableTreeNode node,
		final int level,
		final Random random
	) {
		for (int i = 0, n = random.nextInt(5); i < n; ++i) {
			final DefaultMutableTreeNode child = new DefaultMutableTreeNode();
			child.setUserObject(random.nextInt());

			if (random.nextDouble() < 0.8 && level > 0) {
				fill(child, level - 1, random);
			}

			node.add(child);
		}
	}

	@Test
	public void equality() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void inequality() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));
		stree.setUserObject(39393);

		Assert.assertFalse(equals(tree, stree));
	}

	@Test
	public void getChild() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		Assert.assertEquals(tree.childCount(), stree.getChildCount());
		Assert.assertEquals(
			tree.getChild(1).getValue(),
			((DefaultMutableTreeNode)stree.getChildAt(1)).getUserObject()
		);
	}

	@Test
	public void insert() {
		final Random random = new Random(123);

		final MutableTreeNode<Integer> tree = newTree(5, random);
		final MutableTreeNode<Integer> tree1 = newTree(2, random);

		random.setSeed(123);
		final DefaultMutableTreeNode stree = newSwingTree(5, random);
		final DefaultMutableTreeNode stree1 = newSwingTree(2, random);

		tree.getChild(1).insert(0, tree1);
		Assert.assertFalse(equals(tree, stree));

		((DefaultMutableTreeNode)stree.getChildAt(1)).insert(stree1, 0);
		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void remove() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		tree.remove(0);
		stree.remove(0);
		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void preorderIterator() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<MutableTreeNode<Integer>> treeIt = tree.preorderIterator();
		final Enumeration<?> streeIt = stree.preorderEnumeration();

		while (treeIt.hasNext()) {
			final MutableTreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void postorderIterator() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<MutableTreeNode<Integer>> treeIt = tree.postorderIterator();
		final Enumeration<?> streeIt = stree.postorderEnumeration();

		while (treeIt.hasNext()) {
			final MutableTreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void breathFirstIterator() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<MutableTreeNode<Integer>> treeIt = tree.breadthFirstIterator();
		final Enumeration<?> streeIt = stree.breadthFirstEnumeration();

		while (treeIt.hasNext()) {
			final MutableTreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void depthFirstIterator() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<MutableTreeNode<Integer>> treeIt = tree.depthFirstIterator();
		final Enumeration<?> streeIt = stree.depthFirstEnumeration();

		while (treeIt.hasNext()) {
			final MutableTreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void pathFromAncestorIterator() {
		final MutableTreeNode<Integer> tree = newTree(15, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(15, new Random(123));

		final Iterator<MutableTreeNode<Integer>> treeIt =
			tree.firstLeaf().pathFromAncestorIterator(tree);
		final Enumeration<?> streeIt =
			stree.getFirstLeaf().pathFromAncestorEnumeration(stree);

		while (treeIt.hasNext()) {
			final MutableTreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void getPath() {
		final MutableTreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<MutableTreeNode<Integer>> treeIt = tree.breadthFirstIterator();
		final Enumeration<?> streeIt = stree.breadthFirstEnumeration();

		while (treeIt.hasNext()) {
			final MutableTreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(
				node.getPath().map(MutableTreeNode::getValue),
				ISeq.of(snode.getUserObjectPath())
			);
		}
	}

	@Test(dataProvider = "nodeMethods")
	public void nodeMethod(final TestMethod method) {
		final Iterator<MutableTreeNode<Integer>> tree = newTree(15, new Random(123))
			.breadthFirstIterator();
		final Enumeration<?> swing = newSwingTree(15, new Random(123))
			.breadthFirstEnumeration();

		while (tree.hasNext()) {
			final MutableTreeNode<Integer> node1 = tree.next();
			final DefaultMutableTreeNode node2 = (DefaultMutableTreeNode)swing.nextElement();

			assertEqualNodes(
				method._method1.apply(node1),
				method._method2.apply(node2)
			);
		}
	}

	@DataProvider(name = "nodeMethods")
	public Object[][] nodeMethods() {
		return new Object[][] {
			{TestMethod.of("nextSibling", MutableTreeNode::nextSibling, DefaultMutableTreeNode::getNextSibling)},
			{TestMethod.of("previousSibling", MutableTreeNode::previousSibling, DefaultMutableTreeNode::getPreviousSibling)},
			{TestMethod.of("isLeaf", MutableTreeNode::isLeaf, DefaultMutableTreeNode::isLeaf)},
			{TestMethod.of("firstLeaf", MutableTreeNode::firstLeaf, DefaultMutableTreeNode::getFirstLeaf)},
			{TestMethod.of("lastLeaf", MutableTreeNode::lastLeaf, DefaultMutableTreeNode::getLastLeaf)},
			{TestMethod.of("nextLeaf", MutableTreeNode::nextLeaf, DefaultMutableTreeNode::getNextLeaf)},
			{TestMethod.of("nextLeaf", MutableTreeNode::previousLeaf, DefaultMutableTreeNode::getPreviousLeaf)},
			{TestMethod.of("leafCount", MutableTreeNode::leafCount, DefaultMutableTreeNode::getLeafCount)}
		};
	}

	private static void assertEqualNodes(final Object o1, final Object o2) {
		if (o1 instanceof MutableTreeNode<?> && o2 instanceof DefaultMutableTreeNode) {
			final MutableTreeNode<?> n1 = (MutableTreeNode<?>)o1;
			final DefaultMutableTreeNode n2 = (DefaultMutableTreeNode)o2;

			final Object v1 = n1.getValue();
			final Object v2 = n2.getUserObject();
			Assert.assertEquals(v1, v2);
		} else {
			Assert.assertEquals(o1, o2);
		}
	}

	private static boolean equals(
		final MutableTreeNode<Integer> t1,
		final DefaultMutableTreeNode t2
	) {
		return t1.childCount() == t2.getChildCount() &&
			Objects.equals(t1.getValue(), t2.getUserObject()) &&
			IntStream.range(0, t1.childCount())
				.allMatch(i -> equals(
					t1.getChild(i),
					(DefaultMutableTreeNode) t2.getChildAt(i)));
	}

}
