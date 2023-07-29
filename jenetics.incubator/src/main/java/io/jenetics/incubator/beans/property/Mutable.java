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
package io.jenetics.incubator.beans.property;

import static java.util.Objects.requireNonNull;

import io.jenetics.incubator.beans.description.Getter;
import io.jenetics.incubator.beans.description.Setter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Mutable implements Value {

	private final Object enclosure;
	private final Object value;
	private final Class<?> type;
	private final Getter getter;
	private final Setter setter;

	Mutable(
		final Object enclosure,
		final Object value,
		final Class<?> type,
		final Getter getter,
		final Setter setter
	) {
		this.enclosure = requireNonNull(enclosure);
		this.value = value;
		this.type = requireNonNull(type);
		this.getter = requireNonNull(getter);
		this.setter = requireNonNull(setter);
	}

	@Override
	public Object enclosure() {
		return enclosure;
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public Class<?> type() {
		return type;
	}

	public Object read() {
		return getter.apply(enclosure);
	}

	public boolean write(final Object value) {
		try {
			setter.apply(enclosure, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Mutable[value=%s, type=%s, enclosureType=%s]".formatted(
			value(),
			type().getName(),
			enclosure().getClass().getName()
		);
	}

}