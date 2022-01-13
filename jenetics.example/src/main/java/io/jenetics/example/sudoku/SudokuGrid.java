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
import io.jenetics.IntegerGene;
import io.jenetics.util.ISeq;

import java.util.Arrays;

/**
 * Immutable class that represents an individual of a Sudoku grid.
 *
 * @author José Alejandro Cornejo Acosta
 */
public class SudokuGrid {

	private final int[][] board;

	private final ISeq<Chromosome<IntegerGene>> chromosomes;

	public SudokuGrid(int[][] board, ISeq<Chromosome<IntegerGene>> chromosomes) {
		this.board = board;
		this.chromosomes = chromosomes;
	}

	public ISeq<Chromosome<IntegerGene>> getChromosomes() {
		return chromosomes;
	}

	@Override
	public String toString() {
		StringBuilder tos = new StringBuilder();

		// print board
		int[] skips = new int[board.length];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				int value = -1;
				if (board[i][j] == 0) {
					value = chromosomes.get(i).get(j - skips[i]).allele();
				} else {
					value = board[i][j];
					skips[i]++;
				}
				tos.append(String.format("%d, ", value));
			}
			tos.append("\n");
		}
		return tos.toString();
	}

	/**
	 * Penalties are issues in rows, columns, and issues in sub-boards (3x3).
	 * "Issues" mean repeated numbers.
	 */
	public int penalties() {
		return penaltiesInRows() + penaltiesInCols() + penaltiesInGrid();
	}

	private int penaltiesInCols() {
		int penalties = 0;
		final int[] set = new int[board.length];;

		// skips array aims tracking of fixed cells in the board
		int[] skips = new int[board.length];

		for (int j = 0; j < board.length; j++) {
			Arrays.fill(set, 0);
			for (int i = 0; i < board.length; i++) {
				int value = -1;
				if (board[i][j] == 0) {
					value = chromosomes.get(i).get(j - skips[i]).allele();
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

	private int penaltiesInRows() {
		int penalties = 0;
		final int[] set = new int[board.length];
		int[] skips = new int[board.length];
		for (int i = 0; i < board.length; i++) {
			Arrays.fill(set, 0);
			for (int j = 0; j < board.length; j++) {
				int value = -1;
				if (board[i][j] == 0) {
					value = chromosomes.get(i).get(j - skips[i]).allele();
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

	private int penaltiesInGrid() {
		int[] skips = new int[board.length];
		int penalties = 0;
		for (int i = 0; i < board.length; i += 3) {
			for (int j = 0; j < board.length; j += 3) {
				penalties += penaltiesInSubBoard(i, j, 3, skips);
			}
		}
		return penalties;
	}

	private int penaltiesInSubBoard(int i, int j, int scope, int[] skips) {

		int penalties = 0;
		int[] set = new int[chromosomes.length()];
		for (int y = i; y < i + scope; y++) {
			for (int x = j; x < j + scope; x++) {

				int value = -1;
				if (board[y][x] == 0) {
					value = chromosomes.get(y).get(x - skips[y]).allele();
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
}
