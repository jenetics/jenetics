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

import io.jenetics.Mutator;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.FitnessNullifier;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import io.jenetics.ext.SingleNodeCrossover;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;
import io.jenetics.prog.regression.Complexity;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;
import io.jenetics.prog.regression.SampleBuffer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TimeSeries {

	private final SampleBuffer<Double> _samples = new SampleBuffer<>(50);

	private final SampleProducer _producer = new SampleProducer(_samples);

	private final FitnessNullifier<ProgramGene<Double>, Double> _nullifier = new FitnessNullifier<>();


	// Definition of the allowed operations.
	private final ISeq<Op<Double>> _operations = ISeq.of(
		MathOp.ADD,
		MathOp.SUB,
		MathOp.MUL
	);

	// Definition of the terminals.
	private final ISeq<Op<Double>> _terminals = ISeq.of(
		Var.of("x", 0),
		EphemeralConst.of(() -> (double) RandomRegistry.random().nextInt(10))
	);

	private final Regression<Double> _regression = Regression.of(
		Regression.codecOf(
			_operations,
			_terminals,
			5
		),
		Error.of(LossFunction::mse, Complexity.ofNodeCount(50)),
		_samples
	);

	private final Engine<ProgramGene<Double>, Double> _engine = Engine
		.builder(_regression)
		.interceptor(_nullifier)
		.minimizing()
		.alterers(
			new SingleNodeCrossover<>(),
			new Mutator<>())
		.build();


	public static void main(final String[] args) {
		final TimeSeries ts = new TimeSeries();

		final Thread producer = new Thread(ts._producer);
		producer.start();

		final ProgramGene<Double> program = ts._engine.stream()
			.limit(3000)
			.collect(EvolutionResult.toBestGenotype())
			.gene();

		final TreeNode<Op<Double>> tree = TreeNode.ofTree(program);
		System.out.println(tree);
		MathExpr.REWRITER.rewrite(tree);
		System.out.println(MathExpr.parse(tree.toString()));

		producer.interrupt();
	}

}
