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

import static java.lang.System.out;
import static io.jenetics.engine.EvolutionResult.toBestPhenotype;

import java.util.ArrayList;
import java.util.function.Function;

import io.jenetics.IntegerGene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.SwapMutator;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;

/**
 * Implementation  of a simple Genetic Algorithms for solving sudokus. Sudokus
 * are puzzles where missing numbers in [1, 9] must be filled into a 9x9 grid.
 * There must be no repeating numbers neither in rows, nor columns,
 * nor sub-grids of 3x3.
 *
 * @author José Alejandro Cornejo Acosta
 */
public class SudokuProblem implements Problem<SudokuGrid, IntegerGene, Integer> {

	private final Board board;

	public SudokuProblem(Board board) {
		this.board = board;
	}

	/**
	 * Fitness function penalizes issues rows, columns, and issues in sub-boards (3x3).
	 * "Issues" mean repeated numbers.
	 * It is one of the simplest fitness function for sudoku.
	 */
	@Override
	public Function<SudokuGrid, Integer> fitness() {
		return SudokuGrid::penalties;
	}

	@Override
	public Codec<SudokuGrid, IntegerGene> codec() {
		// Encoder: Given a board, Generator.createIndividual(board) creates new
		// genotypes by filling empty cells with random numbers.
		//
		// Decoder: given a board and sequence of chromosomes, a SudokuGrid
		// object es created.
		return Codec.of(
			() -> Generator.createIndividual(board),
			chromosomes -> new SudokuGrid(board, ISeq.of(chromosomes))
		);
	}

	public static void main(String[] args) {
		final var board = Board.BOARD1;
		final var problem = new SudokuProblem(board);

		// Crossovers like SinglePoint can be used
		var engine = Engine.builder(problem)
			.optimize(Optimize.MINIMUM)
			.alterers(
				new SwapMutator<>(0.05),
				new RowCrossover(0.6)
//				new SinglePointCrossover<>(0.3)
			)
			.selector(new TournamentSelector<>(2))
			.populationSize(300)
			.build();

		final var bestPhenotypes = new ArrayList<Phenotype<IntegerGene, Integer>>();
		final var best = engine.stream()
			.limit(1000)
			.peek(r -> bestPhenotypes.add(r.bestPhenotype()))
			.collect(toBestPhenotype());
		out.println("Issues: " + best.fitness());
		final SudokuGrid grid = problem.decode(best.genotype());
		System.out.println(grid);
	}
}
