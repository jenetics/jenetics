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
package io.jenetics.example;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import io.jenetics.Optimize;

import io.jenetics.ext.moea.Vec;
import io.jenetics.ext.moea.VecFactory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MixedMOEAOptimization {

	private static final VecFactory<double[]> VEC_FACTORY =
		VecFactory.ofDoubleVec(
			Optimize.MAXIMUM,
			Optimize.MINIMUM,
			Optimize.MAXIMUM,
			Optimize.MINIMUM,
			Optimize.MAXIMUM
		);

	static Vec<double[]> fitness(final double[] point) {
		final double x = point[0];
		final double y = point[1];
		return VEC_FACTORY.newVec(new double[] {
			sin(x)*y,
			cos(y)*x,
			sin(x + y),
			cos(x + y)*x,
			x
		});
	}

}
