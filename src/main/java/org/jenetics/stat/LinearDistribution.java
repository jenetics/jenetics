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

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.io.Serializable;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Function;
import org.jenetics.util.Range;


/**
 * <p>This distribution has the following cdf.</p>
 * <p><img src="doc-files/LinearDistribution.png" /></p>
 * <p>
 * The only restriction is that the integral of the cdf must be one.
 * </p>
 * <p>
 * <img src="doc-files/linear-precondition.gif"
 *      alt="\int_{x_1}^{x_2}\left( 
 *             \\underset{k} {\\underbrace {\frac{y_2-y_1}{x_2-x_1}}} \cdot x + 
 *             \\underset{d}{\\underbrace {y_1-\frac{y_2-y_1}{x_2-x_1}\cdot x_1}}
 *           \right)\mathrm{d}x = 1"
 *  />
 *  </p>
 *  
 *  Solving this integral leads to
 *  <p>
 *  <img src="doc-files/linear-precondition-y2.gif"
 *       alt="y_2 = -\frac{(x_2-x_1)\cdot y_1 - 2}{x_2-x_1}"
 *  />
 *  </p>
 *  
 *  for fixed values for <i>x<sub>1</sub></i>, <i>x<sub>2</sub></i> and 
 *  <i>y<sub>1</sub></i>.
 *  <p>
 *  If the value of <i>y<sub>2</sub></i> < 0, the value of <i>x<sub>2</sub></i>
 *  is decreased so that the resulting triangle (<i>x<sub>1</sub></i>,0), 
 *  (<i>x<sub>1</sub></i>,<i>y<sub>1</sub></i>), (<i>x<sub>2</sub></i>,0) has 
 *  an area of <i>one</i>.
 *  </p>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class LinearDistribution<
	N extends Number & Comparable<? super N>
>
	implements Distribution<N> 
{
	
	/**
	 * <p>
	 * <img 
	 *     src="doc-files/linear-pdf.gif"
	 *     alt="f(x) = \left( 
	 *                      \frac{y_2-y_1}{x_2-x_1} \cdot x + 
	 *                      y_1-\frac{y_2-y_1}{x_2-x_1}\cdot x_1
	 *                 \right)"
	 * />
	 * </p>
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	static final class PDF<N extends Number & Comparable<? super N>> 
		implements 
			Function<N, Float64>,
			Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private final double _min;
		private final double _max;
		private final double _k;
		private final double _d;
		
		public PDF(
			final double x1, final double y1, 
			final double x2, final double y2
		) {
			_min = x1;
			_max = x2;
			_k = (y2 - y1)/(x2 - x1);
			_d = y1 - _k*x1;
		}
		
		@Override
		public Float64 apply(final N value) {
			final double x = value.doubleValue();
			
			Float64 result = Float64.ZERO;
			if (x >= _min && x <= _max) {
				result = Float64.valueOf(_k*x + _d);
			}
			
			return result;
		}
	
		@Override
		public String toString() {
			return String.format("p(x) = %f·x + %f", _k, _d);
		}
		
	}
	
	/**
	 * <p>
	 * <img 
	 *     src="doc-files/linear-cdf.gif"
	 *     alt="f(x)=-\frac{(x^2-2x_2x)y_1 - (x^2 - 2x_1x)y_2}
	 *      {2(x_2 - x_1)}"
	 * />
	 * </p>
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	static final class CDF<N extends Number & Comparable<? super N>> 
		implements 
			Function<N, Float64>,
			Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private final double _x1;
		private final double _x2;
		
		private final double _k;
		private final double _d;
		
		public CDF(
			final double x1, final double y1, 
			final double x2, final double y2
		) {
			_x1 = x1;
			_x2 = x2;
			_k = (y2 - y1)/(x2 - x1);
			_d = y1 - _k*x1;
		}
		
		@Override
		public Float64 apply(final N value) {
			final double x = value.doubleValue();
			
			Float64 result = null;
			if (x < _x1) {
				result = Float64.ZERO;
			} else if (x > _x2) {
				result = Float64.ONE; 
			} else {
//				result = Float64.valueOf(
//						-((x*x - 2*x*_x2)*_y1 - (x*x - 2*x*_x1)*_y2)/
//						(2*(_x2 - _x1))
//					);
				result = Float64.valueOf( _k*x*x/2.0 + _d*x);
			}
			
			return result;
		}
	
		@Override
		public String toString() {
			return String.format("P(x) = %f·x² - %f·x", _k/2.0, _d);
		}
		
	}
		
	
	private final Range<N> _domain;
	private final Function<N, Float64> _cdf;
	private final Function<N, Float64> _pdf;
	
	private final double _x1;
	private final double _x2;
	private final double _y1;
	private final double _y2;
	
	public LinearDistribution(final Range<N> domain, final double y1) {
		_domain = nonNull(domain);
		
		_y1 = Math.max(y1, 0.0);
		_x1 = domain.getMin().doubleValue();
		_y2 = Math.max(y2(_x1, domain.getMax().doubleValue(), y1), 0.0);
		if (_y2 == 0) {
			_x2 = 2.0/_y1 + _x1;
		} else {
			_x2 = domain.getMax().doubleValue();
		}
		
		_cdf = new CDF<>(_x1, _y1, _x2, _y2);
		_pdf = new PDF<>(_x1, _y1, _x2, _y2);
	}
	
	private static double y2(final double x1, final double x2, final double y1) {
		return -((x2 - x1)*y1 - 2)/(x2 - x1);
	}
	
	@Override
	public Range<N> getDomain() {
		return _domain;
	}

	/**
	 * Return a new CDF object.
	 * 
	 * <p>
	 * <img 
	 *     src="doc-files/linear-cdf.gif"
	 *     alt="f(x)=-\frac{(x^2-2x_2x)y_1 - (x^2 - 2x_1x)y_2}
	 *      {2(x_2 - x_1)}"
	 * />
	 * </p>
	 *  
	 */
	@Override
	public Function<N, Float64> getCDF() {
		return _cdf;
	}

	/**
	 * Return a new PDF object.
	 * 
	 * <p>
	 * <img 
	 *     src="doc-files/linear-pdf.gif"
	 *     alt="f(x) = \left( 
	 *                      \frac{y_2-y_1}{x_2-x_1} \cdot x + 
	 *                      y_1-\frac{y_2-y_1}{x_2-x_1}\cdot x_1
	 *                 \right)"
	 * />
	 * </p>
	 *  
	 */
	@Override
	public Function<N, Float64> getPDF() {
		return _pdf;
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).
				and(_domain).
				and(_x1).and(_x2).
				and(_y1).and(_y2).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		final LinearDistribution<?> dist = (LinearDistribution<?>)obj;
		return eq(_domain, dist._domain) &&
				eq(_x1, dist._x1) && eq(_x2, dist._x2) &&
				eq(_y1, dist._y1) && eq(_y2, dist._y2);
	}
	
	@Override
	public String toString() {
		return String.format(
				"LinearDistribution[(%f, %f), (%f, %f)]", 
				_x1, _y1, _x2, _y2
			) ;
	}

}





