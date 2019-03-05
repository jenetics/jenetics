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
package io.jenetics.example;

import java.util.function.Function;

import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import io.jenetics.ext.SingleNodeCrossover;

import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;

/**
 * Example for doing genetic programming with more than one chromosome (program).
 * This allows the evolution of several programs in parallel, which can then be
 * combined in the fitness function.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MultiRootGP {

	// Definition of the allowed operations.
	static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		MathOp.ADD,
		MathOp.SUB,
		MathOp.MUL
	);

	// Definition of the terminals.
	static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		EphemeralConst.of(() -> (double) RandomRegistry.getRandom().nextInt(10))
	);

	static final Codec<ISeq<Function<Double[], Double>>, ProgramGene<Double>> CODEC =
		Codec.of(
			Genotype.of(
				// First 'program'
				ProgramChromosome.of(
					5,
					ch -> ch.getRoot().size() <= 50,
					OPERATIONS,
					TERMINALS
				),
				// Second 'program'
				ProgramChromosome.of(
					5,
					ch -> ch.getRoot().size() <= 50,
					OPERATIONS,
					TERMINALS
				)
			),
			gt -> gt.stream()
				.map(Chromosome::getGene)
				.collect(ISeq.toISeq())
		);

	static double fitness(final ISeq<Function<Double[], Double>> programs) {
		assert programs.size() == 2;
		// Combine the two programs for the fitness function.
		return 1;
	}

	public static void main(final String[] args) {
		final Engine<ProgramGene<Double>, Double> engine = Engine
			.builder(MultiRootGP::fitness, CODEC)
			.minimizing()
			.alterers(
				new SingleNodeCrossover<>(),
				new Mutator<>())
			.build();

		final Genotype<ProgramGene<Double>> gt = engine.stream()
			.limit(3000)
			.collect(EvolutionResult.toBestGenotype());

		final ISeq<Function<Double[], Double>> programs = CODEC.decode(gt);
	}

}
