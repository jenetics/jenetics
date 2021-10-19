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
package io.jenetics.util;

/**
 * This interface defines a <em>recursive</em> generic type {@code S}, which
 * represents the type of the implementing class.
 * <pre>{@code
 * interface Foo<T extends Foo<T>> extends Self<T> {
 *     // ...
 * }
 * }</pre>
 * Using the {@code Self} interface in this case makes it clear that the generic
 * type {@code T} of the interface {@code Foo} represents the concrete type of
 * the class, implementing the interface {@code Foo}.
 * <p>
 * If the interface is used as intended, the following generic {@code min} method
 * can be implemented as a <em>default</em> method.
 * <pre>{@code
 * interface Foo<A extends Foo<A>> extends Self<A>, Comparable<A> {
 *     // ...
 *
 *     default A max(final A other) {
 *         return compareTo(other) > 0 ? self() : other;
 *     }
 * }
 * }</pre>
 *
 * @param <S> the type of the implementing class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public interface Self<S extends Self<S>> {

	/**
	 * Return a reference of {@code this} object as the declared generic type
	 * {@code S}.
	 *
	 * @return the {@code this} reference as the generic type {@code S}
	 * @throws ClassCastException if the interface is not used as intended and
	 *         {@code (this instanceof S) == false}
	 */
	@SuppressWarnings("unchecked")
	default S self() {
		return (S)this;
	}

}
