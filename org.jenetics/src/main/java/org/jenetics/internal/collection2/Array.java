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
package org.jenetics.internal.collection2;

import java.lang.ref.WeakReference;

import org.jenetics.internal.collection.Stack;

import org.jenetics.util.Copyable;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class Array<T> implements Copyable<Array<T>> {

	private boolean _hasProxies = false;
	private final Stack<WeakReference<ArrayProxy<T>>> _proxies = new Stack<>();

	final void add(final ArrayProxy<T> proxy) {
		if (proxy.isSealed()) {
			_proxies.push(new WeakReference<>(proxy));
			_hasProxies = true;
		}
	}

	final void copySealedProxyArrays() {
		if (_hasProxies) {
			_proxies.popAll(reference -> {
				final ArrayProxy<T> proxy = reference.get();
				if (proxy != null) {
					proxy.lazyArrayCopy();
				}
			});
		}
	}

	public abstract void set(final int index, final T value);

	public abstract T get(final int index);

	public abstract Array<T> slice(final int from, final int until);

}
