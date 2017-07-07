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
package org.jenetics.programming.ops;

import static java.lang.String.format;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Var<T> implements Op<T> {

	private final int _index;

	private Var(final int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(
				"Index smaller than zero: " + index
			);
		}
		_index = index;
	}

	public int index() {
		return _index;
	}

	@Override
	public int arity() {
		return 0;
	}

	@Override
	public T apply(final T[] variables) {
		return variables[_index];
	}

	@Override
	public String toString() {
		return format("Var(%d)", _index);
	}

	public static <T> Var<T> of(final int index) {
		return new Var<>(index);
	}

}
