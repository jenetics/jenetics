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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.stat;


/**
 * <p>Calculate the variance from a finite sample of <i>n</i> observations:</p>
 * <p><img src="doc-files/variance.gif" alt="Variance" /></p>
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance" >
 * 		  Wikipedia: Algorithms for calculating variance</a>
 * @see <a href="http://mathworld.wolfram.com/Variance.html">
 * 		  Wolfram MathWorld: Variance</a>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Variance<N extends Number> extends Mean<N> {
	
	private double _m2 = Double.NaN;
	
	public Variance() {
	}
	
	/**
	 * Return the variance of the accumulated values.
	 * <p><img src="doc-files/variance.gif" alt="Variance" /></p>
	 * 
	 * @return the variance of the accumulated values, or {@link java.lang.Double#NaN}
	 *         if {@code getSamples() == 0}.
	 */
	public double getVariance() {
		double variance = Double.NaN;
		
		if (_samples == 1) {
			variance = _m2;
		} else if (_samples > 1) {
			variance = _m2/(double)(_samples - 1);
		}
		
		return variance;
	}
	
	/**
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	@Override
	public void accumulate(final N value) {
		if (_samples == 0) {
			_mean = 0;
			_m2 = 0;
		}
		++_samples;
		
		final double data = value.doubleValue();
		final double delta = data - _mean;
		_mean += delta/(double)_samples;
		_m2 += delta*(data - _mean);
	}
	
	@Override
	public String toString() {
		return String.format(
					"%s[samples=%d, mean=%f, stderr=%f, var=%f]", 
					getClass().getSimpleName(), 
					getSamples(), 
					getMean(), 
					getStandardError(), 
					getVariance()
				);
	}
}
