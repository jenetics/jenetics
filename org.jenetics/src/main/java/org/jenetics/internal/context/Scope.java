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
package org.jenetics.internal.context;

import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
final class Scope<A, B> implements Scoped<A> {

	private final A _value;
	private final ThreadLocal<Entry<B>> _threadLocalEntry;

	Scope(final A value, final ThreadLocal<Entry<B>> threadLocalEntry) {
		_value = value;
		_threadLocalEntry = threadLocalEntry;
	}

	@Override
	public A get() {
		return _value;
	}

	@Override
	public void close() {
		final Entry<B> e = _threadLocalEntry.get();
		if (e != null) {
			if (e.thread != Thread.currentThread()) {
				throw new IllegalStateException(
					"Value context must be closed by the creating thread."
				);
			}

			_threadLocalEntry.set(e.parent);
		} else {
			throw new IllegalStateException(
				"Value context has been already close."
			);
		}
	}

}
