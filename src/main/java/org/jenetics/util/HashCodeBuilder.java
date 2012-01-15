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

/**
 * Interface for building hash codes. The HashCodeBuilder is created via a
 * factory method in the {@link object} class.
 * <p/>
 * Example for calculating the hash code for a given class:
 * [code]
 * public int hashCode() {
 *     return object.hashCodeOf(getClass())
 *                  .and(_prop1)
 *                  .and(_prop2).value();
 * }
 * [/code]
 * 
 * @see object#hashCodeOf(Class)
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class HashCodeBuilder {

	int _hash = 0;
	
	HashCodeBuilder(final Class<?> type) {
		_hash = type.hashCode();
	}
	
	/**
	 * Add hash code for a {@code boolean}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final boolean value);

	/**
	 * Add hash code for an {@code boolean} array.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final boolean[] values);

	/**
	 * Add hash code for a {@code byte}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final byte value);

	/**
	 * Add hash code for an {@code byte} arrays.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final byte[] values);

	/**
	 * Add hash code for a {@code char}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final char value);

	/**
	 * Add hash code for an {@code char} array.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final char[] values);

	/**
	 * Add hash code for a {@code short}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final short value);

	/**
	 * Add hash code for an {@code short} array.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final short[] values);

	/**
	 * Add hash code for an {@code int}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final int value);

	/**
	 * Add hash code for an {@code int} array.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final int[] values);

	/**
	 * Add hash code for a {@code long}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final long value);

	/**
	 * Add hash code for an {@code long} array.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final long[] values);

	/**
	 * Add hash code for a {@code float}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final float value);

	/**
	 * Add hash code for an {@code float} array.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final float[] values);

	/**
	 * Add hash code for a {@code double}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final double value);

	/**
	 * Add hash code for an {@code double} array.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final double[] values);

	/**
	 * Add hash code for a {@code Object}.
	 * 
	 * @param value the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final Object value);

	/**
	 * Add hash code for an {@code Object}.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final Object[] values);

	/**
	 * Add hash code for a {@code Seq}.
	 * 
	 * @param values the value to add to the hash code.
	 * @return {@code this}
	 */
	public abstract HashCodeBuilder and(final Seq<?> values);

	/**
	 * Return the calculated hash value.
	 * 
	 * @return the calculated hash value.
	 */
	public int value() {
		return _hash;
	}

}



