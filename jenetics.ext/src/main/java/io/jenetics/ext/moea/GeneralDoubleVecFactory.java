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
package io.jenetics.ext.moea;

import static java.lang.String.format;
import static io.jenetics.ext.moea.Vecs.requireVecLength;
import static io.jenetics.ext.moea.Vecs.toFlags;

import java.util.Comparator;
import java.util.List;

import io.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 5.2
 */
final class GeneralDoubleVecFactory implements VecFactory<double[]> {
	private final boolean[] _maximisations;

	private final ElementComparator<double[]> _comparator = this::cmp;
	private final ElementDistance<double[]> _distance = this::dst;
	private final Comparator<double[]> _dominance = this::dom;

	GeneralDoubleVecFactory(final List<Optimize> optimizes) {
		Vecs.checkVecLength(optimizes.size());
		_maximisations = toFlags(optimizes);
	}

	private int cmp(final double[] u, final double[] v, final int i) {
		return _maximisations[i]
			? Double.compare(u[i], v[i])
			: Double.compare(v[i], u[i]);
	}

	private double dst(final double[] u, final double[] v, final int i) {
		return _maximisations[i]
			? u[i] - v[i]
			: v[i] - u[i];
	}

	private int dom(final double[] u, final double[] v) {
		return Pareto.dominance(u, v, _maximisations.length, this::cmp);
	}

	@Override
	public Vec<double[]> newVec(final double[] array) {
		requireVecLength(_maximisations.length, array.length);
		return new GeneralDoubleVec(array, _comparator, _distance, _dominance);
	}

	@Override
	public String toString() {
		return format("VecFactory<double[%d]>", _maximisations.length);
	}

}
