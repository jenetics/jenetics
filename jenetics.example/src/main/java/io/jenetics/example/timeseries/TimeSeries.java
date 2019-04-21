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
package io.jenetics.example.timeseries;

import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TimeSeries {

	private static final Regression REGRESSION = Regression.of(
		Error.ABS_SUM,
		(a, b) -> 0,
		Sample.of(-1.0, -8.0000),
		Sample.of(-0.9, -6.2460),
		Sample.of(-0.8, -4.7680),
		Sample.of(-0.7, -3.5420),
		Sample.of(-0.6, -2.5440),
		Sample.of(-0.5, -1.7500),
		Sample.of(-0.4, -1.1360),
		Sample.of(-0.3, -0.6780),
		Sample.of(-0.2, -0.3520),
		Sample.of(-0.1, -0.1340),
		Sample.of(0.0, 0.0000),
		Sample.of(0.1, 0.0740),
		Sample.of(0.2, 0.1120),
		Sample.of(0.3, 0.1380),
		Sample.of(0.4, 0.1760),
		Sample.of(0.5, 0.2500),
		Sample.of(0.6, 0.3840),
		Sample.of(0.7, 0.6020),
		Sample.of(0.8, 0.9280),
		Sample.of(0.9, 1.3860),
		Sample.of(1.0, 2.0000)
	);

	// Definition of the allowed operations.
	private static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		MathOp.ADD,
		MathOp.SUB,
		MathOp.MUL
	);

	// Definition of the terminals.
	private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		EphemeralConst.of(() -> (double) RandomRegistry.getRandom().nextInt(10))
	);

	private static final Codec<ProgramGene<Double>, ProgramGene<Double>> CODEC =
		Codec.of(
			Genotype.of(ProgramChromosome.of(
				5,
				ch -> ch.getRoot().size() <= 50,
				OPERATIONS,
				TERMINALS
			)),
			Genotype::getGene
		);

	public static void main(final String[] args) {
		final Engine<ProgramGene<Double>, Double> engine = Engine
			.builder(REGRESSION::error, CODEC)
			.minimizing()
			.alterers(
				new SingleNodeCrossover<>(),
				new Mutator<>())
			.build();

		final ProgramGene<Double> program = engine.stream()
			.limit(3000)
			.collect(EvolutionResult.toBestGenotype())
			.getGene();

		final TreeNode<Op<Double>> tree = TreeNode.ofTree(program);
		System.out.println(tree);
		MathExpr.REWRITER.rewrite(tree);
		System.out.println(tree);
	}


}
