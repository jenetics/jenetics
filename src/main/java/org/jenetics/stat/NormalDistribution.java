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

import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.function.Variable;
import org.jscience.mathematics.number.Float64;

/**
 * Normal distribution.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class NormalDistribution<
	N extends Number & Comparable<? super N>
	>
	implements Distribution<N> 
{
	
	static final class PDF<N extends Number & Comparable<? super N>> 
		extends Function<N, Float64> 
	{
		private static final long serialVersionUID = 1L;
		
		// Create and initialize the used variable 'x'.
		private final Variable<N> _variable = new Variable.Local<N>("x");
		private final List<Variable<N>> _variables = new FastList<Variable<N>>(1);
		{ _variables.add(_variable); }
		
		private final Domain<N> _domain;
		private final double _mean;
		private final double _var;
		
		public PDF(final Domain<N> domain, final double mean, final double var) {
			_domain = domain;
			_mean = mean;
			_var = var;
		}
		
		@Override
		public Float64 evaluate() {
			final double x = _variable.get().doubleValue();
			
			Float64 result = Float64.ZERO;
			if (_domain.contains(_variable.get())) {
				result = Float64.valueOf(
						(1.0/Math.sqrt(2*Math.PI*_var))*
						Math.exp(-(x - _mean)*(x - _mean)/(2*_var))
					);
			}
			
			return result;
		}
	
		@Override
		public List<Variable<N>> getVariables() {
			return _variables;
		}
	
		@Override
		public Text toText() {
			return Text.valueOf(String.format("p(x) = N(%f, %f)(x)", _mean, _var));
		}
		
	}
	
	static final class CDF<N extends Number & Comparable<? super N>> 
		extends Function<N, Float64> 
	{
		private static final long serialVersionUID = 1L;
		
		// Create and initialize the used variable 'x'.
		private final Variable<N> _variable = new Variable.Local<N>("x");
		private final List<Variable<N>> _variables = new FastList<Variable<N>>(1);
		{ _variables.add(_variable); }
		
		private final double _min;
		private final double _max;
		private final double _mean;
		private final double _var;
		
		public CDF(final Domain<N> domain, final double mean, final double var) {
			_min = domain.getMin().doubleValue();
			_max = domain.getMax().doubleValue();
			_mean = mean;
			_var = var;
		}
		
		@Override
		public Float64 evaluate() {
			final double x = _variable.get().doubleValue();
			
			Float64 result = null;
			if (x < _min) {
				result = Float64.ZERO;
			} else if (x > _max) {
				result = Float64.ONE; 
			} else {
				result = Float64.valueOf(
						(1.0 + erf((x - _mean)/Math.sqrt(2*_var)))/2.0
					);
			}
			
			return result;
		}
	
		static double erf(final double z) {
			final double t = 1.0/(1.0 + 0.5*Math.abs(z));

			// Horner's method
			final double result = 1 - t*Math.exp(
					-z*z - 1.26551223 + 
					t*( 1.00002368 + 
					t*( 0.37409196 + 
					t*( 0.09678418 + 
					t*(-0.18628806 + 
					t*( 0.27886807 + 
					t*(-1.13520398 + 
					t*( 1.48851587 + 
					t*(-0.82215223 + 
					t*(0.17087277))))))))));
			
			return z >= 0 ? result : -result;
		}
		
		@Override
		public List<Variable<N>> getVariables() {
			return _variables;
		}
	
		@Override
		public Text toText() {
			return Text.valueOf(String.format(
					"P(x) = 1/2(1 + erf((x - %f)/(sqrt(2*%f))))", _mean, _var
				));
		}
		
	}
	
	private final Domain<N> _domain;
	private final double _mean;
	private final double _var;
	
	public NormalDistribution(final Domain<N> domain, final double mean, final double var) {
		_domain = domain;
		_mean = mean;
		_var = var;
	}
	
	@Override
	public Domain<N> getDomain() {
		return _domain;
	}
	
	@Override
	public Function<N, Float64> cdf() {
		return new CDF<N>(_domain, _mean, _var);
	}
	
	@Override
	public Function<N, Float64> pdf() {
		return new PDF<N>(_domain, _mean, _var);
	}

}
