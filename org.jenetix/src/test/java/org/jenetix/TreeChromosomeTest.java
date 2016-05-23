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

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.SwapMutator;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

import org.jenetix.util.MutableTreeNode;
import org.jenetix.util.MutableTreeNodeTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TreeChromosomeTest {

	@Test
	public void create() {
		final MutableTreeNode<Integer> tree =
			MutableTreeNodeTest.newTree(3, new Random(123));

		final TreeChromosome<Integer> chromosome = TreeChromosome.of(tree);
		System.out.println(chromosome.length());
		System.out.println(chromosome);

		final MutableTreeNode<Integer> tree2 = chromosome.toTree();

		final ISeq<Integer> seq1 = tree.breathFirstStream()
			.map(MutableTreeNode::getValue)
			.collect(ISeq.toISeq());
		final ISeq<Integer> seq2 = tree2.breathFirstStream()
			.map(MutableTreeNode::getValue)
			.collect(ISeq.toISeq());

		System.out.println(seq1);
		System.out.println(seq2);

		Assert.assertEquals(seq1, seq2);
	}

	@Test
	public void swap() {
		final MutableTreeNode<Integer> tree =
			MutableTreeNodeTest.newTree(5, new Random(123));

		final SwapMutator<TreeGene<Integer>, Integer> mutator = new SwapMutator<>();
		final TreeChromosome<Integer> chromosome = TreeChromosome.of(tree);
		System.out.println(chromosome);

		for (int i = 0; i < 30; ++i) {
			final MSeq<TreeGene<Integer>> genes = chromosome.toSeq().copy();
			genes.shuffle();
			final TreeChromosome<Integer> sc = chromosome.newInstance(genes.toISeq());
			System.out.println(sc);
			System.out.flush();
			System.out.println(sc.toTree());
			System.out.flush();
		}
	}

}
