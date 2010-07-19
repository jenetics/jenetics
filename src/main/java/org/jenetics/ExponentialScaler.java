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
 * modified such that 
 * <p/><img src="doc-files/exponential-scaler.gif" alt="Exponential Scaler" />.</p>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ExponentialScaler implements FitnessScaler<Float64>, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final ExponentialScaler SQR_SCALER = new ExponentialScaler(2);
	public static final ExponentialScaler SQRT_SCALER = new ExponentialScaler(0.5);

	private final double _a;
	private final double _b;
	private final double _c;
	
	/**
	 * Create a new FitnessScaler.
	 * 
	 * @param c <pre>fitness = (1 * fitness + 0) ^ <strong>c</strong></pre>
	 */
	public ExponentialScaler(final double c) {
		this(0.0, c);
	}
	
	/**
	 * Create a new FitnessScaler.
	 * 
	 * @param b <pre>fitness = (1 * fitness + <strong>b</strong>) ^ c</pre>
	 * @param c <pre>fitness = (1 * fitness + b) ^ <strong>c</strong></pre>
	 */
	public ExponentialScaler(final double b, final double c) {
		this(1.0, b, c);
	}
	
	/**
	 * Create a new FitnessScaler.
	 * 
	 * @param a <pre>fitness = (<strong>a</strong> * fitness + b) ^ c</pre>
	 * @param b <pre>fitness = (a * fitness + <strong>b</strong>) ^ c</pre>
	 * @param c <pre>fitness = (a * fitness + b) ^ <strong>c</strong></pre>
	 */
	public ExponentialScaler(final double a, final double b, final double c) {
		_a = a;
		_b = b;
		_c = c;
	}

	@Override
	public Float64 scale(final Float64 value) {
		return Float64.valueOf(Math.pow((_a*value.doubleValue() + _b), _c));
	}
}
