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
package org.jenetics.prog;

import static java.lang.String.format;

import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.prog.ops.EphemeralConst;
import org.jenetics.prog.ops.MathOp;
import org.jenetics.prog.ops.Op;
import org.jenetics.prog.ops.Program;
import org.jenetics.prog.ops.Var;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import org.jenetix.SingleNodeCrossover;
import org.jenetix.util.Tree;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Example {

	// Lookup table for 4*x^3 - 3*x^2 + x
	private static final double[][] SAMPLES = new double[][] {
		{-1.0, -8.0000},
		{-0.9, -6.2460},
		{-0.8, -4.7680},
		{-0.7, -3.5420},
		{-0.6, -2.5440},
		{-0.5, -1.7500},
		{-0.4, -1.1360},
		{-0.3, -0.6780},
		{-0.2, -0.3520},
		{-0.1, -0.1340},
		{0.0, 0.0000},
		{0.1, 0.0740},
		{0.2, 0.1120},
		{0.3, 0.1380},
		{0.4, 0.1760},
		{0.5, 0.2500},
		{0.6, 0.3840},
		{0.7, 0.6020},
		{0.8, 0.9280},
		{0.9, 1.3860},
		{1.0, 2.0000}
	};

	// The function we want to determine.
	private static double f(final double x) {
		return 4*x*x*x - 3*x*x + x;
	}

	// Definition of the allowed operations.
	private static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		MathOp.ADD,
		MathOp.SUB,
		MathOp.MUL
	);

	// Definition of the terminals.
	private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		EphemeralConst.of(() -> (double)RandomRegistry.getRandom().nextInt(10))
	);


	static double error(final Genotype<ProgramGene<Double>> genotype) {
		double error = 0;
		for (int i = 0; i < SAMPLES.length; ++i) {
			final double x = SAMPLES[i][0];
			final double result = genotype.getGene().eval(x);

			error += Math.abs(SAMPLES[i][1] - result) +
				genotype.getGene().size()*0.01;
		}

		return error;
	}

	public static void main(final String[] args) {
		final Genotype<ProgramGene<Double>> gt =
			Genotype.of(
				ProgramChromosome.of(
					5,
					ch -> ch.length() < 50,
					OPERATIONS,
					TERMINALS
				)
			);

		final Engine<ProgramGene<Double>, Double> engine = Engine
			.builder(Example::error, gt)
			.minimizing()
			.alterers(
				new SingleNodeCrossover<>(),
				new Mutator<>())
			.build();

		final Tree<? extends Op<Double>, ?> program = engine.stream()
			.limit(3000)
			.collect(EvolutionResult.toBestGenotype())
			.getGene();

		System.out.println(Tree.toString(program));

		for (int i = 0; i < SAMPLES.length; ++i) {
			final double x = SAMPLES[i][0];
			final double result = Program.eval(program, x);

			System.out.println(format(
				"%2.2f: %2.4f, %2.4f: %2.5f",
				x, f(x), result, Math.abs(f(x) - result)
			));
		}
	}

}
