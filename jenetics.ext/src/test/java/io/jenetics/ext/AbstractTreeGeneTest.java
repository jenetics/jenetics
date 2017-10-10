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

import java.util.Random;

import org.testng.annotations.Test;

import io.jenetics.ext.util.FlatTreeNode;
import io.jenetics.ext.util.TreeNode;
import io.jenetics.ext.util.TreeTestBase;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class AbstractTreeGeneTest extends TreeTestBase<Integer, IntTreeGene> {

	@Override
	public IntTreeGene newTree(int levels, Random random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		fill(root, levels, random);

		final FlatTreeNode<Integer> flattened = FlatTreeNode.of(root);
		final ISeq<IntTreeGene> genes = flattened.stream()
			.map(n -> new IntTreeGene(n.getValue(), n.childOffset(), n.childCount()))
			.collect(ISeq.toISeq());

		return new IntTreeChromosome(genes).getRoot();
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

}

