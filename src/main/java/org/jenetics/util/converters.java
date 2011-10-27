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

import java.util.Objects;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

/**
 * Some common type converters.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class converters {

	private converters() {
		throw new AssertionError("Don't create 'converters' instance.");
	}
	
	public static final Function<Object, String>
	ObjectToString = new Function<Object, String>() {
		@Override public String apply(final Object value) {
			return Objects.toString(value);
		}
	};
	
	public static final Function<Float64, Double> 
	Float64ToDouble = new Function<Float64, Double>() {
		@Override public Double apply(final Float64 value) {
			return value.doubleValue();
		}
	};
		
	public static final Function<Double, Float64> 
	DoubleToFloat64 = new Function<Double, Float64>() {
		@Override public Float64 apply(final Double value) {
			return Float64.valueOf(value);
		}
	};	
		
	public static final Function<Integer64, Long> 
	Integer64ToLong = new Function<Integer64, Long>() {
		@Override public Long apply(final Integer64 value) {
			return value.longValue();
		}
	};
		
	public static final Function<Long, Integer64> 
	LongToInteger64 = new Function<Long, Integer64>() {
		@Override public Integer64 apply(final Long value) {
			return Integer64.valueOf(value);
		}
	};
	
}
