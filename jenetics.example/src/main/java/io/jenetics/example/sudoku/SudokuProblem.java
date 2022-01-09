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
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;

import java.util.ArrayList;
import java.util.function.Function;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static java.lang.System.out;

/**
 * Implementation  of a simple Genetic Algorithms for solving sudokus.
 * Sudokus are puzzles where missing numbers in [1, 9] must be filled into a 9x9 grid.
 * There must be no repeating numbers neither in rows, nor columns, nor sub-grids of 3x3.
 *
 * @author José Alejandro Cornejo Acosta
 */
public class SudokuProblem implements Problem<ISeq<Chromosome<IntegerGene>>, IntegerGene, Integer> {

	private final int[][] board;

	public SudokuProblem(int[][] board) {
		this.board = board;
	}

	/**
	 * Fitness function penalizes issues rows, columns, and issues in sub-boards (3x3).
	 * "Issues" mean repeated numbers.
	 * It is one of the simplest fitness function for sudoku.
	 */
	@Override
	public Function<ISeq<Chromosome<IntegerGene>>, Integer> fitness() {
		return board -> {
			int penaltiesInRows = penaltiesInRows(board);
			int penaltiesInCols = penaltiesInCols(board);
			int penaltiesInGrid = penaltiesInGrid(board);
			return penaltiesInRows + penaltiesInCols + penaltiesInGrid;
		};
	}

	private int penaltiesInCols(ISeq<Chromosome<IntegerGene>> ind) {
		int penalties = 0;
		int[] set = null;

		// skips array aims tracking of fixed cells in the board
		int[] skips = new int[board.length];

		for (int j = 0; j < board.length; j++) {
			set = new int[board.length];
			for (int i = 0; i < board.length; i++) {
				int value = -1;
				if (board[i][j] == 0) {
					value = ind.get(i).get(j - skips[i]).allele();
				} else {
					value = board[i][j];
					skips[i]++;

				}
				if (set[value - 1] >= 1) {
					penalties++;
				}
				set[value - 1]++;
			}
		}
		return penalties;
	}

	private int penaltiesInRows(ISeq<Chromosome<IntegerGene>> ind) {
		int penalties = 0;
		int[] set = null;
		int[] skips = new int[board.length];
		for (int i = 0; i < board.length; i++) {
			set = new int[board.length];
			for (int j = 0; j < board.length; j++) {
				int value = -1;
				if (board[i][j] == 0) {
					value = ind.get(i).get(j - skips[i]).allele();
				} else {
					value = board[i][j];
					skips[i]++;
				}
				if (set[value - 1] >= 1) {
					penalties++;
				}
				set[value - 1]++;
			}
		}
		return penalties;
	}

	private int penaltiesInGrid(ISeq<Chromosome<IntegerGene>> ind) {
		int[] skips = new int[board.length];
		int penalties = 0;
		for (int i = 0; i < board.length; i += 3) {
			for (int j = 0; j < board.length; j += 3) {
				penalties += penaltiesInSubBoard(ind, i, j, 3, skips);
			}
		}
		return penalties;
	}

	public int penaltiesInSubBoard(ISeq<Chromosome<IntegerGene>> ind, int i, int j, int scope, int[] skips) {

		int penalties = 0;
		int[] set = new int[ind.length()];
		for (int y = i; y < i + scope; y++) {
			for (int x = j; x < j + scope; x++) {

				int value = -1;
				if (board[y][x] == 0) {
					value = ind.get(y).get(x - skips[y]).allele();
				} else {
					value = board[y][x];
					skips[y]++;
				}

				if (set[value - 1] >= 1) {
					penalties++;
				}
				set[value - 1]++;
			}
		}
		return penalties;
	}

	@Override
	public Codec<ISeq<Chromosome<IntegerGene>>, IntegerGene> codec() {
		return Codec.of(() -> Genotype.of(Generator.createIndividual(board)), ISeq::of);
	}

	public static void main(String[] args) {
		final int[][] board = SudokuUtil.BOARD2;

		// Crossovers like SinglePoint can be used
		var engine = Engine.builder(new SudokuProblem(board)).optimize(Optimize.MINIMUM)
			.alterers(
				new SwapMutator<>(0.05),
				new RowCrossover(0.6)
//				new SinglePointCrossover<>(0.3)
			)
			.selector(new TournamentSelector<>(2))
			.populationSize(300).build();

		final var bestPhenotypes = new ArrayList<Phenotype<IntegerGene, Integer>>();
		final var best = engine.stream()
			.limit(1000)
			.peek(r -> bestPhenotypes.add(r.bestPhenotype()))
			.collect(toBestPhenotype());
		out.println("Issues: " + best.fitness());

		// print board
		int[] skips = new int[board.length];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				int value = -1;
				if (board[i][j] == 0) {
					value = best.genotype().get(i).get(j - skips[i]).allele();
				} else {
					value = board[i][j];
					skips[i]++;
				}
				out.printf("%d, ", value);
			}
			out.println();
		}
	}
}
