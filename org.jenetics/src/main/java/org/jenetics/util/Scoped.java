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

/**
 * Defines a local scope, where the containing value is valid. When the scope is
 * left, the original value is restored. A <i>scoped</i> value is usually used
 * within a {@code try} block.
 * [code]
 * try (Scoped<Random> s = RandomRegistry.scope(new Random(123))) {
 *     System.out.println(s.get().nextDouble());
 * }
 * [/code]
 *
 * In the example above, the new random engine is only used within the defined
 * scope.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-02-24 $</em>
 * @since 1.6
 */
public interface Scoped<T> extends AutoCloseable {

	/**
	 * Return the scoped object.
	 *
	 * @return the scoped object.
	 */
	public T get();

	/**
	 * Closing this scope and restoring the original state.
	 */
	public void close();

}
