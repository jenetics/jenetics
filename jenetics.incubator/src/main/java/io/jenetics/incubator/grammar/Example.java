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
package io.jenetics.incubator.grammar;

import java.util.List;
import java.util.stream.Stream;

import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.incubator.grammar.bnf.Bnf;
import io.jenetics.util.IntRange;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Program;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.LossFunction;
import io.jenetics.prog.regression.Sample;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class Example {

	private static final Cfg CFG = Bnf.parse("""
		<expr> ::= (<expr><op><expr>) | <var>
		<op> ::= + | - | *
		<var> ::= x | 1 | 2 | 3 | 4
		"""
	);

	private static final Codec<Tree<? extends Op<Double>, ?>, IntegerGene> CODEC =
		Sentence.codec(CFG, IntRange.of(0, 256), IntRange.of(30), 500)
			.map(Sentence::toString)
			.map(e -> e.isEmpty() ? null : MathExpr.parseTree(e));

	// Lookup table for 4*x^3 - 3*x^2 + x
	private static final List<Sample<Double>> SAMPLES = List.of(
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

	private static final Double[] ARGS = SAMPLES.stream()
		.map(sample -> sample.argAt(0))
		.toArray(Double[]::new);

	private static final Double[] RESULTS = SAMPLES.stream()
		.map(Sample::result)
		.toArray(Double[]::new);

	private static double fitness(final Tree<? extends Op<Double>, ?> prog) {
		if (prog == null) {
			return 1_000_000;
		}

		final Error<Double> error = Error.of(LossFunction::mse);

		final Double[] calculated = Stream.of(ARGS)
			.map(args -> Program.eval(prog, args))
			.toArray(Double[]::new);

		return error.apply(prog, calculated, RESULTS);
	}

	public static void main(final String[] args) {
		final Engine<IntegerGene, Double> engine = Engine
			.builder(Example::fitness, CODEC)
			.minimizing()
			.build();

		final EvolutionResult<IntegerGene, Double> result = engine.stream()
			.limit(1000)
			.collect(EvolutionResult.toBestEvolutionResult());

		final Tree<? extends Op<Double>, ?> best =
			CODEC.decode(result.bestPhenotype().genotype());

		System.out.println("Generations: " + result.totalGenerations());
		System.out.println(
			"Codons: " + result.bestPhenotype().genotype().chromosome().stream()
				.map(IntegerGene::allele)
				.toList()
		);
		System.out.println(MathExpr.format(best) + " = " + fitness(best));
	}

}
