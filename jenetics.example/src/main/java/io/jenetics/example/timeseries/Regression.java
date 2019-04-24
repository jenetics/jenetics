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

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.pow;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * This class implements a <em>symbolic</em> regression problem.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Regression
	implements Problem<Tree<Op<Double>, ?>, ProgramGene<Double>, Double>
{

	private final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> _codec;
	private final Error _error;
	private final Supplier<List<Sample>> _samples;

	/**
	 * Create a new <em>symbolic</em> regression problem with the given data.
	 *
	 * @param codec the codec used for the for the problem
	 * @param error the error function
	 * @param samples the sample values used for finding a regression. Since
	 *        the samples are given via a <em>supplier</em> they can be changed
	 *        during the evolution process
	 */
	private Regression(
		final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec,
		final Error error,
		final Supplier<List<Sample>> samples
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
	 * @return the overall error value of the program
	 */
	public double error(final Tree<Op<Double>, ?> program) {
		final List<Sample> list = _samples.get();
		final Samples samples = list instanceof Samples
			? (Samples)list
			: new Samples(list.toArray(new Sample[0]));

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

	/**
	 * Create a new regression problem instance with the given parameters.
	 *
	 * @see #codecOf(ISeq, ISeq, int)
	 * @see #codecOf(ISeq, ISeq, int, Predicate)
	 *
	 * @param codec the problem codec to use
	 * @param error the error function
	 * @param samples the sample points used for regression analysis
	 * @return a new regression problem instance
	 */
	public static Regression of(
		final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec,
		final Error error,
		final Sample... samples
	) {
		final Samples s = new Samples(samples.clone());
		return new Regression(codec, error, () -> s);
	}

	/**
	 * Create a new regression problem instance with the given parameters.
	 *
	 * @see #codecOf(ISeq, ISeq, int)
	 * @see #codecOf(ISeq, ISeq, int, Predicate)
	 *
	 * @param codec the problem codec to use
	 * @param error the error function
	 * @param buffer the (mutable) sample points used for regression analysis
	 * @return a new regression problem instance
	 */
	public static Regression of(
		final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec,
		final Error error,
		final SampleBuffer buffer
	) {
		return new Regression(codec, error, buffer::snapshot);
	}


	/* *************************************************************************
	 * Codec factory methods.
	 * ************************************************************************/

	/**
	 * Create a new <em>codec</em>, usable for <em>symbolic regression</em>
	 * problems, with the given parameters.
	 *
	 * @param operations the operations used for the symbolic regression
	 * @param terminals the terminal operations of the program tree
	 * @param depth the maximal tree depth (height) of newly created program
	 *        trees
	 * @param validator the chromosome validator. A typical validator would
	 *        check the size of the tree and if the tree is too large, mark it
	 *        at <em>invalid</em>. The <em>validator</em> may be {@code null}.
	 * @return a new codec, usable for symbolic regression
	 * @throws IllegalArgumentException if the tree {@code depth} is not in the
	 *         valid range of {@code [0, 30)}
	 * @throws NullPointerException if the {@code operations} or {@code terminals}
	 *         are {@code null}
	 */
	public static Codec<Tree<Op<Double>, ?>, ProgramGene<Double>>
	codecOf(
		final ISeq<Op<Double>> operations,
		final ISeq<Op<Double>> terminals,
		final int depth,
		final Predicate<? super ProgramChromosome<Double>> validator
	) {
		if (depth >= 30 || depth < 0) {
			throw new IllegalArgumentException(format(
				"Tree depth out of range [0, 30): %d", depth
			));
		}

		return Codec.of(
			Genotype.of(ProgramChromosome.of(
				depth,
				validator,
				operations,
				terminals
			)),
			Genotype::getGene
		);
	}

	/**
	 * Create a new <em>codec</em>, usable for <em>symbolic regression</em>
	 * problems, with the given parameters.
	 *
	 * @param operations the operations used for the symbolic regression
	 * @param terminals the terminal operations of the program tree
	 * @param depth the maximal tree depth (height) of newly created program
	 *        trees
	 * @return a new codec, usable for symbolic regression
	 * @throws IllegalArgumentException if the tree {@code depth} is not in the
	 *         valid range of {@code [0, 30)}
	 * @throws NullPointerException if the {@code operations} or {@code terminals}
	 *         are {@code null}
	 */
	public static Codec<Tree<Op<Double>, ?>, ProgramGene<Double>>
	codecOf(
		final ISeq<Op<Double>> operations,
		final ISeq<Op<Double>> terminals,
		final int depth
	) {
		// Average arity of tree nodes.
		final double k = operations.stream()
			.collect(Collectors.averagingDouble(Op::arity));

		// The average node count between treeDepth and treeDepth + 1.
		// 2^(k + 1) - 1 + 2^(k + 2) - 1)/2 == 3*2^k - 1
		final int max = (int)(3*pow(k, depth) - 1);

		return codecOf(
			operations,
			terminals,
			depth,
			ch -> ch.getRoot().size() <= max
		);
	}

}
