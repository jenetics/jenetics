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

import io.jenetics.Chromosome;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.internal.math.probability;
import io.jenetics.util.ISeq;

import io.jenetics.ext.util.FlatTree;
import io.jenetics.ext.util.FlatTreeNode;
import io.jenetics.ext.util.TreeNode;

/**
 * Abstract class for mutating tree chromosomes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public abstract class TreeMutator<
	A,
	G extends TreeGene<A, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{
	public TreeMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	public TreeMutator(final double probability) {
		super(probability);
	}


	/**
	 * Mutates the given chromosome.
	 *
	 * @param chromosome the chromosome to mutate
	 * @param p the mutation probability for the underlying genetic objects
	 * @param random the random engine used for the genotype mutation
	 * @return the mutation result
	 */
	@Override
	protected MutatorResult<Chromosome<G>> mutate(
		final Chromosome<G> chromosome,
		final double p,
		final Random random
	) {
		final int P = probability.toInt(p);
		return random.nextInt() < P
			? mutate(chromosome)
			: MutatorResult.of(chromosome);
	}

	private MutatorResult<Chromosome<G>> mutate(final Chromosome<G> chromosome) {
		final var tree = TreeNode.ofTree(chromosome.getGene());
		mutate(tree);

		final var flat = FlatTreeNode.of(tree);
		final var genes = flat.map(t -> gene(chromosome.getGene(), t));
		return MutatorResult.of(chromosome.newInstance(genes), 1);
	}

	private G gene(
		final G template,
		final FlatTree<? extends A, ?> tree
	) {
		return template.newInstance(
			tree.getValue(),
			tree.childOffset(),
			tree.childCount()
		);
	}

	/**
	 * This method does the actual mutating, in place.
	 *
	 * @param tree the mutable tree to mutate
	 */
	protected abstract void mutate(final TreeNode<A> tree);

}
