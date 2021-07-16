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
package io.jenetics.tool.measurement;

import static java.lang.String.format;

/**
 * Represents a stored DB value with its ID.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Stored<T> {
	private final long _id;
	private final T _value;

	private Stored(final long id, final T value) {
		_id = id;
		_value = value;
	}

	public long id() {
		return _id;
	}

	public T value() {
		return _value;
	}

	@Override
	public String toString() {
		return format("Stored[id=%d, value=%s]", _id, _value);
	}

	public static <T> Stored<T> of(final long id, final T value) {
		return new Stored<>(id, value);
	}

}
