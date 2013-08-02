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
package org.jenetics.internal.util;

import org.jenetics.util.Function;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date$</em>
 */
public abstract class TypeBound<A, B> implements Function<A, B> {

	public static class Boolean<T> extends TypeBound<T, java.lang.Boolean> {
		@Override
		public java.lang.Boolean apply(final T value) {
			return java.lang.Boolean.class.cast(value);
		}
	}

	public static final Boolean<java.lang.Boolean> Boolean = Boolean();

	public static <T> Boolean<T> Boolean() {
		return new Boolean<>();
	}

	public static <T> TypeBound<T, T> Id() {
		return new TypeBound<T, T>() {
			@Override
			public T apply(T value) {
				return null;
			}
		};
	}


	static interface Seq<T> {
		public boolean allTrue(final TypeBound<T, java.lang.Boolean> bound);
	}

	static void main() {
		final Seq<java.lang.String> strings = null;
		final Seq<java.lang.Boolean> booleans = null;

		final boolean result1 = strings.allTrue(new TypeBound<String, Boolean>(){});
		final boolean result2 = booleans.allTrue(Boolean);
		final boolean result3 = booleans.allTrue(TypeBound.<java.lang.Boolean>Id());
	}


	public static interface Byte<T> extends TypeBound<T, java.lang.Byte> {}
	public static interface Character<T> extends TypeBound<T, java.lang.Character> {}
	public static interface Short<T> extends TypeBound<T, java.lang.Short> {}
	public static interface Integer<T> extends TypeBound<T, java.lang.Integer> {}
	public static interface Long<T> extends TypeBound<T, java.lang.Long> {}
	public static interface Float<T> extends TypeBound<T, java.lang.Float> {}
	public static interface Double<T> extends TypeBound<T, java.lang.Double> {}
	public static interface String<T> extends TypeBound<T, T> {}

	public static final class Identity<T> implements TypeBound<T, T> {
		@Override
		public T apply(final T value) {
			return value;
		}
	}



	public static final Byte<java.lang.Byte>
	Byte = new Byte<java.lang.Byte>() {
		@Override
		public java.lang.Byte apply(final java.lang.Byte value) {
			return value;
		}
	};

	public static final Character<java.lang.Character>
	Character = new Character<java.lang.Character>() {
		@Override
		public java.lang.Character apply(final java.lang.Character value) {
			return value;
		}
	};

	public static final Short<java.lang.Short>
	Short = new Short<java.lang.Short>() {
		@Override
		public java.lang.Short apply(final java.lang.Short value) {
			return value;
		}
	};

	public static final Integer<java.lang.Integer>
	Integer = new Integer<java.lang.Integer>() {
		@Override
		public java.lang.Integer apply(final java.lang.Integer value) {
			return value;
		}
	};

	public static final Long<java.lang.Long>
	Long = new Long<java.lang.Long>() {
		@Override
		public java.lang.Long apply(final java.lang.Long value) {
			return value;
		}
	};

	public static final Float<java.lang.Float>
	Float = new Float<java.lang.Float>() {
		@Override
		public java.lang.Float apply(final java.lang.Float value) {
			return value;
		}
	};

	public static final Double<java.lang.Double>
	Double = new Double<java.lang.Double>() {
		@Override
		public java.lang.Double apply(final java.lang.Double value) {
			return value;
		}
	};

	public static final String<java.lang.String>
	String = new String<java.lang.String>() {
		@Override
		public java.lang.String apply(final java.lang.String value) {
			return value;
		}
	};

}
