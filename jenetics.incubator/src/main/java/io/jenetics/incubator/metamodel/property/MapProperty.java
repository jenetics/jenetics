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
import java.util.Map;

import io.jenetics.incubator.metamodel.type.MapType;

/**
 * Base class for associative properties which consists of 0 to n objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class MapProperty
	extends PropertyDelegates
	implements CollectionProperty, ConcreteProperty
{

	MapProperty(final PropParam param) {
		super(param);
	}

	@Override
	public MapType type() {
		return (MapType)param.type();
	}

	/**
	 * Return the list values as {@code Map} object.
	 *
	 * @return the map values
	 */
	@SuppressWarnings("unchecked")
	public Map<Object, Object> map() {
		return (Map<Object, Object>)value();
	}

	@Override
	public int size() {
		return map() != null ? map().size() : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Object> iterator() {
		return map() != null
			? (Iterator<Object>)(Object)map().entrySet().iterator()
			: emptyIterator();
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
