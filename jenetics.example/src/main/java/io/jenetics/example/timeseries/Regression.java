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
import io.jenetics.engine.Codec;
import io.jenetics.engine.Problem;
import io.jenetics.ext.util.Tree;
import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Program;
import io.jenetics.util.ISeq;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.Math.pow;
import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Regression
	implements Problem<Tree<Op<Double>, ?>, ProgramGene<Double>, Double>
{

	private final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> _codec;
	private final Error _error;
	private final Supplier<Samples> _samples;

	public Regression(
		final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec,
		final Error error,
		final Supplier<Samples> samples
	) {
		_codec = requireNonNull(codec);
		_error = requireNonNull(error);
		_samples = requireNonNull(samples);
	}

	@Override
	public Function<Tree<Op<Double>, ?>, Double> fitness() {
		return this::error;
	}

	@Override
	public Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec() {
		return _codec;
	}

	/**
	 * Calculates the actual error for the given {@code program}.
	 *
	 * @param program the program to calculate the error value for
	 * @return the overall error value of the program, including its complexity
	 *         penalty
	 */
	public double error(final Tree<Op<Double>, ?> program) {
		final Samples samples = _samples.get();
		assert samples != null;

		final double[] calculated = Stream.of(samples.arguments())
			.mapToDouble(args -> eval(program, args))
			.toArray();

		return _error.apply(program, calculated, samples.results());
	}

	private static double
	eval(final Tree<Op<Double>, ?> program, final double[] args) {
		final Double[] value = new Double[args.length];
		for (int i = 0; i < args.length; ++i) {
			value[i] = args[i];
		}

		return Program.eval(program, value);
	}


	public static Regression of(
		final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec,
		final Error error,
		final Sample... samples
	) {
		final Samples s = new Samples(samples.clone());
		return new Regression(codec, error, () -> s);
	}

//	public static Regression of(
//		final LossFunction error,
//		final Complexity complexity,
//		final SampleBuffer buffer
//	) {
//		return new Regression(null, error, complexity, buffer::snapshot);
//	}
//
//	public static Regression of(
//		final ISeq<Op<Double>> operations,
//		final ISeq<Op<Double>> terminals,
//		final int depth,
//		final int maxNodeCount,
//		final LossFunction error,
//		final Sample... samples
//	) {
//		return null;
//	}
//
//	public static Regression of(
//		final Codec<ProgramGene<Double>, ProgramGene<Double>> codec,
//		final LossFunction error,
//		final Sample... samples
//	) {
//		return null;
//	}
//

	public static Codec<Tree<Op<Double>, ?>, ProgramGene<Double>>
	codecOf(
		final ISeq<Op<Double>> operations,
		final ISeq<Op<Double>> terminals,
		final int treeDepth
	) {
		final int min = 2*treeDepth + 1;
		final int max = (int)pow(2, treeDepth + 1) - 1;

		return Codec.of(
			Genotype.of(ProgramChromosome.of(
				treeDepth,
				ch -> ch.getRoot().size() <= 50,
				operations,
				terminals
			)),
			Genotype::getGene
		);
	}

}
