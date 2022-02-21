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

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import io.jenetics.Chromosome;
import io.jenetics.IntegerGene;
import io.jenetics.util.ISeq;

/**
 * Record that represents an individual of a Sudoku grid.
 *
 * @author José Alejandro Cornejo Acosta
 */
public record SudokuGrid(Board board, ISeq<Chromosome<IntegerGene>> chromosomes) {

	public SudokuGrid {
		requireNonNull(board);
		requireNonNull(chromosomes);
	}

	public ISeq<Chromosome<IntegerGene>> getChromosomes() {
		return chromosomes;
	}

	@Override
	public String toString() {

		String fiLaLine = "+---------+---------+---------+\n";

		StringBuilder tos = new StringBuilder(fiLaLine);
		// print board
		int[] skips = new int[Board.SIZE];
		for (int i = 0; i < Board.SIZE; i++) {
			for (int j = 0; j < Board.SIZE; j++) {

				int value = -1;
				if (board.get(i, j) == 0) {
					value = chromosomes.get(i).get(j - skips[i]).allele();
				} else {
					value = board.get(i, j);
					skips[i]++;
				}
				if (j == 0) {
					tos.append(String.format("| %d  ", value));
				} else if ((j + 1) % Board.SUB_BOARD_SIZE == 0) {
					tos.append(String.format("%d | ", value));
				} else {
					tos.append(String.format("%d  ", value));
				}
			}
			tos.append("\n");
			if ((i + 1) % Board.SUB_BOARD_SIZE == 0) {
				tos.append(fiLaLine);
			}
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
		final int[] set = new int[Board.SIZE];

		// skips array aims tracking of fixed cells in the board
		int[] skips = new int[Board.SIZE];

		for (int j = 0; j < Board.SIZE; j++) {
			Arrays.fill(set, 0);
			for (int i = 0; i < Board.SIZE; i++) {
				int value = -1;
				if (board.get(i, j) == 0) {
					value = chromosomes.get(i).get(j - skips[i]).allele();
				} else {
					value = board.get(i, j);
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
		final int[] set = new int[Board.SIZE];
		int[] skips = new int[Board.SIZE];
		for (int i = 0; i < Board.SIZE; i++) {
			Arrays.fill(set, 0);
			for (int j = 0; j < Board.SIZE; j++) {
				int value = -1;
				if (board.get(i, j) == 0) {
					value = chromosomes.get(i).get(j - skips[i]).allele();
				} else {
					value = board.get(i, j);
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
		int[] skips = new int[Board.SIZE];
		int penalties = 0;
		for (int i = 0; i < Board.SIZE; i += Board.SUB_BOARD_SIZE) {
			for (int j = 0; j < Board.SIZE; j += Board.SUB_BOARD_SIZE) {
				penalties += penaltiesInSubBoard(i, j, Board.SUB_BOARD_SIZE, skips);
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
				if (board.get(y, x) == 0) {
					value = chromosomes.get(y).get(x - skips[y]).allele();
				} else {
					value = board.get(y, x);
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
