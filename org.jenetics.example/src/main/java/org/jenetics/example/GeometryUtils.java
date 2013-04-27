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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.example;

import java.awt.geom.Point2D;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.0 &mdash; <em>$Date: 2013-04-27 $</em>
 */
final class GeometryUtils {
	private static final double GAMUT = 500000000;
	private static final double MID = GAMUT/2;

	private static final class IntPoint {
		int _x;
		int _y;
		IntPoint(final int x, final int y) {
			_x = x;
			_y = y;
		}
	}

	static final class DoublePoint {
		double _x;
		double _y;
		DoublePoint(final double x, final double y) {
			_x = x;
			_y = y;
		}
	}

	static final class BoundingBox {
		DoublePoint _min;
		DoublePoint _max;
		BoundingBox(final DoublePoint min, final DoublePoint max) {
			_min = min;
			_max = max;
		}
	}

	static final class Range {
		int _mn;
		int _mx;
		Range(final int mn, final int mx) {
			_mn = mn;
			_mx = mx;
		}
	}

	static final class Vertex {
		IntPoint _ip;
		Range _rx;
		Range _ry;
		int _in;
	}


	private long _ssss;
	private double _sclx;
	private double _scly;

	private GeometryUtils() {
	}

	private void contribution(
			final int fx,
			final int fy,
			final int tx,
			final int ty,
			final int w
	) {
		_ssss += (long)w*(tx - fx)*(ty + fy)/2;
	}

	private void fit(
			final Point2D[] x,
			final int cx,
			final Vertex[] ix,
			final int fudge,
			final BoundingBox bbox
	) {
		for (int i = 0; i < cx; ++i) {
			ix[i] = new Vertex();
			ix[i]._ip = new IntPoint(
					((int)((x[i].getX() - bbox._min._x)*_sclx - MID) & ~7) | fudge | (i & 1),
					((int)((x[i].getY() - bbox._min._y)*_scly - MID) & ~7) | fudge
				);
		}

		ix[0]._ip._y += cx & 1;
		ix[cx] = ix[0];

		for (int i = 0; i < cx; ++i) {
			ix[i]._rx = ix[i]._ip._x < ix[i + 1]._ip._x ?
						new Range(ix[i]._ip._x, ix[i + 1]._ip._x) :
						new Range(ix[i + 1]._ip._x, ix[i]._ip._x);

			ix[i]._ry = ix[i]._ip._y < ix[i + 1]._ip._y ?
						new Range(ix[i]._ip._y, ix[i + 1]._ip._y) :
						new Range(ix[i + 1]._ip._y, ix[i]._ip._y);

			ix[i]._in = 0;
		}
	}

	private void cross(
			final Vertex a,
			final Vertex b,
			final Vertex c,
			final Vertex d,
			final double a1,
			final double a2,
			final double a3,
			final double a4
	) {
		double r1 = a1/(a1 + a2);
		double r2 = a3/(a3 + a4);

		contribution(
				(int)(a._ip._x + r1*(b._ip._x - a._ip._x)),
				(int)(a._ip._y + r1*(b._ip._y - a._ip._y)),
				b._ip._x, b._ip._y,
				1
			);
		contribution(
				d._ip._x,
				d._ip._y,
				(int)(c._ip._x + r2*(d._ip._x - c._ip._x)),
				(int)(c._ip._y + r2*(d._ip._y - c._ip._y)),
				1
			);

		++a._in;
		--c._in;
	}

	private void inness(final Vertex[] P, final int cP, final Vertex[] Q, final int cQ) {
		int s = 0;
		int c = cQ;
		IntPoint p = P[0]._ip;

		while (c-- > 0) {
			if (Q[c]._rx._mn < p._x && p._x < Q[c]._rx._mx) {
				boolean sgn = 0 < area(p, Q[c]._ip, Q[c + 1]._ip);
				s += (sgn != Q[c]._ip._x < Q[c + 1]._ip._x) ? 0 : (sgn ? -1 : 1);
			}
		}

		for (int j = 0; j < cP; ++j) {
			if (s != 0) {
				contribution(P[j]._ip._x, P[j]._ip._y, P[j + 1]._ip._x, P[j + 1]._ip._y, s);
			}
			s += P[j]._in;
		}
	}


	private double intersection(final Point2D[] a, final Point2D[] b) {
		final int na = a.length;
		final int nb = b.length;
		final Vertex[] ipa = new Vertex[na + 1];
		final Vertex[] ipb = new Vertex[nb + 1];
		final BoundingBox bbox = new BoundingBox(
				new DoublePoint(Double.MAX_VALUE, Double.MAX_VALUE),
				new DoublePoint(-Double.MAX_VALUE, -Double.MAX_VALUE)
			);

		if (na < 3 || nb < 3) {
			return 0;
		}

		range(a, na, bbox);
		range(b, nb, bbox);

		final double rngx = bbox._max._x - bbox._min._x;
		_sclx = GAMUT/rngx;

		final double rngy = bbox._max._y - bbox._min._y;
		_scly = GAMUT/rngy;

		double ascale = _sclx*_scly;

		fit(a, na, ipa, 0, bbox);
		fit(b, nb, ipb, 2, bbox);

		for (int j = 0; j < na; ++j) {
			for (int k = 0; k < nb; ++k) {
				if (overlap(ipa[j]._rx, ipb[k]._rx) && overlap(ipa[j]._ry, ipb[k]._ry)) {
					long a1 = -area(ipa[j]._ip, ipb[k]._ip, ipb[k + 1]._ip);
					long a2 = area(ipa[j + 1]._ip, ipb[k]._ip, ipb[k + 1]._ip);
					boolean o = a1 < 0;

					if (o == a2 < 0) {
						long a3 = area(ipb[k]._ip, ipa[j]._ip, ipa[j + 1]._ip);
						long a4 = -area(ipb[k + 1]._ip, ipa[j]._ip, ipa[j + 1]._ip);
						if (a3 < 0 == a4 < 0) {
							if (o) {
								cross(
										ipa[j],
										ipa[j + 1],
										ipb[k],
										ipb[k + 1],
										a1, a2, a3, a4
									);
							} else {
								cross(
										ipb[k],
										ipb[k + 1],
										ipa[j],
										ipa[j + 1],
										a3, a4, a1, a2
									);
							}
						}
					}
				}
			}
		}

		inness(ipa, na, ipb, nb);
		inness(ipb, nb, ipa, na);

		return _ssss/ascale;
	}

	private static void range(
			final Point2D[] points,
			final int c,
			final BoundingBox bbox
	) {
		for (int i = 0; i < c; ++i) {
			bbox._min._x = Math.min(bbox._min._x, points[i].getX());
			bbox._min._y = Math.min(bbox._min._y, points[i].getY());
			bbox._max._x = Math.max(bbox._max._x, points[i].getX());
			bbox._max._y = Math.max(bbox._max._y, points[i].getY());
		}
	}

	private static long area(final IntPoint a, final IntPoint p, final IntPoint q) {
		return (long)p._x*q._y -
				(long)p._y*q._x +
				(long)a._x*(p._y - q._y) +
				(long)a._y*(q._x - p._x);
	}

	private static boolean overlap(Range p, Range q) {
		return p._mn < q._mx && q._mn < p._mx;
	}

	public static double area(final Point2D[] a, final Point2D[] b) {
		return new GeometryUtils().intersection(a, b);
	}
}
