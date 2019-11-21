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

import io.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface VecFactory<T> {

	public Vec<T> create(final T array);

	public static VecFactory<int[]> ofIntVec(final Optimize... optimizes) {
		return new IntVecFactory(optimizes);
	}

	public static VecFactory<long[]> ofLongVec(final Optimize... optimizes) {
		return null;
	}

	public static VecFactory<double[]> ofDoubleVec(final Optimize... optimizes) {
		return null;
	}

	public static <T> VecFactory<T[]> ofObjectVec(final Optimize... optimizes) {
		return null;
	}

}
