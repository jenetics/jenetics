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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.util;

import java.util.stream.Stream;

/**
 * @param <T> the object type this factory creates.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0
 */
@FunctionalInterface
public interface Factory<T> {

	/**
	 * Create a new instance of type T.
	 *
	 * @return a new instance of type T
	 */
	public T newInstance();

	/**
	 * Return a new stream of object instances, created by this factory.
	 *
	 * @since 3.0
	 *
	 * @return a stream of objects, created by this factory
	 */
	public default Stream<T> instances() {
		return Stream.generate(this::newInstance);
	}

}
