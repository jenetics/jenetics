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
package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import io.jenetics.incubator.property.Property.ValueReader;
import io.jenetics.incubator.property.Property.ValueWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
abstract class PropertyDescriptionMethods {

	final PropertyDescription desc;
	final Object enclosingObject;

	PropertyDescriptionMethods(
		final PropertyDescription desc,
		final Object enclosingObject
	) {
		this.desc = requireNonNull(desc);
		this.enclosingObject = requireNonNull(enclosingObject);
	}

	public Object enclosingObject() {
		return enclosingObject;
	}

	public Class<?> type() {
		return desc.type();
	}

	public ValueReader reader() {
		return this::read;
	}

	private Object read() {
		return desc.read(enclosingObject);
	}

	public boolean isWritable() {
		return desc.isWriteable();
	}

	public Optional<ValueWriter> writer() {
		return desc.isWriteable()
			? Optional.of(this::write)
			: Optional.empty();
	}

	private boolean write(final Object value) {
		return desc.write(enclosingObject, value);
	}

}
