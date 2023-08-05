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

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import io.jenetics.internal.util.Requires;

/**
 * Represents the result pair of one of the four {@code Mutator.mutate} calls.
 *
 * @see Mutator#mutate(Phenotype, long, double, RandomGenerator)
 * @see Mutator#mutate(Genotype, double, RandomGenerator)
 * @see Mutator#mutate(Chromosome, double, RandomGenerator)
 * @see Mutator#mutate(Gene, RandomGenerator)
 *
 * @param <T> the mutation result type
 * @param result the mutation result
 * @param mutations the number of mutations applied while creating the mutation
 *        result
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.0
 * @since 4.0
 */
public record MutatorResult<T>(T result, int mutations)
	implements Serializable
{

	@Serial
	private static final long serialVersionUID = 2L;

	/**
	 * Create a new mutation result with the given values.
	 *
	 * @param result the mutation result
	 * @param mutations the number of mutations
	 * @throws IllegalArgumentException if the given {@code mutations} is
	 *         negative
	 * @throws NullPointerException if the given mutation result is {@code null}
	 */
	public MutatorResult {
		requireNonNull(result);
		Requires.nonNegative(mutations);
	}

	/**
	 * Maps this mutation result to type {@code B} using the given {@code mapper}.
	 *
	 * @param mapper the mutation result mapper
	 * @param <B> the new mutation result type
	 * @return a new mapped mutation result
	 * @throws NullPointerException if the given {@code mapper} is {@code null}
	 */
	<B> MutatorResult<B> map(final Function<? super T, ? extends B> mapper) {
		requireNonNull(mapper);
		return new MutatorResult<>(mapper.apply(result), mutations);
	}

}
