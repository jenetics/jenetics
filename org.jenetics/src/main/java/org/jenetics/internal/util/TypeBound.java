/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
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














