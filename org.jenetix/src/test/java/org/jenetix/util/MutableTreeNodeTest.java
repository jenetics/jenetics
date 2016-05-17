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

import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import javax.swing.tree.DefaultMutableTreeNode;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MutableTreeNodeTest {

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
			child.setValue(random.nextInt(1_000_000));

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
			child.setUserObject(random.nextInt(1_000_000));

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

	private static boolean equals(
		final MutableTreeNode<Integer> t1,
		final DefaultMutableTreeNode t2
	) {
		return t1.getChildCount() == t2.getChildCount() &&
			Objects.equals(t1.getValue(), t2.getUserObject()) &&
			IntStream.range(0, t1.getChildCount())
				.allMatch(i -> t1.getChild(i).getValue()
					.equals(((DefaultMutableTreeNode)t2.getChildAt(i)).getUserObject()));
	}

}
