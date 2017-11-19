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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.Spliterator;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface LimitSpliterator<T> extends Spliterator<T> {

	public static final Predicate<?> TRUE = a -> true;

	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> TRUE() {
		return (Predicate<T>)TRUE;
	}

	public LimitSpliterator<T> limit(final Predicate<? super T> proceed);

	public static <T> Predicate<? super T> and(
		final Predicate<? super T> a,
		final Predicate<? super T> b
	) {
		requireNonNull(a);
		requireNonNull(b);

		final Predicate<? super T> result;
		if (a == TRUE && b == TRUE) {
			result = TRUE();
		} else if (a == TRUE) {
			result = b;
		} else if (b == TRUE) {
			result = a;
		} else {
			result = r -> a.test(r) & b.test(r);
		}

		return result;
	}

}
