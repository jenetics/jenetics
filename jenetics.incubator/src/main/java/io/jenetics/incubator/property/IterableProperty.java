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

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public sealed class IterableProperty
	extends PropertyMethods
	implements Iterable<Object>, Property
	permits ArrayProperty, CollectionProperty, MapProperty
{

	private final Path path;
	final Object value;

	IterableProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		super(desc, enclosingObject);
		this.path = path;
		this.value = value;
	}

	@Override
	public Path path() {
		return path;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<Object> iterator() {
		return ((Iterable<Object>)value).iterator();
	}

	@Override
	public Object value() {
		return null;
	}

	public Stream<Object> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

}
