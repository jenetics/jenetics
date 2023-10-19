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
package io.jenetics.incubator.beans.description;

import java.lang.reflect.Type;

import io.jenetics.incubator.beans.Path;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class IndexDescription extends SimpleDescription {

	private final int index;

	public IndexDescription(
		final Path path,
		final Class<?> enclosure,
		final Type type,
		final Access access,
		final int index
	) {
		super(path, enclosure, type, access);
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

}
