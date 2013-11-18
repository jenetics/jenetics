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
package org.jenetics.internal.util;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jenetics.util.Factory;
import org.jenetics.util.StaticObject;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date$</em>
 */
public final class lists extends StaticObject {
	private lists() {}

	public static <T> void fill(
		final List<? super T> list,
		final Factory<? extends T> factory,
		final int size
	) {
		final Object[] array = arrays.fill(new Object[size], factory);

		// This will tricking the ArrayList.addAll method.
		list.addAll(new AbstractCollection<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public Iterator<T> iterator() {
				return Arrays.asList((T[])array).iterator();
			}
			@Override
			public int size() {
				return array.length;
			}
			@Override
			public Object[] toArray() {
				return array;
			}
		});
	}
}





