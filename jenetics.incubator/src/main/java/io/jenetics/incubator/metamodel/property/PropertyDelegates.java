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
package io.jenetics.incubator.metamodel.property;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.access.Accessor;
import io.jenetics.incubator.metamodel.access.Writer;
import io.jenetics.incubator.metamodel.type.ModelType;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
abstract class PropertyDelegates {

	final PropParam param;

	PropertyDelegates(final PropParam param) {
		this.param = requireNonNull(param);
	}

	public Path path() {
		return param.path();
	}

	public Object enclosure() {
		return param.enclosure();
	}

	public Object value() {
		return param.value();
	}

	public ModelType type() {
		return param.type();
	}

	public Stream<Annotation> annotations() {
		return param.annotations().stream();
	}

	public Object read() {
		return param.accessor().getter().get();
	}

	public Optional<Writer> writer() {
		return param.accessor() instanceof Accessor.Writable
			? Optional.of(this::write)
			: Optional.empty();
	}

	/**
	 * Writes a new value to the property.
	 *
	 * @param value the new property value
	 * @return {@code true} if the new value has been written, {@code false}
	 * otherwise
	 */
	private boolean write(final Object value) {
		try {
			if (param.accessor() instanceof Accessor.Writable(var __, var setter)) {
				setter.set(value);
				return true;
			} else {
				return false;
			}
		} catch (VirtualMachineError | LinkageError e) {
			throw e;
		} catch (Throwable e) {
			return false;
		}
	}

}
