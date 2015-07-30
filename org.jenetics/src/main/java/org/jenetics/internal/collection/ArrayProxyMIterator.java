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


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0
 */
public class ArrayProxyMIterator<T, P extends ArrayProxy<T, ?, ?>>
	extends ArrayProxyIterator<T, P>
{

	public ArrayProxyMIterator(final P proxy) {
		super(proxy);
	}

	@Override
	public void set(final T value) {
		if (lastElement < 0) {
			throw new IllegalStateException();
		}
		proxy.cloneIfSealed();
		proxy.__set(lastElement, value);
	}

}
