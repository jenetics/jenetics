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
package org.jenetix;

import java.util.Random;

import org.testng.annotations.Test;

import org.jenetics.Chromosome;
import org.jenetics.util.ISeq;

import org.jenetix.util.FlatTree;
import org.jenetix.util.TreeNode;
import org.jenetix.util.TreeTestBase;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class AbstractTreeGeneTest extends TreeTestBase<Integer, TestTreeGene> {

	@Override
	public TestTreeGene newTree(int levels, Random random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		fill(root, levels, random);

		final FlatTree<Integer> flattened = FlatTree.of(root);
		final ISeq<TestTreeGene> genes = flattened.stream()
			.map(n -> new TestTreeGene(n.getValue(), n.childOffset(), n.childCount()))
			.collect(ISeq.toISeq());

		return new TestTreeChromosome(genes).getRoot();
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

			node.attach(child);
		}
	}

}

final class TestTreeGene extends AbstractTreeGene<Integer, TestTreeGene> {

	public TestTreeGene(
		final Integer allele,
		final int childOffset,
		final int childCount
	) {
		super(allele, childOffset, childCount);
	}

	@Override
	public TestTreeGene newInstance() {
		return null;
	}

	@Override
	public TestTreeGene newInstance(Integer value) {
		return null;
	}
}

final class TestTreeChromosome extends AbstractTreeChromosome<Integer, TestTreeGene> {

	public TestTreeChromosome(final ISeq<? extends TestTreeGene> genes) {
		super(genes);
	}

	@Override
	public Chromosome<TestTreeGene> newInstance() {
		return null;
	}

	@Override
	public Chromosome<TestTreeGene> newInstance(final ISeq<TestTreeGene> genes) {
		return null;
	}
}
