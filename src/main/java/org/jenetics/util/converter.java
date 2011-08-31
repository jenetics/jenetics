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
package org.jenetics.util;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

/**
 * Some common converters.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class converter {

	private converter() {
		throw new AssertionError("Don't create 'converter' instance.");
	}
	
	public static final Converter<Float64, Double> Float64ToDouble =
		new Converter<Float64, Double>() {
			@Override public Double convert(final Float64 value) {
				return value.doubleValue();
			}
		};
		
	public static final Converter<Double, Float64> DoubleToFloat64 =
		new Converter<Double, Float64>() {
			@Override public Float64 convert(final Double value) {
				return Float64.valueOf(value);
			}
		};	
		
	public static final Converter<Integer64, Long> Integer64ToLong =
		new Converter<Integer64, Long>() {
			@Override public Long convert(final Integer64 value) {
				return value.longValue();
			}
		};
		
	public static final Converter<Long, Integer64> LongToInteger64 =
		new Converter<Long, Integer64>() {
			@Override public Integer64 convert(final Long value) {
				return Integer64.valueOf(value);
			}
		};
	
}
