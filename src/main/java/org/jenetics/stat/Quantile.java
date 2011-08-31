/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.stat;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Arrays;

import org.jenetics.util.AdaptableAccumulator;

/**
 * Implementation of the quantile estimation algorithm published by
 * <p/>
 * <strong>Raj JAIN and Imrich CHLAMTAC</strong>:
 * <em>
 *     The P<sup>2</sup> Algorithm for Dynamic Calculation of Quantiles and
 *     Histograms Without Storing Observations
 * </em>
 * <br/>
 * [<a href="http://www.cse.wustl.edu/~jain/papers/ftp/psqr.pdf">Communications
 * of the ACM; October 1985, Volume 28, Number 10</a>]
 *
 * @see <a href="http://en.wikipedia.org/wiki/Quantile">Wikipedia: Quantile</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Quantile<N extends Number> extends AdaptableAccumulator<N> {

	// The desired quantile.
	private double _quantile;             

	// Marker heights.
	private final double[] _q = {0, 0, 0, 0, 0};

	// Marker positions.
	private final double[] _n = {0, 0, 0, 0, 0};

	// Desired marker positions.
	private final double[] _nn = {0, 0, 0};

	// Desired marker position increments.
	private final double[] _dn = {0, 0, 0};

	private boolean _initialized;

	/**
	 * Create a new quantile accumulator with the given value.
	 *
	 * @param quantile the wished quantile value.
	 * @throws IllegalArgumentException if the {@code quantile} is not in the
	 * 		  range {@code [0, 1]}.
	 */
	public Quantile(double quantile) {
		if (quantile < 0.0 || quantile > 1) {
			throw new IllegalArgumentException(String.format(
					"Quantile (%s) not in the valid range of [0, 1]", quantile
				));
		}
		_quantile = quantile;
		_n[0] = -1.0;
		_q[2] = 0.0;
		_initialized = Double.compare(_quantile, 0.0) == 0 ||
						Double.compare(_quantile, 1.0) == 0;
	}

	public double getQuantile() {
		return _q[2];
	}

	@Override
	public void accumulate(final N value) {
		if (!_initialized) {
			initialize(value.doubleValue());
		} else {
			update(value.doubleValue());
		}

		++_samples;
	}


	private void initialize(double value) {
		if (_n[0] < 0.0) {
			_n[0] = 0.0;
			_q[0] = value;
		} else if (_n[1] == 0.0) {
			_n[1] = 1.0;
			_q[1] = value;
		} else if (_n[2] == 0.0) {
			_n[2] = 2.0;
			_q[2] = value;
		} else if (_n[3] == 0.0) {
			_n[3] = 3.0;
			_q[3] = value;
		} else if (_n[4] == 0.0) {
			_n[4] = 4.0;
			_q[4] = value;
		}

		if (_n[4] != 0.0) {
			Arrays.sort(_q);

			_nn[0] = 2.0*_quantile;
			_nn[1] = 4.0*_quantile;
			_nn[2] = 2.0*_quantile + 2.0;

			_dn[0] = _quantile/2.0;
			_dn[1] = _quantile;
			_dn[2] = (1.0 + _quantile)/2.0;

			_initialized = true;
		}
	}

	private void update(double value) {
		assert (_initialized);

		// If min or max, handle as special case; otherwise, ...
		if (_quantile == 0.0) {
			if (value < _q[2]) {
				_q[2] = value;
			}
		} else if (_quantile == 1.0) {
			if (value > _q[2]) {
				_q[2] = value;
			}
		} else {
			// Increment marker locations and update min and max.
			if (value < _q[0]) {
				++_n[1]; ++_n[2]; ++_n[3]; ++_n[4]; _q[0] = value;
			} else if (value < _q[1]) {
				++_n[1]; ++_n[2]; ++_n[3]; ++_n[4];
			} else if (value < _q[2]) {
				++_n[2]; ++_n[3]; ++_n[4];
			} else if (value < _q[3]) {
				++_n[3]; ++_n[4];
			} else if (value < _q[4]) {
				++_n[4];
			} else {
				++_n[4]; _q[4] = value;
			}

			// Increment positions of markers k + 1
			_nn[0] += _dn[0];
			_nn[1] += _dn[1];
			_nn[2] += _dn[2];

			// Adjust heights of markers 0 to 2 if necessary
			double mm = _n[1] - 1.0;
			double mp = _n[1] + 1.0;
			if (_nn[0] >= mp && _n[2] > mp) {
				_q[1] = qPlus(mp, _n[0], _n[1], _n[2], _q[0], _q[1], _q[2]);
				_n[1] = mp;
			} else if (_nn[0] <= mm && _n[0] < mm) {
				_q[1] = qMinus(mm, _n[0], _n[1], _n[2], _q[0], _q[1], _q[2]);
				_n[1] = mm;
			}

			mm = _n[2] - 1.0;
			mp = _n[2] + 1.0;
			if (_nn[1] >= mp && _n[3] > mp) {
				_q[2] = qPlus(mp, _n[1], _n[2], _n[3], _q[1], _q[2], _q[3]);
				_n[2] = mp;
			} else if (_nn[1] <= mm && _n[1] < mm) {
				_q[2] = qMinus(mm, _n[1], _n[2], _n[3], _q[1], _q[2], _q[3]);
				_n[2] = mm;
			}

			mm = _n[3] - 1.0;
			mp = _n[3] + 1.0;
			if (_nn[2] >= mp && _n[4] > mp) {
				_q[3] = qPlus(mp, _n[2], _n[3], _n[4], _q[2], _q[3], _q[4]);
				_n[3] = mp;
			} else if (_nn[2] <= mm && _n[2] < mm) {
				_q[3] = qMinus(mm, _n[2], _n[3], _n[4], _q[2], _q[3], _q[4]);
				_n[3] = mm;
			}
		}
	}

	private static double qPlus(
		final double mp,
		final double m0,
		final double m1,
		final double m2,
		final double q0,
		final double q1,
		final double q2
	) {
		double result = q1 +
					((mp - m0)*(q2 - q1)/(m2 - m1) +
					(m2 - mp)*(q1 - q0)/(m1 - m0))/(m2 - m0);

		if (result > q2) {
			result = q1 + (q2 - q1)/(m2 - m1);
		}

		return result;
	}

	private static double qMinus(
		final double mm,
		final double m0,
		final double m1,
		final double m2,
		final double q0,
		final double q1,
		final double q2
	) {
		double result = q1 -
					((mm - m0)*(q2 - q1)/(m2 - m1) +
					(m2 - mm)*(q1 - q0)/(m1 - m0))/(m2 - m0);

		if (q0 > result) {
			result = q1 + (q0 - q1)/(m0 - m1);
		}

		return result;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).
				and(super.hashCode()).
				and(_quantile).
				and(_dn).
				and(_n).
				and(_nn).
				and(_q).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		final Quantile<?> quantile = (Quantile<?>)obj;
		return super.equals(obj) &&
				eq(_quantile, quantile._quantile) &&
				eq(_dn, quantile._dn) &&
				eq(_n, quantile._n) &&
				eq(_nn, quantile._nn) &&
				eq(_q, quantile._q);
	}
	
	@Override
	public String toString() {
		return String.format(
				"%s[samples=%d, qantile=%f]",
				getClass().getSimpleName(), getSamples(), getQuantile()
			);
	}
	
	@Override
	public Quantile<N> clone() {
		return (Quantile<N>)super.clone();
	}
}


