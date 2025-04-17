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

/**
 * Base class for properties which consists of 0 to n objects and can be accessed
 * via an element index.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public abstract sealed class IndexedProperty
	extends CollectionProperty
	permits OptionalProperty, ArrayProperty, ListProperty
{

	IndexedProperty(final PropParam param) {
		super(param);
	}

	/**
	 * Return the size of the <em>indexed</em> property.
	 *
	 * @return the size of the <em>indexed</em> property
	 */
	public abstract int size();

	/**
	 * Return the property value at the given {@code index}.
	 *
	 * @param index the property index
	 * @return the property value at the given index
	 */
	public abstract Object get(final int index);

}
