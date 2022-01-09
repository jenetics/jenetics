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

import io.jenetics.Chromosome;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.util.MSeq;

import java.util.*;

/**
 * Generates individuals for sudoku
 * Each row in a sudoku board is represented through a chromosome
 * (So an individual for a 9x9 sudoku board, contains 9 chromosomes)
 * Each chromosome is composed only with the non-fixed cells
 *
 * @author José Alejandro Cornejo Acosta
 */
public class Generator {
	/**
	 * @param board the board of sudoku
	 * @return a random individual for the given board
	 */
	public static MSeq<Chromosome<IntegerGene>> createIndividual(int[][] board) {
		int size = board.length;
		MSeq<Chromosome<IntegerGene>> sudoku = MSeq.ofLength(size);
		for (int i = 0; i < size; i++) {
			sudoku.set(i, createChromosome(board, i));
		}
		return sudoku;
	}

	private static IntegerChromosome createChromosome(int[][] board, int iChromosome) {
		LinkedList<Integer> changes = new LinkedList<>();
		LinkedList<Integer> inputs = new LinkedList<>();

		for (int i = 0; i < board.length; i++) {
			changes.add(i + 1);
			if (board[iChromosome][i] != 0) {
				inputs.add(board[iChromosome][i]);
			}
		}
		changes.removeAll(inputs);
		Collections.shuffle(changes);

		List<IntegerGene> genes = new ArrayList<>();
		for (int j = 0; j < board.length; j++) {
			if (board[iChromosome][j] == 0) {
				int value = changes.removeLast();
				IntegerGene gene = IntegerGene.of(value, 1, SudokuUtil.SIZE + 1);
				genes.add(gene);
			}
		}
		return IntegerChromosome.of(genes);
	}
}
