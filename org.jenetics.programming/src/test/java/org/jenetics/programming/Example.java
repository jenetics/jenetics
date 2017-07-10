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
package org.jenetics.programming;

import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.programming.ops.Op;
import org.jenetics.programming.ops.Ops;
import org.jenetics.programming.ops.Program;
import org.jenetics.programming.ops.Var;
import org.jenetics.util.ISeq;

import org.jenetix.SingleNodeCrossover;
import org.jenetix.TreeCrossover;
import org.jenetix.util.Tree;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Example {

	private static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		Ops.ADD,
		Ops.SUB,
		Ops.MUL,
		Ops.DIV,
		Ops.POW,
		//Ops.EXP,
		Ops.SIN
		//Ops.COS
	);

	private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		//Ops.fixed(Math.PI),
		Ops.fixed(1)
	);


	static double error(final ProgramGene<Double> program) {
		double error = 0;
		for (int i = 0; i < 20; ++i) {
			final double x = 2*Math.PI/20.0*i;
			final double result = program.eval(x);

			error += Math.abs(Math.sin(x) - result);
		}

		return error;
	}

	public static void main(final String[] args) {
		final Codec<ProgramGene<Double>, ProgramGene<Double>>
			codec = Codec.of(
				Genotype.of(ProgramChromosome.of(
					7,
					ch -> ch.length() < 200,
					OPERATIONS,
					TERMINALS
				)),
				Genotype::getGene
			);

		final Engine<ProgramGene<Double>, Double> engine = Engine
			.builder(Example::error, codec)
			.minimizing()
			.alterers(
				new SingleNodeCrossover<>(),
				new Mutator<>())
			.build();

		final Tree<? extends Op<Double>, ?> program = engine.stream()
			.limit(150)
			.collect(EvolutionResult.toBestGenotype())
			.getGene();

		System.out.println(Tree.toString(program));

		for (int i = 0; i < 20; ++i) {
			final double x = 2*Math.PI/20.0*i;
			final double result = Program.eval(program, x);

			System.out.println(i + ": " + Math.sin(x) + " -> " + result);
		}
	}

}
