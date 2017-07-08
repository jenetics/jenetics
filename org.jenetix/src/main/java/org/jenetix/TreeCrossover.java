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

import static java.lang.Math.min;

import java.util.Objects;
import java.util.Random;

import org.jenetics.Chromosome;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.Recombinator;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

import org.jenetix.util.FlatTree;
import org.jenetix.util.TreeNode;

/**
 * Abstract implementation of tree crossover recombinator.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
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
	 * Constructs an tree crossover with a given recombination probability.
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
		final Population<G, C> population,
		final int[] individuals,
		final long generation
	) {
		assert individuals.length == 2 : "Required order of 2";
		final Random random = RandomRegistry.getRandom();

		final Phenotype<G, C> pt1 = population.get(individuals[0]);
		final Phenotype<G, C> pt2 = population.get(individuals[1]);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();

		//Choosing the Chromosome index for crossover.
		final int chIndex = random.nextInt(min(gt1.length(), gt2.length()));

		final MSeq<Chromosome<G>> c1 = gt1.toSeq().copy();
		final MSeq<Chromosome<G>> c2 = gt2.toSeq().copy();

		crossover(c1, c2, chIndex);

		//Creating two new Phenotypes and exchanging it with the old.
		population.set(
			individuals[0],
			pt1.newInstance(Genotype.of(c1.toISeq()), generation)
		);
		population.set(
			individuals[1],
			pt2.newInstance(Genotype.of(c2.toISeq()), generation)
		);

		return getOrder();
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
		final TreeNode<A> tree1 = (TreeNode<A>)TreeNode.of(c1.get(index).getGene());
		@SuppressWarnings("unchecked")
		final TreeNode<A> tree2 = (TreeNode<A>)TreeNode.of(c2.get(index).getGene());

		crossover(tree1, tree2);

		tree1.breadthFirstStream().forEach(n -> Objects.requireNonNull(n.getValue()));
		tree2.breadthFirstStream().forEach(n -> Objects.requireNonNull(n.getValue()));

		final FlatTree<A> flat1 = FlatTree.of(tree1);
		final FlatTree<A> flat2 = FlatTree.of(tree2);

		@SuppressWarnings("unchecked")
		final TreeGene<A, ?> template = (TreeGene<A, ?>)c1.get(0).getGene();

		final ISeq<G> genes1 = flat1.map(tree -> gene(template, tree));
		final ISeq<G> genes2 = flat2.map(tree -> gene(template, tree));

		genes1.forEach(g -> Objects.requireNonNull(g.getValue()));
		genes2.forEach(g -> Objects.requireNonNull(g.getValue()));

		try {
			c1.set(index, c1.get(index).newInstance(genes1));
			c2.set(index, c2.get(index).newInstance(genes2));
		} catch (Exception e) {
			System.out.println(genes1);
			System.out.println(genes2);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private <A> G gene(final TreeGene<A, ?> template, final FlatTree<A> tree) {
		return (G)template.newInstance(
			tree.getValue(),
			tree.childOffset(),
			tree.childCount()
		);
	}

	/**
	 * Template method which performs the crossover. The arguments given are
	 * mutable non null trees.
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
