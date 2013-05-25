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
package org.jenetics.util;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
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
		return hashCodeOf(getClass()).and(_1).and(_2).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final Tuple2<?, ?> tuple = (Tuple2<?, ?>)obj;
		return eq(_1, tuple._1) && eq(_2, tuple._2);
	}

	@Override
	public String toString() {
		return "(" + _1 + ", " + _2 + ")";
	}

}


