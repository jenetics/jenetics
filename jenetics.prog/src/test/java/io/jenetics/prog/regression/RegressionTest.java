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
package io.jenetics.prog.regression;

import io.jenetics.engine.Engine;
import io.jenetics.engine.FitnessNullifier;
import io.jenetics.util.Streams;
import org.testng.annotations.Test;

import io.jenetics.engine.Codec;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.EphemeralConst;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RegressionTest {

	private static final ISeq<Op<Double>> OPS =
		ISeq.of(MathOp.ADD, MathOp.SUB, MathOp.MUL);

	private static final ISeq<Op<Double>> TMS = ISeq.of(
		Var.of("x", 0),
		EphemeralConst.of(() -> (double) RandomRegistry.random().nextInt(10))
	);

	@Test
	public void error() {
		final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec =
			Regression.codecOf(OPS, TMS, 5, t -> t.gene().size() < 30);

		final Regression<Double> regression = Regression.of(
			codec,
			Error.of(LossFunction::mse),
			Sample.ofDouble(-1.0, -8.0000),
			Sample.ofDouble(-0.9, -6.2460),
			Sample.ofDouble(-0.8, -4.7680),
			Sample.ofDouble(-0.7, -3.5420),
			Sample.ofDouble(-0.6, -2.5440),
			Sample.ofDouble(-0.5, -1.7500),
			Sample.ofDouble(-0.4, -1.1360),
			Sample.ofDouble(-0.3, -0.6780),
			Sample.ofDouble(-0.2, -0.3520),
			Sample.ofDouble(-0.1, -0.1340),
			Sample.ofDouble(0.0, 0.0000),
			Sample.ofDouble(0.1, 0.0740),
			Sample.ofDouble(0.2, 0.1120),
			Sample.ofDouble(0.3, 0.1380),
			Sample.ofDouble(0.4, 0.1760),
			Sample.ofDouble(0.5, 0.2500),
			Sample.ofDouble(0.6, 0.3840),
			Sample.ofDouble(0.7, 0.6020),
			Sample.ofDouble(0.8, 0.9280),
			Sample.ofDouble(0.9, 1.3860),
			Sample.ofDouble(1.0, 2.0000)
		);

		final Tree<Op<Double>, ?> tree = codec.encoding().newInstance().gene();
		regression.error(tree);
	}

	//@Test
	public void dynamicSamples() {
		final var scheduler = Executors.newScheduledThreadPool(1);
		final var nullifier = new FitnessNullifier<ProgramGene<Double>, Double>();
		final var sampling = new SampleBuffer<Double>(100);
		scheduler.scheduleWithFixedDelay(
			() -> {
				// Adding a new sample point every second to the ring buffer.
				sampling.add(nextSamplePoint());
				// Force re-evaluation of populations fitness values.
				nullifier.nullifyFitness();
			},
			1, 1, TimeUnit.SECONDS
		);

		final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec =
			Regression.codecOf(OPS, TMS, 5, t -> t.gene().size() < 30);

		final Regression<Double> regression = Regression.of(
			codec,
			Error.of(LossFunction::mse),
			sampling
		);

		final Engine<ProgramGene<Double>, Double> engine = Engine
			.builder(regression)
			.interceptor(nullifier)
			.build();

		engine.stream()
			.flatMap(Streams.toIntervalMax(Duration.ofSeconds(30)))
			.map(program -> program.bestPhenotype()
				.genotype().gene()
				.toParenthesesString())
			// Printing the best program found so far every 30 seconds.
			.forEach(System.out::println);
	}

	private static Sample<Double> nextSamplePoint() {
		final Random random = new Random();
		return Sample.ofDouble(random.nextDouble(), random.nextDouble());
	}

}
