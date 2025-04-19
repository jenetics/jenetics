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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.incubator.metamodel.Path;

/**
 * Base class for properties which consists of 0 to n objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public abstract sealed class SizedProperty
	implements Iterable<Object>, Property
	permits CollectionProperty, MapProperty
{

	final PropParam param;

	SizedProperty(final PropParam param) {
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
	public Stream<Annotation> annotations() {
		return param.annotations().stream();
	}

	@Override
	public Object read() {
		return param.access().getter().get(enclosure());
	}

	/**
	 * Return the property values.
	 *
	 * @return the property values
	 */
	public Stream<Object> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

	/**
	 * Return the size of the property.
	 *
	 * @return the size of the property
	 */
	abstract int size();

}
