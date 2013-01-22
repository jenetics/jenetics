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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.AbstractList;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date$</em>
 */
public class MSeqListAdapter<T> extends AbstractList<T> implements List<T> {

	private final MSeq<T> _adoptee;

	private int _index = 0;

	public MSeqListAdapter(final MSeq<T> adoptee) {
		_adoptee = object.nonNull(adoptee, "Adoptee");
	}

	@Override
	public T get(int index) {
		return _adoptee.get(index);
	}

	@Override
	public boolean add(final T value) {
		_adoptee.set(_index++, value);
		return true;
	}

	@Override
	public void add(final int index, final T value) {
		_adoptee.set(index, value);
	}

	@Override
	public T set(final int index, final T element) {
		final T old = _adoptee.get(index);
		_adoptee.set(index, element);
		return old;
	}

	@Override
	public int size() {
		return _adoptee.length();
	}

}







