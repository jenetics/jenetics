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

import java.util.List;

import javolution.text.Text;
import javolution.util.FastList;

import org.jenetics.util.Validator;
import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.function.Variable;
import org.jscience.mathematics.number.Float64;

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
 *       alt="y_2 = \frac{(x_2-x_1)\cdot y_1 - 2}{x_2-x_1}"
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
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	static final class PDF<N extends Number & Comparable<? super N>> 
		extends Function<N, Float64> 
	{
		private static final long serialVersionUID = 1L;
		
		private final List<Variable<N>> 
			_variables = new FastList<Variable<N>>(1);
		
		private final double _min;
		private final double _max;
		private final Float64 _probability;
		
		public PDF(final Domain<N> domain) {
			_min = domain.getMin().doubleValue();
			_max = domain.getMax().doubleValue();
			_probability = Float64.valueOf(1.0/(_max - _min));
			
			_variables.add(new Variable.Local<N>("x"));
		}
		
		@Override
		public Float64 evaluate() {
			final double x = _variables.get(0).get().doubleValue();
			
			Float64 result = Float64.ZERO;
			if (x >= _min && x <= _max) {
				result = _probability;
			}
			
			return result;
		}
	
		@Override
		public List<Variable<N>> getVariables() {
			return _variables;
		}
	
		@Override
		public Text toText() {
			return null;
		}
		
	}
	
	
	private final Domain<N> _domain;
	
	public LinearDistribution(final Domain<N> domain) {
		_domain = Validator.nonNull(domain);
	}
	
	@Override
	public Domain<N> getDomain() {
		return _domain;
	}

	@Override
	public Function<N, Float64> cdf() {
		return null;
	}

	@Override
	public Function<N, Float64> pdf() {
		return new PDF<N>(_domain);
	}

}
