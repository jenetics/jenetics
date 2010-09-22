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
package org.jenetics;

import java.util.List;

import javolution.text.Text;
import javolution.util.FastList;

import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.function.Variable;
import org.jscience.mathematics.number.Float64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
final class Distribution {

	private Distribution() {
		throw new AssertionError(); 
	}
	
	final static class Uniform {
		
		private Uniform() {
			throw new AssertionError();
		}
		
		/*
		 * (min + max)/2
		 */
		static double mean(final double min, final double max) {
			return (min + max)/2.0;
		}
		
		/*
		 * ((max - min)^2)/12
		 */
		static double variance(final double min, final double max) {
			return Math.pow(max - min, 2)/12.0;
		}
		
		
		static class CDF extends Function<Float64, Float64>  {
			private static final long serialVersionUID = 1L;
			
			private final double _min;
			private final double _max;
			
			public CDF(final double min, final double max) {
				_min = min;
				_max = max;
			}

			private final List<Variable<Float64>> _variables = 
				new FastList<Variable<Float64>>(1);
			{
				_variables.add(new Variable.Local<Float64>("x"));
			}
			
			@Override
			public Float64 evaluate() {
				final Float64 x = getVariables().get(0).get();	
				return Float64.valueOf((x.doubleValue() - _min)/(_max - _min));
			}

			@Override
			public List<Variable<Float64>> getVariables() {
				return _variables;
			}

			@Override
			public Text toText() {
				return Text.valueOf("CDF-UniformNumberDistribution[" + _min + ", " + _max + "]");
			}
		}
		
		static Function<Float64, Float64> cdf(
			final double min, 
			final double max
		) {
			return new CDF(min, max);
		}
		
		static Function<Float64, Float64> cdf(
				final Float64 min, 
				final Float64 max
			) {
				return new CDF(min.doubleValue(), max.doubleValue());
			}
		
	}
	
//	public static void main(String[] args) {
//		Function<Float64, Float64> cdf = UniformNumberDistribution.cdf(1, 11);
//		System.out.println(cdf.evaluate(Float64.valueOf(1)));
//		System.out.println(cdf.evaluate(Float64.valueOf(6)));
//		System.out.println(cdf.evaluate(Float64.valueOf(11)));
//	}
	
}
