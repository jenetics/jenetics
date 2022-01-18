/*
 * Java Genetic Algorithm Library (@__identifier__@).
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
 */
package io.jenetics.example.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomAdapter;
import io.jenetics.util.RandomRegistry;

/**
 * Generates individuals for sudoku, Each row in a sudoku board is represented
 * through a chromosome (So an individual for a 9x9 sudoku board, contains 9
 * chromosomes.) Each chromosome is composed only with the non-fixed cells.
 *
 * @author José Alejandro Cornejo Acosta
 */
final public class Generator {

	private Generator() {
	}

	/**
	 * @param board the board of sudoku
	 * @return a random individual (genotype) for the given board.
	 * Fixed cells in the board are not included in the chromosomes.
	 */
	public static Genotype<IntegerGene> createIndividual(final Board board) {
		MSeq<Chromosome<IntegerGene>> sudokuChromosomes = MSeq.ofLength(Board.SIZE);
		for (int i = 0; i < Board.SIZE; i++) {
			sudokuChromosomes.set(i, createChromosome(board, i));
		}
		return Genotype.of(sudokuChromosomes);
	}

	private static IntegerChromosome createChromosome(final Board board, int iChromosome) {
		final var random = RandomAdapter.of(RandomRegistry.random());

		List<Integer> changes = new ArrayList<>();
		List<Integer> inputs = new ArrayList<>();

		for (int i = 0; i < Board.SIZE; i++) {
			changes.add(i + 1);
			if (board.get(iChromosome, i) != 0) {
				inputs.add(board.get(iChromosome, i));
			}
		}
		changes.removeAll(inputs);
		Collections.shuffle(changes, random);

		List<IntegerGene> genes = new ArrayList<>();
		for (int j = 0; j < Board.SIZE; j++) {
			if (board.get(iChromosome, j) == 0) {
				int value = changes.remove(changes.size() - 1);
				IntegerGene gene = IntegerGene.of(value, 1, Board.SIZE + 1);
				genes.add(gene);
			}
		}
		return IntegerChromosome.of(genes);
	}
}
