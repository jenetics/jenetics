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

import static java.lang.Math.pow;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.ProgramChromosome;
import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Program;

/**
 * This class implements a <em>symbolic</em> regression problem. The example
 * below shows a typical usage of the {@code Regression} class.
 *
 * <pre>{@code
 * public class SymbolicRegression {
 *     private static final ISeq<Op<Double>> OPERATIONS =
 *         ISeq.of(MathOp.ADD, MathOp.SUB, MathOp.MUL);
 *
 *     private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
 *         Var.of("x", 0),
 *         EphemeralConst.of(() -> (double)RandomRegistry.getRandom().nextInt(10))
 *     );
 *
 *     private static final Regression<Double> REGRESSION = Regression.of(
 *         Regression.codecOf(OPERATIONS, TERMINALS, 5),
 *         Error.of(LossFunction::mse),
 *         Sample.ofDouble(-1.0, -8.0000),
 *         // ...
 *         Sample.ofDouble(0.9, 1.3860),
 *         Sample.ofDouble(1.0, 2.0000)
 *     );
 *
 *     public static void main(final String[] args) {
 *         final Engine<ProgramGene<Double>, Double> engine = Engine
 *             .builder(REGRESSION)
 *             .minimizing()
 *             .alterers(
 *                 new SingleNodeCrossover<>(0.1),
 *                 new Mutator<>())
 *             .build();
 *
 *         final EvolutionResult<ProgramGene<Double>, Double> result = engine.stream()
 *             .limit(Limits.byFitnessThreshold(0.01))
 *             .collect(EvolutionResult.toBestEvolutionResult());
 *
 *         final ProgramGene<Double> program = result.getBestPhenotype()
 *             .getGenotype()
 *             .getGene();
 *
 *         final TreeNode<Op<Double>> tree = program.toTreeNode();
 *         MathExpr.rewrite(tree); // Simplify result program.
 *         System.out.println("Generations: " + result.getTotalGenerations());
 *         System.out.println("Function:    " + new MathExpr(tree));
 *         System.out.println("Error:       " + REGRESSION.error(tree));
 *     }
 * }
 * }</pre>
 *
 * @param <T> the operation type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public final class Regression<T>
	implements Problem<Tree<Op<T>, ?>, ProgramGene<T>, Double>
{

	private final Codec<Tree<Op<T>, ?>, ProgramGene<T>> _codec;
	private final Error<T> _error;
	private final Samples<T> _samples;


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
		final Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec,
		final Error<T> error,
		final Samples<T> samples
	) {
		_codec = requireNonNull(codec);
		_error = requireNonNull(error);
		_samples = requireNonNull(samples);
	}

	@Override
	public Function<Tree<Op<T>, ?>, Double> fitness() {
		return this::error;
	}

	@Override
	public Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec() {
		return _codec;
	}

	/**
	 * Return an immutable list of the symbolic regression's points.
	 *
	 * @return the sample points
	 */
	public List<Sample<T>> samples() {
		return _samples;
	}

	/**
	 * Calculates the actual error for the given {@code program}.
	 *
	 * @param program the program to calculate the error value for
	 * @return the overall error value of the program
	 */
	public double error(final Tree<Op<T>, ?> program) {
		@SuppressWarnings("unchecked")
		final T[] calculated = Stream.of(_samples.arguments())
			.map(args -> Program.eval(program, args))
			.toArray(size -> (T[])Array.newInstance(_samples.type(), size));

		return _error.apply(program, calculated, _samples.results());
	}

	/* *************************************************************************
	 * Factory methods.
	 * ************************************************************************/

	/**
	 * Create a new regression problem instance with the given parameters.
	 *
	 * @see #codecOf(ISeq, ISeq, int)
	 * @see #codecOf(ISeq, ISeq, int, Predicate)
	 *
	 * @param <T> the operation type
	 * @param codec the problem codec to use
	 * @param error the error function
	 * @param samples the sample points used for regression analysis
	 * @return a new regression problem instance
	 * @throws IllegalArgumentException if the given {@code samples} is empty
	 * @throws NullPointerException if on of the arguments is {@code null}
	 */
	public static <T> Regression<T> of(
		final Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec,
		final Error<T> error,
		final Iterable<Sample<T>> samples
	) {
		if (!samples.iterator().hasNext()) {
			throw new IllegalArgumentException("Sample list must not be empty.");
		}

		final List<Sample<T>> s = new ArrayList<>();
		samples.forEach(s::add);

		return new Regression<>(codec, error, new Samples<>(s));
	}

	/**
	 * Create a new regression problem instance with the given parameters.
	 *
	 * @see #codecOf(ISeq, ISeq, int)
	 * @see #codecOf(ISeq, ISeq, int, Predicate)
	 *
	 * @param <T> the operation type
	 * @param codec the problem codec to use
	 * @param error the error function
	 * @param samples the sample points used for regression analysis
	 * @return a new regression problem instance
	 * @throws IllegalArgumentException if the given {@code samples} is empty
	 * @throws NullPointerException if on of the arguments is {@code null}
	 */
	@SafeVarargs
	public static <T> Regression<T> of(
		final Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec,
		final Error<T> error,
		final Sample<T>... samples
	) {
		return of(codec, error, Arrays.asList(samples));
	}


	/* *************************************************************************
	 * Codec factory methods.
	 * ************************************************************************/

	/**
	 * Create a new <em>codec</em>, usable for <em>symbolic regression</em>
	 * problems, with the given parameters.
	 *
	 * @param <T> the operation type
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
	public static <T> Codec<Tree<Op<T>, ?>, ProgramGene<T>>
	codecOf(
		final ISeq<Op<T>> operations,
		final ISeq<Op<T>> terminals,
		final int depth,
		final Predicate<? super ProgramChromosome<T>> validator
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
	 * @param <T> the operation type
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
	public static <T> Codec<Tree<Op<T>, ?>, ProgramGene<T>>
	codecOf(
		final ISeq<Op<T>> operations,
		final ISeq<Op<T>> terminals,
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
