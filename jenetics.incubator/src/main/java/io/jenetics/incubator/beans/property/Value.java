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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public sealed interface Value permits Immutable, Mutable {

	/**
	 * Returns the object which contains {@code this} node.
	 *
	 * @return the object which contains {@code this} node
	 */
	Object enclosure();

	/**
	 * The value of the metaobject, may be {@code null}. This method always
	 * returns the initial property value.
	 *
	 * @return the <em>original</em> value of the metaobject
	 */
	Object value();

	/**
	 * The type of the property value, never {@code null}.
	 *
	 * @return the type of the property value
	 */
	Class<?> type();
}
