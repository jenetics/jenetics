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
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import javax.swing.tree.DefaultMutableTreeNode;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class TreeTestBase<V, T extends Tree<? extends V, T>> {

	public abstract T newTree(final int levels, final RandomGenerator random);

	private final class AccessorMethod {
		final String _name;
		final Function<T, Object> _method1;
		final Function<DefaultMutableTreeNode, Object> _method2;

		AccessorMethod(
			final String name,
			final Function<T, Object> method1,
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

	}

	private final class QueryMethod {
		final String _name;
		final BiFunction<T, T, Object> _method1;
		final BiFunction<DefaultMutableTreeNode, DefaultMutableTreeNode, Object> _method2;

		QueryMethod(
			final String name,
			final BiFunction<T, T, Object> method1,
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

	}

	public DefaultMutableTreeNode newSwingTree(
		final int levels,
		final RandomGenerator random
	) {
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode(0);
		fill(root, levels, random);

		return root;
	}

	private void fill(
		final DefaultMutableTreeNode node,
		final int level,
		final RandomGenerator random
	) {
		for (int i = 0, n = random.nextInt(3) + 1; i < n; ++i) {
			final DefaultMutableTreeNode child = new DefaultMutableTreeNode();
			child.setUserObject(random.nextInt());

			if (level > 0) {
				fill(child, level - 1, random);
			}

			node.add(child);
		}
	}

	@Test
	public void equality() {
		final T tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		Assert.assertTrue(equals(tree, stree));
	}

	@Test
	public void inequality() {
		final T tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));
		stree.setUserObject(39393);

		Assert.assertFalse(equals(tree, stree));
	}

	@Test
	public void getChild() {
		final T tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		Assert.assertEquals(tree.childCount(), stree.getChildCount());
		Assert.assertEquals(
			tree.childAt(1).value(),
			((DefaultMutableTreeNode)stree.getChildAt(1)).getUserObject()
		);
	}

	@Test
	public void equals() {
		final T tree1 = newTree(6, new Random(123));
		final T tree2 = newTree(6, new Random(123));

		Assert.assertEquals(tree2, tree1);
	}

	@Test
	public void nonEquals() {
		final T tree1 = newTree(6, new Random(123));
		final T tree2 = newTree(6, new Random(1232));

		Assert.assertNotEquals(tree2, tree1);
	}

	@Test
	public void size() {
		final T tree = newTree(2, new Random(123));
		checkTreeSize(tree);

		for (int i = 0, n = tree.size(); i < n; ++i) {
			final T node = get(tree, i);
			checkTreeSize(node);
		}
	}

	private T get(final T tree, final int index) {
		int i = 0;
		final Iterator<T> it = tree.depthFirstIterator();
		while (it.hasNext()) {
			final T node = it.next();
			if (i++ == index) {
				return node;
			}
		}

		throw new AssertionError();
	}

	private void checkTreeSize(final T tree) {
		Assert.assertEquals(
			tree.size(),
			(int)tree.breadthFirstStream().count()
		);
	}

	@Test
	public void preorderIterator() {
		final T tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<T> treeIt = tree.preorderIterator();
		final Enumeration<?> streeIt = stree.preorderEnumeration();

		while (treeIt.hasNext()) {
			final T node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.value(), snode.getUserObject());
		}
	}

	@Test
	public void postorderIterator() {
		final T tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<T> treeIt = tree.postorderIterator();
		final Enumeration<?> streeIt = stree.postorderEnumeration();

		while (treeIt.hasNext()) {
			final T node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.value(), snode.getUserObject());
		}
	}

	@Test
	public void breathFirstIterator() {
		final T tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<T> treeIt = tree.breadthFirstIterator();
		final Enumeration<?> streeIt = stree.breadthFirstEnumeration();

		while (treeIt.hasNext()) {
			final T node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.value(), snode.getUserObject());
		}
	}

	@Test
	public void depthFirstIterator() {
		final T tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<T> treeIt = tree.depthFirstIterator();
		final Enumeration<?> streeIt = stree.depthFirstEnumeration();

		while (treeIt.hasNext()) {
			final T node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.value(), snode.getUserObject());
		}
	}

	@Test
	public void pathFromAncestorIterator() {
		final T tree = newTree(15, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(15, new Random(123));

		final Iterator<T> treeIt =
			tree.firstLeaf().pathFromAncestorIterator(tree);

		final Enumeration<?> streeIt =
			stree.getFirstLeaf().pathFromAncestorEnumeration(stree);

		while (treeIt.hasNext()) {
			final T node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(node.value(), snode.getUserObject());
		}
	}

	@Test
	public void getPath() {
		final T tree = newTree(5, new Random(123));
		final DefaultMutableTreeNode stree = newSwingTree(5, new Random(123));

		final Iterator<T> treeIt = tree.breadthFirstIterator();
		final Enumeration<?> streeIt = stree.breadthFirstEnumeration();

		while (treeIt.hasNext()) {
			final T node = treeIt.next();
			final DefaultMutableTreeNode snode = (DefaultMutableTreeNode)streeIt.nextElement();

			Assert.assertEquals(
				node.pathElements().map(t -> t.value()),
				ISeq.of(snode.getUserObjectPath())
			);
		}
	}

	@Test(dataProvider = "nodeQueryMethods")
	public void nodeQueryMethod(final QueryMethod method) {
		final Iterator<T> tree = newTree(5, new Random(123))
			.breadthFirstIterator();
		final Enumeration<?> swing = newSwingTree(5, new Random(123))
			.breadthFirstEnumeration();

		while (tree.hasNext()) {
			final T node1 = tree.next();
			final DefaultMutableTreeNode node2 = (DefaultMutableTreeNode)swing.nextElement();

			final Iterator<T> tree1 = node1.breadthFirstIterator();
			final Enumeration<?> swing1 = node2.breadthFirstEnumeration();

			while (tree1.hasNext()) {
				final T node21 = tree1.next();
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
			{new QueryMethod("isAncestor", Tree::isAncestor, DefaultMutableTreeNode::isNodeAncestor)},
			{new QueryMethod("isDescendant", Tree::isDescendant, DefaultMutableTreeNode::isNodeDescendant)},
			{new QueryMethod("sharedAncestor", Tree::sharedAncestor, DefaultMutableTreeNode::getSharedAncestor)},
			{new QueryMethod("isRelated", Tree::isRelated, DefaultMutableTreeNode::isNodeRelated)},
			{new QueryMethod("isChild", Tree::isChild, DefaultMutableTreeNode::isNodeChild)},
			{new QueryMethod("childAfter", Tree::childAfter, DefaultMutableTreeNode::getChildAfter)},
			{new QueryMethod("childBefore", Tree::childBefore, DefaultMutableTreeNode::getChildBefore)},
			{new QueryMethod("isNodeSibling", Tree::isSibling, DefaultMutableTreeNode::isNodeSibling)}
		};
	}

	@Test(dataProvider = "nodeAccessorMethods")
	public void nodeAccessorMethod(final AccessorMethod method) {
		final Iterator<T> tree = newTree(7, new Random(123))
			.breadthFirstIterator();
		final Enumeration<?> swing = newSwingTree(7, new Random(123))
			.breadthFirstEnumeration();

		while (tree.hasNext()) {
			final T node1 = tree.next();
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
			{new AccessorMethod("getParent", Tree::parent, DefaultMutableTreeNode::getParent)},
			{new AccessorMethod("depth", Tree::depth, DefaultMutableTreeNode::getDepth)},
			{new AccessorMethod("level", Tree::level, DefaultMutableTreeNode::getLevel)},
			{new AccessorMethod("getRoot", Tree::root, DefaultMutableTreeNode::getRoot)},
			{new AccessorMethod("isRoot", Tree::isRoot, DefaultMutableTreeNode::isRoot)},
			{new AccessorMethod("nextNode", Tree::nextNode, DefaultMutableTreeNode::getNextNode)},
			{new AccessorMethod("previousNode", Tree::previousNode, DefaultMutableTreeNode::getPreviousNode)},
			{new AccessorMethod("firstChild", Tree::firstChild, DefaultMutableTreeNode::getFirstChild)},
			{new AccessorMethod("lastChild", Tree::lastChild, DefaultMutableTreeNode::getLastChild)},
			{new AccessorMethod("siblingCount", Tree::siblingCount, DefaultMutableTreeNode::getSiblingCount)},
			{new AccessorMethod("nextSibling", Tree::nextSibling, DefaultMutableTreeNode::getNextSibling)},
			{new AccessorMethod("previousSibling", Tree::previousSibling, DefaultMutableTreeNode::getPreviousSibling)},
			{new AccessorMethod("isLeaf", Tree::isLeaf, DefaultMutableTreeNode::isLeaf)},
			{new AccessorMethod("firstLeaf", Tree::firstLeaf, DefaultMutableTreeNode::getFirstLeaf)},
			{new AccessorMethod("lastLeaf", Tree::lastLeaf, DefaultMutableTreeNode::getLastLeaf)},
			{new AccessorMethod("nextLeaf", Tree::nextLeaf, DefaultMutableTreeNode::getNextLeaf)},
			{new AccessorMethod("previousLeaf", Tree::previousLeaf, DefaultMutableTreeNode::getPreviousLeaf)},
			{new AccessorMethod("leafCount", Tree::leafCount, DefaultMutableTreeNode::getLeafCount)}
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

		if (uo1 instanceof Tree&& uo2 instanceof DefaultMutableTreeNode) {
			final Tree n1 = (Tree)uo1;
			final DefaultMutableTreeNode n2 = (DefaultMutableTreeNode)uo2;

			final Object v1 = n1.value();
			final Object v2 = n2.getUserObject();
			Assert.assertEquals(v1, v2);
		} else {
			Assert.assertEquals(uo1, uo1);
		}
	}

	private static Object unwrap(final Object object) {
		return object instanceof Optional
			? ((Optional<?>)object).orElse(null)
			: object;
	}

	public static boolean equals(
		final Tree<?, ?> t1,
		final DefaultMutableTreeNode t2
	) {
		return t1.childCount() == t2.getChildCount() &&
			Objects.equals(t1.value(), t2.getUserObject()) &&
			IntStream.range(0, t1.childCount())
				.allMatch(i -> equals(
					t1.childAt(i),
					(DefaultMutableTreeNode) t2.getChildAt(i)));
	}

}
