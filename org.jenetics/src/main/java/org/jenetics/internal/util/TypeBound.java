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
import org.jenetics.util.StaticObject;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date$</em>
 */
public final class TypeBound extends StaticObject {

	public static class Extends<A, B> implements Function<A, B> {
		private Extends() {
		}

		@Override
		@SuppressWarnings("unchecked")
		public B apply(final A value) {
			return (B)value;
		}
	}

	public static final class Equals<A, B> extends Extends<A, B> {
		private Equals() {
		}
	}



	public static <T> Equals<T, T> Equals() {
		return new Equals<>();
	}

	public static <B, A extends B> Extends<A, B> Extends() {
		return new Extends<>();
	}


	private TypeBound() {}



/*

	static interface Seq<T> {
		public boolean allTrue(final Equals<T, Boolean> bound);
		public int sum(final Extends<T, Number> bound);
	}

	static void main() {
		final Seq<String> strings = null;
		final Seq<Boolean> booleans = null;
		final Seq<Integer> ints = null;

		//strings.allTrue(TypeBound.<Boolean>Equals());
		booleans.allTrue(TypeBound.<Boolean>Equals());

		ints.sum(TypeBound.<Number, Integer>Extends());
	}
*/

}














