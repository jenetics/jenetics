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

import static java.util.Collections.emptyIterator;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.type.ListType;

/**
 * Represents a list property.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class ListProperty
	extends PropertyDelegates
	implements IndexedProperty, ConcreteProperty
{

	ListProperty(final PropParam param) {
		super(param);
	}

	private static PropParam fix(final PropParam param) {
		var path = param.path().parent();
		if (path == null) {
			path = Path.of("");
		}
		if (param.path().element() instanceof Path.Field(var n, var e)) {
			if (!e) {
				path = path.append(new Path.Field(n, true));
			} else {
				path = param.path();
			}
		}

		return new PropParam(
			path,
			param.enclosure(),
			param.value(),
			param.type(),
			param.annotations(),
			param.accessor()
		);
	}

	@Override
	public ListType type() {
		return (ListType)param.type();
	}

	/**
	 * Return the list values as {@code List} object.
	 *
	 * @return the list values
	 */
	@SuppressWarnings("unchecked")
	public List<Object> list() {
		return (List<Object>)value();
	}

	@Override
	public int size() {
		return list() != null ? list().size() : 0;
	}

	@Override
	public Object get(final int index) {
		if (list() == null) {
			throw new IndexOutOfBoundsException("List is null.");
		}
		return list().get(index);
	}

	@Override
	public Iterator<Object> iterator() {
		return list() != null ? list().iterator() : emptyIterator();
	}

	//@Override
	public Stream<Object> stream() {
		return list().stream();
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
