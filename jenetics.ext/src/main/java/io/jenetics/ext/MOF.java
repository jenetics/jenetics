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
package io.jenetics.ext;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

/**
 * Multi objective fitness value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MOF<T> implements Comparable<MOF<T>> {




	@Override
	public int compareTo(final MOF<T> o) {
		return 0;
	}

	public static <T> int
	compare(final T[] a, final T[] b, final Comparator<? super T> comparator) {
		requireNonNull(comparator);
		if (a.length != b.length) {
			throw new IllegalArgumentException();
		}

		boolean adom = false;
		boolean bdom = false;

		for (int i = 0; i < a.length; ++i) {
			final int cmp = comparator.compare(a[i], b[i]);

			if (cmp > 0) {
				adom = true;
				if (bdom) {
					return 0;
				}
			} else if (cmp < 0) {
				bdom = true;
				if (adom) {
					return 0;
				}
			}
		}

		if (adom == bdom) {
			return 0;
		} else if (adom) {
			return 1;
		} else {
			return -1;
		}
	}

}
