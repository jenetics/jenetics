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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class NumberUniform<
	N extends Number & Comparable<? super N>
>
	implements Distribution<N> 
{

	static final class CDF<N extends Number> extends Function<N, Float64> {
		private static final long serialVersionUID = 1L;
		
		private final List<Variable<N>> 
			_variables = new FastList<Variable<N>>(1);
		
		private final double _min;
		private final double _max;
		private final double _divisor;
		
		public CDF(final N min, final N max) {
			_min = min.doubleValue();
			_max = max.doubleValue();
			_divisor = _max - _min;
			
			_variables.add(new Variable.Local<N>("x"));
		}
		
		@Override
		public Float64 evaluate() {
			final double x = getVariables().get(0).get().doubleValue();
			return Float64.valueOf((x - _min)/_divisor);
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


	private final N _min;
	private final N _max;

	public NumberUniform(final N min, final N max) {
		_min = min;
		_max = max;
	}

	@Override
	public N getMin() {
		return _min;
	}

	@Override
	public N getMax() {
		return _max;
	}

	@Override
	public Function<N, Float64> getCDF() {
		return new CDF<N>(_min, _max);
	}

	@Override
	public Function<N, Float64> getPDF() {
		return null;
	}

}









