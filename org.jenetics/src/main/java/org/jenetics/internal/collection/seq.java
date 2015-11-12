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
package org.jenetics.internal.collection;

import org.jenetics.internal.util.require;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class seq {
	private seq() {require.noInstance();}

	public static <T> ISeq<T> concat(
		final ISeq<? extends T> a,
		final ISeq<? extends T> b
	) {
		final MSeq<T> seq = MSeq.ofLength(a.length() + b.length());
		for (int i = 0; i < a.length(); ++i) {
			seq.set(i, a.get(i));
		}
		for (int i = 0; i < b.length(); ++i) {
			seq.set(a.length() + i, b.get(i));
		}

		return seq.toISeq();
	}
}
