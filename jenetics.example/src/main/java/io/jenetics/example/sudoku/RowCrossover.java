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
package io.jenetics.example.sudoku;

import io.jenetics.*;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

/**
 * Custom crossover for the sudoku.
 * Given two individuals, it swaps their chromosomes in the same random position.
 * This crossover assumes that an individual is represented by n chromosomes for a sudoku board
 * of nxn.
 * This crossover is not too disruptive.
 *
 * @author José Alejandro Cornejo Acosta
 */
public class RowCrossover extends Recombinator<IntegerGene, Integer> {
	protected RowCrossover(double probability) {
		super(probability, 2);
	}

	@Override
	protected int recombine(MSeq<Phenotype<IntegerGene, Integer>> population, int[] individuals, long generation) {

		assert individuals.length == 2 : "Required order of 2";

		var random = RandomRegistry.random();

		// getting parents
		var pt1 = population.get(individuals[0]);
		var pt2 = population.get(individuals[1]);
		var gt1 = pt1.genotype();
		var gt2 = pt2.genotype();

		// getting chromosome to be crossed
		var chIndex = random.nextInt(Math.min(gt1.length(), gt2.length()));
		var individual1 = ISeq.of(gt1).copy();
		var individual2 = ISeq.of(gt2).copy();

		this.crossover(individual1, individual2, chIndex);

		population.set(individuals[0], Phenotype.of(Genotype.of(individual1), generation));
		population.set(individuals[1], Phenotype.of(Genotype.of(individual2), generation));
		return this.order();
	}

	private void crossover(MSeq<Chromosome<IntegerGene>> individual1, MSeq<Chromosome<IntegerGene>> individual2, int chIndex) {
		var ch = individual1.get(chIndex);
		individual1.set(chIndex, individual2.get(chIndex));
		individual2.set(chIndex, ch);
	}
}
