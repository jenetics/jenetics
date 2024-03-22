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

import java.util.Optional;

import io.jenetics.incubator.beans.Path;

/**
 * Represents a simple property value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public sealed class SimpleProperty
	implements Property
	permits IndexProperty, StructProperty
{
	private final PropParam param;

	SimpleProperty(final PropParam param) {
        this.param = requireNonNull(param);
    }

	@Override
	public Path path() {
		return param.path();
	}

	@Override
	public Object enclosure() {
		return param.enclosure();
	}

	@Override
	public Object value() {
		return param.value();
	}

	@Override
	public Class<?> type() {
		return param.type();
	}

	@Override
	public Object read() {
		return param.getter().get(enclosure());
	}

	@Override
	public Optional<Writer> writer() {
		return param.setter() != null
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
			param.setter().set(enclosure(), value);
			return true;
		} catch (VirtualMachineError | LinkageError e) {
			throw e;
		} catch (Throwable e) {
			return false;
		}
	}

    @Override
    public String toString() {
        return Properties.toString(getClass().getSimpleName(), this);
    }

}
