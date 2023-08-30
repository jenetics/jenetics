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
package io.jenetics;

import static java.lang.Math.min;
import static io.jenetics.internal.math.Randoms.indexes;

import io.jenetics.internal.util.Requires;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

/**
 * The uniform crossover uses swaps single genes between two chromosomes, instead
 * of whole ranges as in single- and multipoint crossover.
 * <pre> {@code
 * +---+---+---+---+---+---+---+
 * | 1 | 2 | 3 | 4 | 6 | 7 | 8 |
 * +-+-+---+-+-+-+-+---+-+-+---+
 *   |       |   |       |        swapping
 * +-+-+---+-+-+-+-+---+-+-+---+
 * | a | b | c | d | e | f | g |
 * +---+---+---+---+---+---+---+
 * }</pre>
 * The probability that two genes are swapped is controlled by the
 * <i>swap-probability</i> ({@link #swapProbability()}), whereas the
 * probability that a given individual is selected for crossover is defined by
 * the <i>crossover-probability</i> ({@link #probability()}).
 *
 * @see <a href="https://en.wikipedia.org/wiki/Crossover_(genetic_algorithm)#Uniform_crossover_and_half_uniform_crossover">
 *     Wikipedia: Uniform crossover</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 3.7
 */
public class UniformCrossover<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Crossover<G, C>
{

	private final double _swapProbability;

	/**
	 * Create a new universal crossover instance.
	 *
	 * @param crossoverProbability the recombination probability as defined in
	 *        {@link Crossover#Crossover(double)}. This is the probability that
	 *        a given individual is selected for crossover.
	 * @param swapProbability the probability for swapping a given gene of
	 *         a chromosome
	 * @throws IllegalArgumentException if the probabilities are not in the
	 *         valid range of {@code [0, 1]}
	 */
	public UniformCrossover(
		final double crossoverProbability,
		final double swapProbability
	) {
		super(crossoverProbability);
		_swapProbability = Requires.probability(swapProbability);
	}

	/**
	 * Create a new universal crossover instance. The {@code swapProbability} is
	 * set to {@link Alterer#DEFAULT_ALTER_PROBABILITY}.
	 *
	 * @param crossoverProbability the recombination probability as defined in
	 *        {@link Crossover#Crossover(double)}. This is the probability that
	 *        a given individual is selected for crossover.
	 * @throws IllegalArgumentException if the probabilities are not in the
	 *         valid range of {@code [0, 1]}
	 */
	public UniformCrossover(final double crossoverProbability) {
		this(crossoverProbability, DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Create a new universal crossover instance. The probabilities are set to
	 * {@link Alterer#DEFAULT_ALTER_PROBABILITY}.
	 */
	public UniformCrossover() {
		this(DEFAULT_ALTER_PROBABILITY, DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Return the probability for swapping genes of a chromosome.
	 *
	 * @return the probability for swapping genes of a chromosome
	 */
	public double swapProbability() {
		return _swapProbability;
	}

	@Override
	protected int crossover(final MSeq<G> that, final MSeq<G> other) {
		final int length = min(that.length(), other.length());
		return (int)indexes(RandomRegistry.random(), length, _swapProbability)
			.peek(i -> that.swap(i, other))
			.count();
	}

}
