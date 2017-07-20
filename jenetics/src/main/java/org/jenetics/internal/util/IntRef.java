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

import java.io.Serializable;

/**
 * Int reference class, which allows the usage in an lambda expression.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public final class IntRef implements Serializable {
	private static final long serialVersionUID = 1;

	/**
	 * The actual int value.
	 */
	public int value;

	/**
	 * Create a new {@code IntRef} object with the given initial value.
	 *
	 * @param initialValue the initial int value of the reference.
	 */
	public IntRef(final int initialValue) {
		value = initialValue;
	}

	/**
	 * Create a new {@code IntRef} object initialized with zero.
	 */
	public IntRef() {
		this(0);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IntRef && ((IntRef)obj).value == value;
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}

}
