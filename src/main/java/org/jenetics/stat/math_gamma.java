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

class math_gamma {

	static final double MACHEP = 1.11022302462515654042E-16;
	static final double MAXLOG = 7.09782712893383996732E2;
	static final double MINLOG = -7.451332191019412076235E2;
	static final double MAXGAM = 171.624376956302725;
	static final double SQTPI = 2.50662827463100050242E0;
	static final double SQRTH = 7.07106781186547524401E-1;
	static final double LOGPI = 1.14472988584940017414;
	static final double big = 4.503599627370496e15;
	static final double biginv = 2.22044604925031308085e-16;

	math_gamma() {
	}

	/**
	 * Continued fraction expansion #1 for incomplete beta integral; formerly
	 * named <tt>incbcf</tt>.
	 */
	static double incompleteBetaFraction1(double a, double b, double x)
		throws ArithmeticException 
	{
		double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
		double k1, k2, k3, k4, k5, k6, k7, k8;
		double r, t, ans, thresh;
		int n;

		k1 = a;
		k2 = a + b;
		k3 = a;
		k4 = a + 1.0;
		k5 = 1.0;
		k6 = b - 1.0;
		k7 = k4;
		k8 = a + 2.0;

		pkm2 = 0.0;
		qkm2 = 1.0;
		pkm1 = 1.0;
		qkm1 = 1.0;
		ans = 1.0;
		r = 1.0;
		n = 0;
		thresh = 3.0 * MACHEP;
		do {
			xk = -(x * k1 * k2) / (k3 * k4);
			pk = pkm1 + pkm2 * xk;
			qk = qkm1 + qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			xk = (x * k5 * k6) / (k7 * k8);
			pk = pkm1 + pkm2 * xk;
			qk = qkm1 + qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			if (qk != 0) {
				r = pk / qk;
			}
			if (r != 0) {
				t = Math.abs((ans - r) / r);
				ans = r;
			} else {
				t = 1.0;
			}

			if (t < thresh) {
				return ans;
			}

			k1 += 1.0;
			k2 += 1.0;
			k3 += 2.0;
			k4 += 2.0;
			k5 += 1.0;
			k6 -= 1.0;
			k7 += 2.0;
			k8 += 2.0;

			if ((Math.abs(qk) + Math.abs(pk)) > big) {
				pkm2 *= biginv;
				pkm1 *= biginv;
				qkm2 *= biginv;
				qkm1 *= biginv;
			}
			if ((Math.abs(qk) < biginv) || (Math.abs(pk) < biginv)) {
				pkm2 *= big;
				pkm1 *= big;
				qkm2 *= big;
				qkm1 *= big;
			}
		} while (++n < 300);

		return ans;
	}

	/**
	 * Continued fraction expansion #2 for incomplete beta integral; formerly
	 * named <tt>incbd</tt>.
	 */
	static double incompleteBetaFraction2(double a, double b, double x)
			throws ArithmeticException {
		double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
		double k1, k2, k3, k4, k5, k6, k7, k8;
		double r, t, ans, z, thresh;
		int n;

		k1 = a;
		k2 = b - 1.0;
		k3 = a;
		k4 = a + 1.0;
		k5 = 1.0;
		k6 = a + b;
		k7 = a + 1.0;
		k8 = a + 2.0;

		pkm2 = 0.0;
		qkm2 = 1.0;
		pkm1 = 1.0;
		qkm1 = 1.0;
		z = x / (1.0 - x);
		ans = 1.0;
		r = 1.0;
		n = 0;
		thresh = 3.0 * MACHEP;
		do {
			xk = -(z * k1 * k2) / (k3 * k4);
			pk = pkm1 + pkm2 * xk;
			qk = qkm1 + qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			xk = (z * k5 * k6) / (k7 * k8);
			pk = pkm1 + pkm2 * xk;
			qk = qkm1 + qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			if (qk != 0) {
				r = pk / qk;
			}
			if (r != 0) {
				t = Math.abs((ans - r) / r);
				ans = r;
			} else {
				t = 1.0;
			}

			if (t < thresh) {
				return ans;
			}

			k1 += 1.0;
			k2 -= 1.0;
			k3 += 2.0;
			k4 += 2.0;
			k5 += 1.0;
			k6 += 1.0;
			k7 += 2.0;
			k8 += 2.0;

			if ((Math.abs(qk) + Math.abs(pk)) > big) {
				pkm2 *= biginv;
				pkm1 *= biginv;
				qkm2 *= biginv;
				qkm1 *= biginv;
			}
			if ((Math.abs(qk) < biginv) || (Math.abs(pk) < biginv)) {
				pkm2 *= big;
				pkm1 *= big;
				qkm2 *= big;
				qkm1 *= big;
			}
		} while (++n < 300);

		return ans;
	}

	/**
	 * Returns the Incomplete Gamma function; formerly named <tt>igamma</tt>.
	 * 
	 * @param a
	 *            the parameter of the gamma distribution.
	 * @param x
	 *            the integration end point.
	 */
	static public double incompleteGamma(double a, double x)
			throws ArithmeticException {

		double ans, ax, c, r;

		if (x <= 0 || a <= 0) {
			return 0.0;
		}

		if (x > 1.0 && x > a) {
			return 1.0 - incompleteGammaComplement(a, x);
		}

		/* Compute x**a * exp(-x) / gamma(a) */
		ax = a * Math.log(x) - x - math.logGamma(a);
		if (ax < -MAXLOG) {
			return (0.0);
		}

		ax = Math.exp(ax);

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
	 * Returns the Complemented Incomplete Gamma function; formerly named
	 * <tt>igamc</tt>.
	 * 
	 * @param a
	 *            the parameter of the gamma distribution.
	 * @param x
	 *            the integration start point.
	 */
	static public double incompleteGammaComplement(double a, double x)
			throws ArithmeticException {
		double ans, ax, c, yc, r, t, y, z;
		double pk, pkm1, pkm2, qk, qkm1, qkm2;

		if (x <= 0 || a <= 0) {
			return 1.0;
		}

		if (x < 1.0 || x < a) {
			return 1.0 - incompleteGamma(a, x);
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

	// /**
	// * Returns the natural logarithm of the gamma function; formerly named
	// <tt>lgamma</tt>.
	// */
	// public static double logGamma(double x) throws ArithmeticException {
	// double p, q, w, z;
	//
	// double A[] = {
	// 8.11614167470508450300E-4,
	// -5.95061904284301438324E-4,
	// 7.93650340457716943945E-4,
	// -2.77777777730099687205E-3,
	// 8.33333333333331927722E-2
	// };
	// double B[] = {
	// -1.37825152569120859100E3,
	// -3.88016315134637840924E4,
	// -3.31612992738871184744E5,
	// -1.16237097492762307383E6,
	// -1.72173700820839662146E6,
	// -8.53555664245765465627E5
	// };
	// double C[] = {
	// /* 1.00000000000000000000E0, */
	// -3.51815701436523470549E2,
	// -1.70642106651881159223E4,
	// -2.20528590553854454839E5,
	// -1.13933444367982507207E6,
	// -2.53252307177582951285E6,
	// -2.01889141433532773231E6
	// };
	//
	// if (x < -34.0) {
	// q = -x;
	// w = logGamma(q);
	// p = Math.floor(q);
	// if (p == q) {
	// throw new ArithmeticException("lgam: Overflow");
	// }
	// z = q - p;
	// if (z > 0.5) {
	// p += 1.0;
	// z = p - q;
	// }
	// z = q * Math.sin(Math.PI * z);
	// if (z == 0.0) {
	// throw new ArithmeticException("lgamma: Overflow");
	// }
	// z = LOGPI - Math.log(z) - w;
	// return z;
	// }
	//
	// if (x < 13.0) {
	// z = 1.0;
	// while (x >= 3.0) {
	// x -= 1.0;
	// z *= x;
	// }
	// while (x < 2.0) {
	// if (x == 0.0) {
	// throw new ArithmeticException("lgamma: Overflow");
	// }
	// z /= x;
	// x += 1.0;
	// }
	// if (z < 0.0) {
	// z = -z;
	// }
	// if (x == 2.0) {
	// return Math.log(z);
	// }
	// x -= 2.0;
	// p = x * Polynomial.polevl(x, B, 5) / Polynomial.p1evl(x, C, 6);
	// return (Math.log(z) + p);
	// }
	//
	// if (x > 2.556348e305) {
	// throw new ArithmeticException("lgamma: Overflow");
	// }
	//
	// q = (x - 0.5) * Math.log(x) - x + 0.91893853320467274178;
	// //if( x > 1.0e8 ) return( q );
	// if (x > 1.0e8) {
	// return (q);
	// }
	//
	// p = 1.0 / (x * x);
	// if (x >= 1000.0) {
	// q += ((7.9365079365079365079365e-4 * p
	// - 2.7777777777777777777778e-3) * p
	// + 0.0833333333333333333333) / x;
	// } else {
	// q += Polynomial.polevl(p, A, 4) / x;
	// }
	// return q;
	// }

}