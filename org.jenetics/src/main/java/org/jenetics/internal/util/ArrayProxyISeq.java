/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.internal.util;

import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date$</em>
 */
public class ArrayProxyISeq<T> extends ArrayProxySeq<T> implements ISeq<T> {

	ArrayProxyISeq(final ArrayProxy<T> proxy) {
		super(proxy);
	}

	@Override
	public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper) {
		final ArrayProxyImpl<B> proxy = new ArrayProxyImpl<>(_proxy._length);
		for (int i = 0; i < proxy._length; ++i) {
			proxy._array[i] = mapper.apply(_proxy.uncheckedGet(i));
		}
		return new ArrayProxyISeq<>(proxy);
	}

	@Override
	public ISeq<T> subSeq(final int start) {
		return new ArrayProxyISeq<>(_proxy.sub(start));
	}

	@Override
	public ISeq<T> subSeq(int start, int end) {
		return new ArrayProxyISeq<>(_proxy.sub(start, end));
	}

	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public <A> ISeq<A> upcast(ISeq<? extends A> seq) {
		return (ISeq<A>)seq;
	}

	@Override
	public MSeq<T> copy() {
		return null;
	}

}
