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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import static io.jenetics.internal.util.Hashes.hash;

import java.util.Objects;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
class Tuple2<T1, T2> {

	final T1 _1;
	final T2 _2;

	public Tuple2(final T1 t1, final T2 t2) {
		_1 = t1;
		_2 = t2;
	}

	@Override
	public int hashCode() {
		return hash(_1, hash(_2));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Tuple2<?, ?> other &&
			Objects.equals(_1, other._1) &&
			Objects.equals(_2, other._2);
	}

	@Override
	public String toString() {
		return "(" + _1 + ", " + _2 + ")";
	}

}
