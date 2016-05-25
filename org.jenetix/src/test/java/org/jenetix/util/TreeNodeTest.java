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
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
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
public class TreeNodeTest {

	private static final class AccessorMethod {
		final String _name;
		final Function<TreeNode<Integer>, Object> _method1;
		final Function<DefaultMutableTreeNode, Object> _method2;

		private AccessorMethod(
			final String name,
			final Function<TreeNode<Integer>, Object> method1,
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

		static AccessorMethod of(
			final String name,
			final Function<TreeNode<Integer>, Object> method1,
			final Function<DefaultMutableTreeNode, Object> method2
		) {
			return new AccessorMethod(name, method1, method2);
		}
	}

	private static final class QueryMethod {
		final String _name;
		final BiFunction<TreeNode<Integer>, TreeNode<Integer>, Object> _method1;
		final BiFunction<DefaultMutableTreeNode, DefaultMutableTreeNode, Object> _method2;

		private QueryMethod(
			final String name,
			final BiFunction<TreeNode<Integer>, TreeNode<Integer>, Object> method1,
			final BiFunction<DefaultMutableTreeNode, DefaultMutableTreeNode, Object> method2
		) {
			_name = requireNonNull(name);
			_method1 = requireNonNull(method1);
			_method2 = requireNonNull(method2);
		}

		@Override
		public String toString() {
			return format("NodeMethod[%s]", _name);
		}

		static QueryMethod of(
			final String name,
			final BiFunction<TreeNode<Integer>, TreeNode<Integer>, Object> method1,
			final BiFunction<DefaultMutableTreeNode, DefaultMutableTreeNode, Object> method2
		) {
			return new QueryMethod(name, method1, method2);
		}
	}


	public static TreeNode<Integer> newTree(final int levels, final Random random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		fill(root, levels, random);

		return root;
	}

	private static void fill(
		final TreeNode<Integer> node,
		final int level,
		final Random random
	) {
		for (int i = 0, n = random.nextInt(5); i < n; ++i) {
			final TreeNode<Integer> child = TreeNode.of();
			child.setValue(random.nextInt());

			if (random.nextDouble() < 0.8 && level > 0) {
				fill(child, level - 1, random);
			}

			node.add(child);
		}
	}

	public DefaultMutableTreeNode newSwingTree(final int levels, final Random random) {
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode(0);
		fill(root, levels, random);

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
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void inequality() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));
		stree.setUserObject(39393);

		Assert.assertFalse(equals(tree, stree));
	}

	@Test
	public void getChild() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
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
	public void remove() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		tree.remove(0);
		stree.remove(0);
		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void copy() {
		final TreeNode<Integer> tree = newTree(6, new Random(123));
		final TreeNode<Integer> copy = tree.copy();

		Assert.assertEquals(copy, tree);
	}

	@Test
	public void equals() {
		final TreeNode<Integer> tree1 = newTree(6, new Random(123));
		final TreeNode<Integer> tree2 = newTree(6, new Random(123));

		Assert.assertEquals(tree2, tree1);
	}

	@Test
	public void nonEquals() {
		final TreeNode<Integer> tree1 = newTree(6, new Random(123));
		final TreeNode<Integer> tree2 = newTree(6, new Random(1232));

		Assert.assertNotEquals(tree2, tree1);
	}

	@Test
	public void preorderIterator() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<TreeNode<Integer>> treeIt = tree.preorderIterator();
		final Enumeration<?> streeIt = stree.preorderEnumeration();

		while (treeIt.hasNext()) {
			final TreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void postorderIterator() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<TreeNode<Integer>> treeIt = tree.postorderIterator();
		final Enumeration<?> streeIt = stree.postorderEnumeration();

		while (treeIt.hasNext()) {
			final TreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void breathFirstIterator() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<TreeNode<Integer>> treeIt = tree.breadthFirstIterator();
		final Enumeration<?> streeIt = stree.breadthFirstEnumeration();

		while (treeIt.hasNext()) {
			final TreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void depthFirstIterator() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<TreeNode<Integer>> treeIt = tree.depthFirstIterator();
		final Enumeration<?> streeIt = stree.depthFirstEnumeration();

		while (treeIt.hasNext()) {
			final TreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void pathFromAncestorIterator() {
		final TreeNode<Integer> tree = newTree(15, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(15, new Random(123));

		final Iterator<TreeNode<Integer>> treeIt =
			tree.firstLeaf().pathFromAncestorIterator(tree);
		final Enumeration<?> streeIt =
			stree.getFirstLeaf().pathFromAncestorEnumeration(stree);

		while (treeIt.hasNext()) {
			final TreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.getValue(), snode.getUserObject());
		}
	}

	@Test
	public void getPath() {
		final TreeNode<Integer> tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<TreeNode<Integer>> treeIt = tree.breadthFirstIterator();
		final Enumeration<?> streeIt = stree.breadthFirstEnumeration();

		while (treeIt.hasNext()) {
			final TreeNode<Integer> node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(
				node.getPath().map(TreeNode::getValue),
				ISeq.of(snode.getUserObjectPath())
			);
		}
	}

	@Test(dataProvider = "nodeQueryMethods")
	public void nodeQueryMethod(final QueryMethod method) {
		final Iterator<TreeNode<Integer>> tree = newTree(5, new Random(123))
			.breadthFirstIterator();
		final Enumeration<?> swing = newSwingTree(5, new Random(123))
			.breadthFirstEnumeration();

		while (tree.hasNext()) {
			final TreeNode<Integer> node1 = tree.next();
			final DefaultMutableTreeNode node2 = (DefaultMutableTreeNode)swing.nextElement();

			final Iterator<TreeNode<Integer>> tree1 = node1.breadthFirstIterator();
			final Enumeration<?> swing1 = node2.breadthFirstEnumeration();

			while (tree1.hasNext()) {
				final TreeNode<Integer> node21 = tree1.next();
				final DefaultMutableTreeNode node22 = (DefaultMutableTreeNode)swing1.nextElement();

				assertEqualNodes(
					Try(() -> method._method1.apply(node1, node21)),
					Try(() -> method._method2.apply(node2, node22))
				);
			}
		}
	}

	@DataProvider
	public Object[][] nodeQueryMethods() {
		return new Object[][] {
			{QueryMethod.of("isAncestor", TreeNode::isAncestor, DefaultMutableTreeNode::isNodeAncestor)},
			{QueryMethod.of("isDescendant", TreeNode::isDescendant, DefaultMutableTreeNode::isNodeDescendant)},
			{QueryMethod.of("sharedAncestor", TreeNode::sharedAncestor, DefaultMutableTreeNode::getSharedAncestor)},
			{QueryMethod.of("isRelated", TreeNode::isRelated, DefaultMutableTreeNode::isNodeRelated)},
			{QueryMethod.of("isChild", TreeNode::isChild, DefaultMutableTreeNode::isNodeChild)},
			{QueryMethod.of("childAfter", TreeNode::childAfter, DefaultMutableTreeNode::getChildAfter)},
			{QueryMethod.of("childBefore", TreeNode::childBefore, DefaultMutableTreeNode::getChildBefore)},
			{QueryMethod.of("isNodeSibling", TreeNode::isSibling, DefaultMutableTreeNode::isNodeSibling)}
		};
	}

	@Test(dataProvider = "nodeAccessorMethods")
	public void nodeAccessorMethod(final AccessorMethod method) {
		final Iterator<TreeNode<Integer>> tree = newTree(15, new Random(123))
			.breadthFirstIterator();
		final Enumeration<?> swing = newSwingTree(15, new Random(123))
			.breadthFirstEnumeration();

		while (tree.hasNext()) {
			final TreeNode<Integer> node1 = tree.next();
			final DefaultMutableTreeNode node2 = (DefaultMutableTreeNode)swing.nextElement();

			assertEqualNodes(
				Try(() -> method._method1.apply(node1)),
				Try(() -> method._method2.apply(node2))
			);
		}
	}

	@DataProvider
	public Object[][] nodeAccessorMethods() {
		return new Object[][] {
			{AccessorMethod.of("getParent", TreeNode::getParent, DefaultMutableTreeNode::getParent)},
			{AccessorMethod.of("depth", TreeNode::depth, DefaultMutableTreeNode::getDepth)},
			{AccessorMethod.of("level", TreeNode::level, DefaultMutableTreeNode::getLevel)},
			{AccessorMethod.of("getRoot", TreeNode::getRoot, DefaultMutableTreeNode::getRoot)},
			{AccessorMethod.of("isRoot", TreeNode::isRoot, DefaultMutableTreeNode::isRoot)},
			{AccessorMethod.of("nextNode", TreeNode::nextNode, DefaultMutableTreeNode::getNextNode)},
			{AccessorMethod.of("previousNode", TreeNode::previousNode, DefaultMutableTreeNode::getPreviousNode)},
			{AccessorMethod.of("firstChild", TreeNode::firstChild, DefaultMutableTreeNode::getFirstChild)},
			{AccessorMethod.of("lastChild", TreeNode::lastChild, DefaultMutableTreeNode::getLastChild)},
			{AccessorMethod.of("siblingCount", TreeNode::siblingCount, DefaultMutableTreeNode::getSiblingCount)},
			{AccessorMethod.of("nextSibling", TreeNode::nextSibling, DefaultMutableTreeNode::getNextSibling)},
			{AccessorMethod.of("previousSibling", TreeNode::previousSibling, DefaultMutableTreeNode::getPreviousSibling)},
			{AccessorMethod.of("isLeaf", TreeNode::isLeaf, DefaultMutableTreeNode::isLeaf)},
			{AccessorMethod.of("firstLeaf", TreeNode::firstLeaf, DefaultMutableTreeNode::getFirstLeaf)},
			{AccessorMethod.of("lastLeaf", TreeNode::lastLeaf, DefaultMutableTreeNode::getLastLeaf)},
			{AccessorMethod.of("nextLeaf", TreeNode::nextLeaf, DefaultMutableTreeNode::getNextLeaf)},
			{AccessorMethod.of("previousLeaf", TreeNode::previousLeaf, DefaultMutableTreeNode::getPreviousLeaf)},
			{AccessorMethod.of("leafCount", TreeNode::leafCount, DefaultMutableTreeNode::getLeafCount)}
		};
	}

	private static <T> Object Try(final Supplier<T> supplier) {
		Object result;
		try {
			result = supplier.get();
		} catch (Exception e) {
			result = e.getClass();
		}

		return result;
	}

	private static void assertEqualNodes(final Object o1, final Object o2) {
		final Object uo1 = unwrap(o1);
		final Object uo2 = unwrap(o2);

		if (uo1 instanceof TreeNode<?> && uo2 instanceof DefaultMutableTreeNode) {
			final TreeNode<?> n1 = (TreeNode<?>)uo1;
			final DefaultMutableTreeNode n2 = (DefaultMutableTreeNode)uo2;

			final Object v1 = n1.getValue();
			final Object v2 = n2.getUserObject();
			Assert.assertEquals(v1, v2);
		} else {
			Assert.assertEquals(uo1, uo1);
		}
	}

	private static Object unwrap(final Object object) {
		return object instanceof Optional<?>
			? ((Optional<?>)object).orElse(null)
			: object;
	}

	private static boolean equals(
		final TreeNode<Integer> t1,
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
