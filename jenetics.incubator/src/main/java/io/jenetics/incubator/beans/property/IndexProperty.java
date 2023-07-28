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

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.description.IndexedDescription;

/**
 * This property represents an element of an {@link IndexedProperty}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class IndexProperty
	extends IndexedDescriptionMethods
	implements Property
{
    private final Path path;
    private final int index;
    private final Object value;

	IndexProperty(
	    final IndexedDescription desc,
	    final Object enclosingObject,
		final Path path,
		final int index,
		final Object value
    ) {
		super(desc, enclosingObject);
        this.path = path;
        this.index = index;
        this.value = value;
    }

    @Override
    public Path path() {
        return path;
    }

	@Override
	public Class<?> type() {
		return value() != null ? value.getClass() : desc.type();
	}

    public int index() {
        return index;
    }

    @Override
    public Object value() {
        return value;
    }

	@Override
	public String toString() {
		return Properties.toString(IndexProperty.class.getSimpleName(), this);
	}

}
