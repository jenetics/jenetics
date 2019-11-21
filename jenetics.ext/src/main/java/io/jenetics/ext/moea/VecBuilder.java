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

import java.util.Comparator;

import io.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class VecBuilder {

	private final Optimize[] _optimizes;


	private ElementComparator<int[]> _comparator;
	private ElementDistance<int[]> _distance;
	private Comparator<int[]> _dominance;

	private VecBuilder(final Optimize[] optimizes) {
		_optimizes = optimizes.clone();

		_comparator = this::cmp;
	}

	private int cmp(final int[] u, final int[] v, final int i) {
		return _optimizes[i] == Optimize.MAXIMUM
			? Integer.compare(u[i], v[i])
			: Integer.compare(v[i], u[i]);
	}

	public Vec<int[]> newVec(final int... array) {
		return new IntVec(array, _comparator, _distance, _dominance);
	}


	public static VecBuilder builder(final Optimize... optimizes) {
		return new VecBuilder(optimizes);
	}

}
