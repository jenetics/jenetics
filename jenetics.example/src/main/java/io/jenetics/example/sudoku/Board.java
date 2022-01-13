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

/**
 * Immutable class to represent an initial board of Sudoku.
 * Provides examples of boards for 9x9 sudoku.
 * Zeros represent empty cells that must be filled.
 *
 * @author José Alejandro Cornejo Acosta
 */
public final class Board {
	private final int[][] cells;

	public Board(final int[][] cells) {
		this.cells = clone(cells);
	}

	private static int[][] clone(final int[][] cells) {
		final int[][] result = new int[cells.length][];
		for (int i = 0; i < cells.length; i++) {
			result[i] = cells[i].clone();
		}
		return result;
	}

	public int get(final int i, final int j) {
		return cells[i][j];
	}

	// constants
	public static final int SIZE = 9;
	public static final int SUB_BOARD_SIZE = 3;

	public static final int[][] BOARD1 = {
		{0, 0, 4, 0, 0, 0, 0, 9, 0},
		{7, 0, 0, 0, 6, 0, 0, 0, 5},
		{0, 9, 0, 5, 4, 1, 8, 7, 2},
		{0, 0, 0, 1, 8, 7, 0, 4, 0},
		{2, 4, 3, 6, 0, 5, 1, 8, 7},
		{0, 8, 0, 3, 2, 4, 0, 0, 0},
		{9, 2, 1, 8, 7, 6, 0, 5, 0},
		{6, 0, 0, 0, 1, 0, 0, 0, 8},
		{0, 3, 0, 0, 0, 0, 7, 0, 0}};

	public static final int[][] BOARD2 = {
		{0, 9, 0, 0, 0, 0, 4, 6, 8},
		{0, 6, 8, 4, 0, 0, 0, 0, 3},
		{0, 5, 0, 8, 0, 0, 7, 2, 0},
		{5, 2, 0, 0, 0, 0, 8, 0, 0},
		{8, 3, 0, 9, 0, 7, 6, 5, 4},
		{0, 4, 0, 0, 6, 8, 0, 0, 0},
		{2, 8, 0, 6, 4, 0, 3, 0, 0},
		{0, 0, 0, 0, 7, 0, 0, 8, 0},
		{0, 7, 0, 0, 0, 3, 2, 0, 0}};

	public static final int[][] BOARD3 = {
		{0, 7, 9, 2, 0, 5, 0, 3, 0},
		{0, 8, 0, 6, 7, 0, 5, 2, 0},
		{0, 5, 0, 0, 0, 0, 7, 8, 6},
		{7, 0, 1, 5, 3, 9, 0, 4, 0},
		{0, 3, 2, 0, 0, 0, 0, 9, 0},
		{8, 9, 5, 4, 6, 0, 3, 0, 0},
		{9, 0, 7, 1, 2, 0, 8, 5, 0},
		{0, 0, 0, 7, 5, 0, 9, 6, 2},
		{0, 0, 0, 3, 9, 8, 4, 7, 0}};

	public static final int[][] BOARD4 = {
		{0, 0, 8, 0, 3, 0, 0, 0, 0},
		{0, 3, 0, 6, 7, 5, 0, 0, 0},
		{5, 0, 0, 8, 0, 0, 0, 7, 0},
		{8, 4, 9, 0, 0, 3, 6, 5, 2},
		{3, 5, 0, 9, 8, 0, 0, 0, 7},
		{0, 6, 0, 0, 0, 4, 0, 8, 3},
		{0, 8, 0, 0, 5, 0, 7, 2, 9},
		{7, 0, 0, 0, 0, 8, 4, 0, 6},
		{0, 2, 4, 3, 0, 0, 5, 1, 0}};
}
