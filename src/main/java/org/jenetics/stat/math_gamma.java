/*
 * Copyright © 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and
 * its documentation for any purpose is hereby granted without fee, provided
 * that the above copyright notice appear in all copies and that both that
 * copyright notice and this permission notice appear in supporting
 * documentation. CERN makes no representations about the suitability of
 * this software for any purpose. It is provided "as is" without expressed
 * or implied warranty.
 */
package org.jenetics.stat;

import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.sin;

@SuppressWarnings("javadoc")
abstract class math_gamma {

	static final double MACHEP = 1.11022302462515654042E-16;
	static final double MAXLOG = 7.09782712893383996732E2;
	static final double MINLOG = -7.451332191019412076235E2;
	static final double MAXGAM = 171.624376956302725;
	static final double SQTPI = 2.50662827463100050242E0;
	static final double SQRTH = 7.07106781186547524401E-1;
	static final double LOGPI = 1.14472988584940017414;
	static final double big = 4.503599627370496E15;
	static final double biginv = 2.22044604925031308085E-16;

	math_gamma() {
	}

	
	public static double lngamma(final double z) {
		if (z <= 0.0) {
			throw new ArithmeticException();
		}

		double x;
		double tmp;
		double y;
		double ser;
		
		y = z;
		x = z;
		tmp = x + 5.24218750000000000;
		tmp = (x + 0.5)*Math.log(tmp) - tmp;
		ser = 0.999999999999997092;
		for (int i = 0; i < cof.length; ++i) {
			ser += cof[i]/(++y);
		}
		return tmp+log(2.5066282746310005*ser/x);
	}

	private static final double[] cof = {
		57.1562356658629235,
		-59.5979603554754912,
		14.1360979747417471,
		-0.491913816097620199,
		0.339946499848118887e-4,
		0.465236289270485756e-4,
		-0.983744753048795646e-4,
		0.158088703224912494e-3,
		-0.210264441724104883e-3,
		0.217439618115212643e-3,
		-0.164318106536763890e-3,
		0.844182239838527433e-4,
		0-.261908384015814087e-4,
		0.368991826595316234e-5
	};
	
	public static double lnΓ(final double z) {
		return lngamma(z);
	}
	
	
	/**
	 * Returns the <a
	 * href="http://mathworld.wolfram.com/IncompleteGammaFunction.html">
	 * Incomplete Gamma function</a>.
	 *
	 * @see #gamma(double, double)
	 *
	 * @param a
	 *            the parameter of the gamma distribution.
	 * @param x
	 *            the integration end point.
	 */
	public static double Γ(final double a, final double x) {
		double ans = 0;
		double ax = 0;
		double c = 0;
		double r = 0;

		if (x <= 0 || a <= 0) {
			return 0.0;
		}

		if (x > 1.0 && x > a) {
			return 1.0 - incompleteGammaComplement(a, x);
		}

		/* Compute x**a * exp(-x) / gamma(a) */
		ax = a * log(x) - x - math.logGamma(a);
		if (ax < -MAXLOG) {
			return (0.0);
		}

		ax = exp(ax);

		/* power series */
		r = a;
		c = 1.0;
		ans = 1.0;

		do {
			r += 1.0;
			c *= x / r;
			ans += c;
		} while (c / ans > MACHEP);

		return (ans * ax / a);
	}

	/**
	 * Returns the <a
	 * href="http://mathworld.wolfram.com/IncompleteGammaFunction.html">
	 * Incomplete Gamma function</a>.
	 *
	 * @see #Γ(double, double)
	 *
	 * @param a
	 *            the parameter of the gamma distribution.
	 * @param x
	 *            the integration end point.
	 */
	public static double gamma(final double a, final double x) {
		return Γ(a, x);
	}

	/**
	 * Returns the Complemented Incomplete Gamma function; formerly named
	 * <tt>igamc</tt>.
	 *
	 * @param a
	 *            the parameter of the gamma distribution.
	 * @param x
	 *            the integration start point.
	 */
	static public double incompleteGammaComplement(double a, double x) {
		double ans, ax, c, yc, r, t, y, z;
		double pk, pkm1, pkm2, qk, qkm1, qkm2;

		if (x <= 0 || a <= 0) {
			return 1.0;
		}

		if (x < 1.0 || x < a) {
			return 1.0 - Γ(a, x);
		}

		ax = a * Math.log(x) - x - math.logGamma(a);
		if (ax < -MAXLOG) {
			return 0.0;
		}

		ax = Math.exp(ax);

		/* continued fraction */
		y = 1.0 - a;
		z = x + y + 1.0;
		c = 0.0;
		pkm2 = 1.0;
		qkm2 = x;
		pkm1 = x + 1.0;
		qkm1 = z * x;
		ans = pkm1 / qkm1;

		do {
			c += 1.0;
			y += 1.0;
			z += 2.0;
			yc = y * c;
			pk = pkm1 * z - pkm2 * yc;
			qk = qkm1 * z - qkm2 * yc;
			if (qk != 0) {
				r = pk / qk;
				t = Math.abs((ans - r) / r);
				ans = r;
			} else {
				t = 1.0;
			}

			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;
			if (Math.abs(pk) > big) {
				pkm2 *= biginv;
				pkm1 *= biginv;
				qkm2 *= biginv;
				qkm1 *= biginv;
			}
		} while (t > MACHEP);

		return ans * ax;
	}

	/**
	 * Returns the natural logarithm of the gamma function; formerly named
	 * <tt>lgamma</tt>.
	 */
	static double logGamma(double x) {
		final double A[] = {
			8.11614167470508450300E-4,
			-5.95061904284301438324E-4,
			7.93650340457716943945E-4,
			-2.77777777730099687205E-3,
			8.33333333333331927722E-2
		};
		final double B[] = {
			-1.37825152569120859100E3,
			-3.88016315134637840924E4,
			-3.31612992738871184744E5,
			-1.16237097492762307383E6,
			-1.72173700820839662146E6,
			-8.53555664245765465627E5
		};
		final double C[] = {
			/* 1.00000000000000000000E0, */
			-3.51815701436523470549E2,
			-1.70642106651881159223E4,
			-2.20528590553854454839E5,
			-1.13933444367982507207E6,
			-2.53252307177582951285E6,
			-2.01889141433532773231E6
		};
		
		double p, q, w, z;
		
		double result = Double.NaN;
		if (x < -34.0) {
			q = -x;
			w = logGamma(q);
			p = floor(q);
			if (p == q) {
				throw new ArithmeticException("lgam: Overflow");
			}
			
			z = q - p;
			if (z > 0.5) {
				p += 1.0;
				z = p - q;
			}
			z = q * sin(Math.PI * z);
			if (z == 0.0) {
				throw new ArithmeticException("lgamma: Overflow");
			}
			
			result = LOGPI - log(z) - w;
		} else if (x < 13.0) {
			z = 1.0;
			while (x >= 3.0) {
				x -= 1.0;
				z *= x;
			}
			while (x < 2.0) {
				if (x == 0.0) {
					throw new ArithmeticException("lgamma: Overflow");
				}
				z /= x;
				x += 1.0;
			}
			if (z < 0.0) {
				z = -z;
			}
			if (x == 2.0) {
				return Math.log(z);
			}
			x -= 2.0;
			p = x * polevl(x, B, 5) / p1evl(x, C, 6);
			result = (Math.log(z) + p);
		} else if (x > 2.556348E305) {
			throw new ArithmeticException("lgamma: Overflow");
		} else {
			q = (x - 0.5)*log(x) - x + 0.91893853320467274178;
			if (x > 1.0e8) {
				return (q);
			} else {
				p = 1.0/(x*x);
				if (x >= 1000.0) {
					q += ((7.9365079365079365079365e-4 * p - 2.7777777777777777777778e-3)
							* p + 0.0833333333333333333333) / x;
				} else {
					q += polevl(p, A, 4)/x;
				}
			}
			
			result = q;
		}
		
		return result;
	}
	
	private static double p1evl( double x, double coef[], int N ) {
		double ans;

		ans = x + coef[0];

		for(int i=1; i<N; i++) { ans = ans*x+coef[i]; }

		return ans;
	}

	private static double polevl( double x, double coef[], int N ) {
		double ans;
		ans = coef[0];

		for(int i=1; i<=N; i++) ans = ans*x+coef[i];

		return ans;
	}

}