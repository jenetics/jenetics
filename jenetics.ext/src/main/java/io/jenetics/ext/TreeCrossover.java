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

import static java.lang.Math.min;

import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.Recombinator;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

import io.jenetics.ext.util.FlatTree;
import io.jenetics.ext.util.FlatTreeNode;
import io.jenetics.ext.util.TreeNode;

/**
 * Abstract implementation of tree base crossover recombinator. This class
 * simplifies the implementation of tree base crossover implementation, by doing
 * the transformation of the flattened tree genes to actual trees and vice versa.
 * Only the {@link #crossover(TreeNode, TreeNode)} method must be implemented.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public abstract class TreeCrossover<
	G extends TreeGene<?, G>,
	C extends Comparable<? super C>
>
	extends Recombinator<G, C>
{

	/**
	 * Constructs a tree crossover with a given recombination probability.
	 *
	 * @param probability the recombination probability
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}
	 */
	protected TreeCrossover(final double probability) {
		super(probability, 2);
	}

	@Override
	protected int recombine(
		final MSeq<Phenotype<G, C>> population,
		final int[] individuals,
		final long generation
	) {
		assert individuals.length == 2 : "Required order of 2";
		final var random = RandomRegistry.random();

		final Phenotype<G, C> pt1 = population.get(individuals[0]);
		final Phenotype<G, C> pt2 = population.get(individuals[1]);
		final Genotype<G> gt1 = pt1.genotype();
		final Genotype<G> gt2 = pt2.genotype();

		//Choosing the Chromosome index for crossover.
		final int chIndex = random.nextInt(min(gt1.length(), gt2.length()));

		final MSeq<Chromosome<G>> c1 = MSeq.of(gt1);
		final MSeq<Chromosome<G>> c2 = MSeq.of(gt2);

		crossover(c1, c2, chIndex);

		//Creating two new Phenotypes and exchanging it with the old.
		population.set(
			individuals[0],
			Phenotype.of(Genotype.of(c1.toISeq()), generation)
		);
		population.set(
			individuals[1],
			Phenotype.of(Genotype.of(c2.toISeq()), generation)
		);

		return order();
	}

	// Since the allele type "A" is not part of the type signature, we have to
	// do some unchecked casts to make it "visible" again. The implementor of
	// the abstract "crossover" method usually don't have to do additional casts.
	private <A> void crossover(
		final MSeq<Chromosome<G>> c1,
		final MSeq<Chromosome<G>> c2,
		final int index
	) {
		@SuppressWarnings("unchecked")
		final TreeNode<A> tree1 = (TreeNode<A>)TreeNode.ofTree(c1.get(index).gene());
		@SuppressWarnings("unchecked")
		final TreeNode<A> tree2 = (TreeNode<A>)TreeNode.ofTree(c2.get(index).gene());

		crossover(tree1, tree2);

		final var flat1 = FlatTreeNode.ofTree(tree1);
		final var flat2 = FlatTreeNode.ofTree(tree2);

		@SuppressWarnings("unchecked")
		final var template = (TreeGene<A, ?>)c1.get(0).gene();

		final var genes1 = flat1.map(tree -> gene(template, tree));
		final var genes2 = flat2.map(tree -> gene(template, tree));

		c1.set(index, c1.get(index).newInstance(genes1));
		c2.set(index, c2.get(index).newInstance(genes2));
	}

	@SuppressWarnings("unchecked")
	private <A> G gene(
		final TreeGene<A, ?> template,
		final FlatTree<? extends A, ?> tree
	) {
		return (G)template.newInstance(
			tree.value(),
			tree.childOffset(),
			tree.childCount()
		);
	}

	/**
	 * Template method which performs the crossover. The arguments given are
	 * mutable non-null trees.
	 *
	 * @param <A> the <em>existential</em> allele type
	 * @param that the first (chromosome) tree
	 * @param other he second (chromosome) tree
	 * @return the number of altered genes
	 */
	protected abstract <A> int crossover(
		final TreeNode<A> that,
		final TreeNode<A> other
	);

}
