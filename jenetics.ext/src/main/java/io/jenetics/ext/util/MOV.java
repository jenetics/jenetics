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
package io.jenetics.ext.util;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface MOV<T> {

	public T value();

	public int dimension();

	public default int compareTo(final int index, final T other) {
		return 0;
	}

	public int domination(final T other);

	public default boolean dominates(final T other) {
		return domination(other) > 0;
	}

	public default boolean dominated(final T other) {
		return domination(other) < 0;
	}

	default int rank(final Seq<T> population) {
		domination(population.get(0));
		return 0;
	}

	default int[] distances(final Seq<T> population) {
		return null;
	}

	public static <T> double[] crowdingDistances(
		final Seq<? extends T> front,
		final ComponentComparator<? super T> comparator
	) {

		return null;
	}

	public static MOV<double[]> of(final double[] value) {
		return new DoubleMOV(value);
	}

}

final class DoubleMOV implements MOV<double[]> {

	private final double[] _value;

	DoubleMOV(final double[] value) {
		_value = requireNonNull(value);
	}

	@Override
	public double[] value() {
		return _value;
	}

	@Override
	public int dimension() {
		return _value.length;
	}

	@Override
	public int domination(final double[] other) {
		return 0;
	}
}
