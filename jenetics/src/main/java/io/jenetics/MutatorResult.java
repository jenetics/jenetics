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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Random;
import java.util.function.Function;

/**
 * Represents the result pair of one of the four {@code Mutator.mutate} calls.
 *
 * @see Mutator#mutate(Phenotype, long, double, Random)
 * @see Mutator#mutate(Genotype, double, Random)
 * @see Mutator#mutate(Chromosome, double, Random)
 * @see Mutator#mutate(Gene, Random)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 4.0
 */
public final class MutatorResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final T _result;
	private final int _mutations;

	private MutatorResult(final T result, final int mutations) {
		_result = requireNonNull(result);
		_mutations = mutations;
	}

	/**
	 * Maps this mutation result to type {@code B} using the given {@code mapper}.
	 *
	 * @param mapper the mutation result mapper
	 * @param <B> the new mutation result type
	 * @return a new mapped mutation result
	 * @throws NullPointerException if the given {@code mapper} is {@code null}
	 */
	public <B> MutatorResult<B> map(
		final Function<? super T, ? extends B> mapper
	) {
		requireNonNull(mapper);
		return of(mapper.apply(_result), _mutations);
	}

	/**
	 * Return the mutation result.
	 *
	 * @return the mutation result
	 */
	public T getResult() {
		return _result;
	}

	/**
	 * Return the number of mutations for this mutation result.
	 *
	 * @return the number of mutations
	 */
	public int getMutations() {
		return _mutations;
	}

	/**
	 * Create a new mutation result with the given values.
	 *
	 * @param result the mutation result
	 * @param mutations the number of mutations
	 * @param <T> the mutation result type
	 * @return a new mutation result
	 * @throws NullPointerException if the given mutation result is {@code null}
	 */
	public static <T> MutatorResult<T> of(final T result, final int mutations) {
		return new MutatorResult<>(result, mutations);
	}

	/**
	 * Create a new mutation result with the given result. The number of
	 * mutations is set to zero.
	 *
	 * @param result the mutation result
	 * @param <T> the mutation result type
	 * @return a new mutation result
	 * @throws NullPointerException if the given mutation result is {@code null}
	 */
	public static <T> MutatorResult<T> of(final T result) {
		return new MutatorResult<>(result, 0);
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 37*_result.hashCode() + 31;
		hash += 37*_mutations + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof MutatorResult<?> &&
			((MutatorResult)obj)._result.equals(_result) &&
			((MutatorResult)obj)._mutations == _mutations;
	}

	@Override
	public String toString() {
		return format("MutatorResult[%s, %s]", _result, _mutations);
	}

}
