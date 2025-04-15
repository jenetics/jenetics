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

/**
 * This property represents a single element of an {@link IndexedProperty}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class IndexProperty extends SimpleProperty {

    private final int index;

	IndexProperty(final PropParam param, final int index) {
		super(param);
		this.index = index;
	}

	/**
	 * Return the actual index of the property.
	 *
	 * @return the actual index of the property
	 */
    public int index() {
        return index;
    }

	@Override
	public String toString() {
		return Properties.toString(IndexProperty.class.getSimpleName(), this);
	}

}
