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

import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: org.eclipse.jdt.ui.prefs 421 2010-03-18 22:41:17Z fwilhelm $
 */
abstract class Option<T> {

	public static enum Type {
		Some, None
	}

	public static final class Some<T> extends Option<T> {

		private final T _value;

		private Some(final T value) {
			_value = value;
		}

		@Override
		public Type type() {
			return Type.Some;
		}

		@Override
		public T get() {
			return _value;
		}

		@Override
		public int hashCode() {
			return _value.hashCode();
		}

		@Override
		public boolean equals(final Object other) {
			if (other == this) {
				return true;
			}
			if (!(other instanceof Some<?>)) {
				return false;
			}

			final Some<?> some = (Some<?>)other;
			return _value.equals(some._value);
		}

		@Override
		public String toString() {
			return String.format("Some(%s)", _value);
		}

	}

	public static final class None<T> extends Option<T> {

		private None() {
		}

		@Override
		public Type type() {
			return Type.None;
		}

		@Override
		public T get() {
			throw new NoSuchElementException();
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(final Object other) {
			return NONE == other;
		}

		@Override
		public String toString() {
			return "None";
		}
	}


	private Option() {
	}

	private static final None<?> NONE = new None<>();

	public abstract Type type();

	public abstract T get();

	public static <T> Option<T> Some(final T value) {
		return value != null ? new Some<>(value) : Option.<T>None();
	}

	@SuppressWarnings("unchecked")
	public static <T> Option<T> None() {
		return (Option<T>)NONE;
	}

}







