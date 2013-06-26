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

import java.util.Iterator;

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date$</em>
 */
public class ArrayProxyMSeq<T> extends ArrayProxySeq<T> implements MSeq<T> {

	ArrayProxyMSeq(ArrayProxy<T> proxy) {
		super(proxy);
	}

	@Override
	public MSeq<T> copy() {
		return null;
	}

	@Override
	public void set(int index, T value) {
	}

	@Override
	public MSeq<T> setAll(T value) {
		return null;
	}

	@Override
	public MSeq<T> setAll(Iterator<? extends T> it) {
		return null;
	}

	@Override
	public MSeq<T> setAll(Iterable<? extends T> values) {
		return null;
	}

	@Override
	public MSeq<T> setAll(T[] values) {
		return null;
	}

	@Override
	public MSeq<T> fill(Factory<? extends T> factory) {
		return null;
	}

	@Override
	public void swap(int i, int j) {
	}

	@Override
	public void swap(int start, int end, MSeq<T> other, int otherStart) {
	}

	@Override
	public MSeq<T> subSeq(int start, int end) {
		return null;
	}

	@Override
	public MSeq<T> subSeq(int start) {
		return null;
	}

	@Override
	public <B> MSeq<B> map(Function<? super T, ? extends B> mapper) {
		return null;
	}

	@Override
	public ISeq<T> toISeq() {
		return null;
	}

}
