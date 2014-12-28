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

import java.util.function.Function;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0 &mdash; <em>$Date: 2014-04-21 $</em>
 */
public class ArrayProxyISeq<T, P extends ArrayProxy<T, ?, ?>>
	extends ArrayProxySeq<T, P>
	implements ISeq<T>
{

	private static final long serialVersionUID = 1L;

	public ArrayProxyISeq(final P proxy) {
		super(proxy);
	}

	@Override
	public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper) {
		return new ArrayProxyISeq<>(proxy.map(mapper));
	}

	@Override
	public ISeq<T> subSeq(final int start) {
		return new ArrayProxyISeq<>(proxy.slice(start));
	}

	@Override
	public ISeq<T> subSeq(int start, int end) {
		return new ArrayProxyISeq<>(proxy.slice(start, end));
	}

	@Override
	public MSeq<T> copy() {
		return new ArrayProxyMSeq<>(proxy.copy());
	}

}
