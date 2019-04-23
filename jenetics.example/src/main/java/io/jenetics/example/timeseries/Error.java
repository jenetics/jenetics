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

import static java.lang.Math.min;
import static java.lang.Math.sqrt;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Error {

	public double apply(
		final Tree<Op<Double>, ?> program,
		final double[] calculated,
		final double[] expected
	);


	public static Error linear(final Similarity similarity, final Complexity complexity) {
		return (p, c, e) -> {
			return similarity.apply(c, e)*complexity.apply(p);
		};
	}

	/**
	 * e' = e*(1 + c/cm)
	 *
	 * @param e ssff
	 * @param c aaaf
	 * @param cm asdf
	 * @return asdf
	 */
	public static double linear(final double e, final double c, final double cm) {
		final double cc = min(c, cm);
		return e*(1.0 + cc/cm);
	}

	public static double euclidean(final double e, final double c, final double cm) {
		final double cc = min(c, cm);
		return 2*e - e*sqrt(1.0 - (cc*cc)/(cm*cm));
	}

}
