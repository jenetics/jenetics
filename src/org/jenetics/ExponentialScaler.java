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

import java.io.Serializable;

import org.jscience.mathematics.number.Float64;

/**
 * Implements an exponential fitness scaling, whereby all fitness values are 
 * modified such that new fitness = (a * fitness + b) ^ exp.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ExponentialScaler.java,v 1.2 2010-01-18 22:09:05 fwilhelm Exp $
 */
public class ExponentialScaler implements FitnessScaler<Float64>, Serializable {
	private static final long serialVersionUID = -5895077899454677843L;
	
	public static final ExponentialScaler SQR_SCALER = new ExponentialScaler(2);
	public static final ExponentialScaler SQRT_SCALER = new ExponentialScaler(0.5);

	private final double _a;
	private final double _b;
	private final double _exp;
	
	/**
	 * Create a new FitnessScaler.
	 * 
	 * @param exp <pre>fitness = (1 * fitness + 0) ^ <strong>exp</strong></pre>
	 */
	public ExponentialScaler(final double exp) {
		this(0.0, exp);
	}
	
	/**
	 * Create a new FitnessScaler.
	 * 
	 * @param b <pre>fitness = (1 * fitness + <strong>b</strong>) ^ exp</pre>
	 * @param exp <pre>fitness = (1 * fitness + b) ^ <strong>exp</strong></pre>
	 */
	public ExponentialScaler(final double b, final double exp) {
		this(1.0, b, exp);
	}
	
	/**
	 * Create a new FitnessScaler.
	 * 
	 * @param a <pre>fitness = (<strong>a</strong> * fitness + b) ^ exp</pre>
	 * @param b <pre>fitness = (a * fitness + <strong>b</strong>) ^ exp</pre>
	 * @param exp <pre>fitness = (a * fitness + b) ^ <strong>exp</strong></pre>
	 */
	public ExponentialScaler(final double a, final double b, final double exp) {
		_a = a;
		_b = b;
		_exp = exp;
	}

	@Override
	public Float64 scale(final Float64 value) {
		return Float64.valueOf(Math.pow((_a*value.doubleValue() + _b), _exp));
	}
}
