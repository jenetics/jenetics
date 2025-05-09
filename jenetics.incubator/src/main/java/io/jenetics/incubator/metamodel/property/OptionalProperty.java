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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents an optional property.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class OptionalProperty
	extends PropertyDelegates
	implements EnclosingProperty, ConcreteProperty
{

	OptionalProperty(final PropParam param) {
		super(param);
	}

	/**
	 * Return the optional value as {@code Optional} object.
	 *
	 * @return the optional value
	 */
	@SuppressWarnings("unchecked")
	public Optional<Object> optional() {
		return (Optional<Object>)value();
	}

	@Override
	public int size() {
		return optional().isPresent() ? 1 : 0;
	}

	//@Override
	public Object get(int index) {
		if (optional().isPresent() && index == 0) {
			return optional().orElseThrow();
		} else {
			throw new NoSuchElementException(
				"Optional=%s, index=%s".formatted(optional(), index)
			);
		}
	}

	//@Override
	public Iterator<Object> iterator() {
		return optional().stream().iterator();
	}

	@Override
	public String toString() {
		return Properties.toString(OptionalProperty.class.getSimpleName(), this);
	}
}
