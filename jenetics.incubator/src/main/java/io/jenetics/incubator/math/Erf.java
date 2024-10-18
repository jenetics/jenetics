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
 * This class contains helper methods related the error function.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Error_function">
 *     Wikipedia: Error function</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Erf {
	private Erf() {
	}

	/**
	 * Calculates an approximation of {@code erf}: erf(x) = 2/&radic;&pi;
	 * <sub>0</sub>&int;<sup>x</sup> e<sup>-t<sup>2</sup></sup>dt,
	 * with a maximum relative error less than 2<sup>-53</sup>
	 * (~ 1.1*10<sup>-16</sup>) in absolute value.
	 *
	 * @see <a href="https://dx.doi.org/10.2139/ssrn.4487559">
	 *     Yaya D. Dia: Approximate Incomplete Integrals, Application to
	 *     Complementary Error Function</a>
	 * @see #erfc(double)
	 *
	 * @param x the input value
	 * @return an approximation of {@code erf}
	 */
	public static double erf(final double x) {
		if (Math.abs(x) > 40) {
			return x > 0 ? 1 : -1;
		}
		return 1 - erfc(x);
	}

	/**
	 * Calculates an approximation of the complementary error function
	 * {@code erfc}: erfc(x) = 2/&radic;&pi; <sub>x</sub>&int;<sup>&infin;
	 * </sup> e<sup>-t<sup>2</sup></sup>dt = 1 - {@link #erf(double) erf(x)},
	 * with a maximum relative error less than 2<sup>-53</sup>
	 * (~ 1.1*10<sup>-16</sup>) in absolute value.
	 *
	 * @see <a href="https://dx.doi.org/10.2139/ssrn.4487559">
	 *     Yaya D. Dia: Approximate Incomplete Integrals, Application to
	 *     Complementary Error Function</a>
	 * @see #erf(double)
	 *
	 * @param x the input value
	 * @return an approximation of {@code erfc}
	 */
	public static double erfc(double x) {
		if (Math.abs(x) > 40) {
			return x > 0 ? 0 : 2;
		}
		if (x < 0) {
			return 2 - erfc(Math.abs(x));
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
		r *= Math.exp(-xx);
		return r;
	}

	/**
	 * Returns the inverse error function, {@code erfinv}. This implementation
	 * is described in the paper:
	 * <a href="https://people.maths.ox.ac.uk/~gilesm/codes/erfinv/gems.pdf">
	 * Approximating the erfinv function</a>, by Mike Giles, Oxford-Man
	 * Institute of Quantitative Finance, which was published in GPU Computing
	 * Gems, volume 2, 2010. The source code is available
	 * <a href="https://people.maths.ox.ac.uk/~gilesm/codes/erfinv/erfinv_DP_1.cu">
	 *     here</a>.
	 *
	 * @see <a href="https://people.maths.ox.ac.uk/~gilesm/codes/erfinv/gems.pdf">
	 * 	  Mike Giles: Approximating the erfinv function</a>
	 * @see #erfcinv(double)
	 *
	 * @param x the input value
	 * @return an approximation of {@code erfinv}
	 */
	public static double erfinv(final double x) {
		if (Double.isNaN(x)) {
			return Double.NaN;
		}
		if (Double.isInfinite(x)) {
			return Double.POSITIVE_INFINITY;
		}

		double w = -Math.log((1.0 - x)*(1.0 + x));
		double p;

		if (w < 6.250000) {
			w = w - 3.125000;
			p = -3.6444120640178196996e-21;
			p = -1.6850591381820165890e-19 + p*w;
			p =  1.2858480715256400167e-18 + p*w;
			p =  1.1157877678025180960e-17 + p*w;
			p = -1.3331716628546209060e-16 + p*w;
			p =  2.0972767875968561637e-17 + p*w;
			p =  6.6376381343583238325e-15 + p*w;
			p = -4.0545662729752068639e-14 + p*w;
			p = -8.1519341976054721522e-14 + p*w;
			p =  2.6335093153082322977e-12 + p*w;
			p = -1.2975133253453532498e-11 + p*w;
			p = -5.4154120542946279317e-11 + p*w;
			p =  1.0512122733215322850e-09 + p*w;
			p = -4.1126339803469836976e-09 + p*w;
			p = -2.9070369957882005086e-08 + p*w;
			p =  4.2347877827932403518e-07 + p*w;
			p = -1.3654692000834678645e-06 + p*w;
			p = -1.3882523362786468719e-05 + p*w;
			p =  0.0001867342080340571352  + p*w;
			p = -0.00074070253416626697512 + p*w;
			p = -0.0060336708714301490533  + p*w;
			p =  0.24015818242558961693    + p*w;
			p =  1.6536545626831027356     + p*w;
		} else if (w < 16.000000) {
			w = Math.sqrt(w) - 3.250000;
			p =  2.2137376921775787049e-09;
			p =  9.0756561938885390979e-08  + p*w;
			p = -2.7517406297064545428e-07  + p*w;
			p =  1.8239629214389227755e-08  + p*w;
			p =  1.5027403968909827627e-06  + p*w;
			p = -4.0138675269815459690e-06  + p*w;
			p =  2.9234449089955446044e-06  + p*w;
			p =  1.2475304481671778723e-05  + p*w;
			p = -4.7318229009055733981e-05  + p*w;
			p =  6.8284851459573175448e-05  + p*w;
			p =  2.40311103870978939990e-05 + p*w;
			p = -0.0003550375203628474796   + p*w;
			p =  0.00095328937973738049703  + p*w;
			p = -0.0016882755560235047313   + p*w;
			p =  0.0024914420961078508066   + p*w;
			p =  -0.0037512085075692412107  + p*w;
			p =   0.005370914553590063617   + p*w;
			p =   1.0052589676941592334     + p*w;
			p =   3.0838856104922207635     + p*w;
		} else {
			w = Math.sqrt(w) - 5.000000;
			p = -2.7109920616438573243e-11;
			p = -2.5556418169965252055e-10 + p*w;
			p =  1.5076572693500548083e-09 + p*w;
			p = -3.7894654401267369937e-09 + p*w;
			p =  7.6157012080783393804e-09 + p*w;
			p = -1.4960026627149240478e-08 + p*w;
			p =  2.9147953450901080826e-08 + p*w;
			p = -6.7711997758452339498e-08 + p*w;
			p =  2.2900482228026654717e-07 + p*w;
			p = -9.9298272942317002539e-07 + p*w;
			p =  4.5260625972231537039e-06 + p*w;
			p = -1.9681778105531670567e-05 + p*w;
			p =  7.5995277030017761139e-05 + p*w;
			p = -0.00021503011930044477347 + p*w;
			p = -0.00013871931833623122026 + p*w;
			p =  1.0103004648645343977     + p*w;
			p =  4.8499064014085844221     + p*w;
		}

		return p*x;
	}

	/**
	 * Returns the complementary inverse error function, {@code erfinv}.
	 *
	 * @see <a href="https://people.maths.ox.ac.uk/~gilesm/codes/erfinv/gems.pdf">
	 * 	  Mike Giles: Approximating the erfinv function</a>
	 * @see #erfinv(double)
	 *
	 * @param x the input value
	 * @return an approximation of {@code erfinvc}
	 */
	public static double erfcinv(final double x) {
		return erfinv(1.0 - x);
	}

}

