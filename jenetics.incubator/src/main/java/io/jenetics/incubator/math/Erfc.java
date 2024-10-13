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
package io.jenetics.incubator.math;

/**
 * Calculates an approximation of {@code erfc} with a maximum relative error
 * less than 2<sup>-53</sup> (~ 1.1*10<sup>-16</sup>) in absolute value.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Error_function">
 *     Wikipedia: Error function</a>
 * @see <a href="https://dx.doi.org/10.2139/ssrn.4487559">
 *     Yaya D. Dia: Approximate Incomplete Integrals, Application to
 *     Complementary Error Function</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Erfc {
	private Erfc() {
	}

	/**
	 * Calculates an approximation of {@code erfc}.
	 *
	 * @param x the input value
	 * @return an approximation of {@code erfc}
	 */
	public static double apply(double x) {
		if (x < 0) {
			return 2 - apply(Math.abs(x));
		}

		final var xx = x*x;

		double r = 0.56418958354775629/(x + 2.06955023132914151);
		r *= ((xx + 2.71078540045147805*x + 5.80755613130301624)/
			  (xx + 3.47954057099518960*x + 12.06166887286239555));
		r *= ((xx + 3.47469513777439592*x + 12.07402036406381411)/
			  (xx + 3.72068443960225092*x + 8.44319781003968454));
		r *= ((xx + 4.00561509202259545*x + 9.30596659485887898)/
			  (xx + 3.90225704029924078*x + 6.36161630953880464));
		r *= ((xx + 5.16722705817812584*x + 9.12661617673673262)/
			  (xx + 4.03296893109262491*x + 5.13578530585681539));
		r *= ((xx + 5.95908795446633271*x + 9.19435612886969243)/
			  (xx + 4.11240942957450885*x + 4.48640329523408675));
		r *= Math.exp(-(xx));
		return r;
	}

}

