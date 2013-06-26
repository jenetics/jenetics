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

import static java.lang.String.format;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date$</em>
 */
public abstract class ArrayProxy<T> {

	protected final int _start;
	protected final int _end;
	protected final int _length;

	protected ArrayProxy(final int start, final int end) {
		_start = start;
		_end = end;
		_length = _end - _start;
	}

	public abstract T uncheckedOffsetGet(final int absoluteIndex);

	public abstract ArrayProxy<T> sub(final int start, final int end);

	/**
	 * Return the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}.
	 *
	 * @param index index of the element to return
	 * @return the <i>array</i> element at the specified position
	 * @throws IndexOutOfBoundsException if the index it out of range
	 *          (index < 0 || index >= _length).
	 */
	public T get(final int index) {
		checkIndex(index);
		return uncheckedOffsetGet(index + _start);
	}

	/**
	 * Return the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param index index of the element to return
	 * @return the <i>array</i> element at the specified position
	 * @throws IndexOutOfBoundsException if the index it out of range
	 *          (index < 0 || index >= _length).
	 */
	public T uncheckedGet(final int index) {
		return uncheckedOffsetGet(index + _start);
	}

	public ArrayProxy<T> sub(final int start) {
		return sub(start, _length);
	}

	protected final void checkIndex(final int index) {
		if (index < 0 || index >= _length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", index, _length
			));
		}
	}

	protected final void checkIndex(final int from, final int to) {
		if (from > to) {
			throw new ArrayIndexOutOfBoundsException(
				"fromIndex(" + from + ") > toIndex(" + to+ ")"
			);
		}
		if (from < 0 || to > _length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %s)", from, to
			));
		}
	}


}

final class ObjectArrayProxy<T> extends ArrayProxy<T> {

	final Object[] _array;

	ObjectArrayProxy(final Object[] array, final int start, final int end) {
		super(start, end);
		_array = array;
	}

	ObjectArrayProxy(final int length) {
		this(new Object[length], 0, length);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T uncheckedOffsetGet(final int absoluteIndex) {
		return (T)_array[absoluteIndex];
	}

	@Override
	public ObjectArrayProxy<T> sub(final int start, final int end) {
		return new ObjectArrayProxy<>(_array, start + _start, end + _start);
	}

}


