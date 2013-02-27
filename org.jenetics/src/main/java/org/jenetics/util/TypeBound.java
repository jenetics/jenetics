/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date: 2013-02-27 $</em>
 */
public interface TypeBound<A, B> extends Function<A, B> {

	public static final Boolean<java.lang.Boolean> Boolean = new Boolean<java.lang.Boolean>() {
		@Override
		public java.lang.Boolean apply(final java.lang.Boolean value) {
			return value;
		}
	};

	public static final Byte<java.lang.Byte> Byte = new Byte<java.lang.Byte>() {
		@Override
		public java.lang.Byte apply(final java.lang.Byte value) {
			return value;
		}
	};

	public static final Character<java.lang.Character> Character = new Character<java.lang.Character>() {
		@Override
		public java.lang.Character apply(final java.lang.Character value) {
			return value;
		}
	};

	public static final Short<java.lang.Short> Short = new Short<java.lang.Short>() {
		@Override
		public java.lang.Short apply(final java.lang.Short value) {
			return value;
		}
	};

	public static final Integer<java.lang.Integer> Integer = new Integer<java.lang.Integer>() {
		@Override
		public java.lang.Integer apply(final java.lang.Integer value) {
			return value;
		}
	};

	public static final Long<java.lang.Long> Long = new Long<java.lang.Long>() {
		@Override
		public java.lang.Long apply(final java.lang.Long value) {
			return value;
		}
	};

	public static final Float<java.lang.Float> Float = new Float<java.lang.Float>() {
		@Override
		public java.lang.Float apply(final java.lang.Float value) {
			return value;
		}
	};

	public static final Double<java.lang.Double> Double = new Double<java.lang.Double>() {
		@Override
		public java.lang.Double apply(final java.lang.Double value) {
			return value;
		}
	};

	public static interface Boolean<T> extends TypeBound<T, java.lang.Boolean> {}
	public static interface Byte<T> extends TypeBound<T, java.lang.Byte> {}
	public static interface Character<T> extends TypeBound<T, java.lang.Character> {}
	public static interface Short<T> extends TypeBound<T, java.lang.Short> {}
	public static interface Integer<T> extends TypeBound<T, java.lang.Integer> {}
	public static interface Long<T> extends TypeBound<T, java.lang.Long> {}
	public static interface Float<T> extends TypeBound<T, java.lang.Float> {}
	public static interface Double<T> extends TypeBound<T, java.lang.Double> {}


}
