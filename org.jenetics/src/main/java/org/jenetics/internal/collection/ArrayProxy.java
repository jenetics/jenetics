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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jenetics.util.Copyable;

/**
 * Abstraction for an ordered and bounded sequence of elements.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.1
 */
public abstract class ArrayProxy<T, A, P extends ArrayProxy<T, A, P>>
	implements
		Copyable<P>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * This flag determines if {@code this} represents a sealed proxy instance.
	 */
	transient boolean _isSealedProxy = false;

	/**
	 * Contains all sealed proxies, which share the same {@code array} as
	 * {@code this} proxy instance.
	 */
	transient Stack<WeakReference<ArrayProxy<?, ?, ?>>>
		_sealedProxies = new Stack<>();

	/**
	 * Used for creating new array proxy instances.
	 */
	private final ArrayProxyFactory<A, P> _proxyFactory;

	/**
	 * Used for creating copies of the current array.
	 */
	private final ArrayCopier<A> _arrayCopier;

	/**
	 * The actual array, where the elements are stored.
	 */
	public A array;

	/**
	 * The start index of the array view, inclusively.
	 */
	public int start;

	/**
	 * The end index of the array view, exclusively.
	 */
	public int end;

	/**
	 * The actual array length.
	 */
	public final int length;



	/**
	 * Create a new array proxy.
	 *
	 * @param array the array which is wrapped by this proxy
	 * @param start the start index of the wrapped array (inclusively)
	 * @param end the end index of the wrapped array (exclusively)
	 * @param factory factory function for creating new proxy objects
	 * @param copier array cloning function
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}
	 * @throws java.lang.IllegalArgumentException if the start and end indexes
	 *         are invalid.
	 */
	protected ArrayProxy(
		final A array,
		final int start,
		final int end,
		final ArrayProxyFactory<A, P> factory,
		final ArrayCopier<A> copier
	) {
		if (start < 0 || end < 0 || end < start) {
			throw new IllegalArgumentException(format(
				"Invalid indexes [%d, %d)", start, end
			));
		}

		this.array = requireNonNull(array);
		this.length = end - start;
		this.start = start;
		this.end = end;

		_proxyFactory = requireNonNull(factory);
		_arrayCopier = requireNonNull(copier);
	}

	/**
	 * Return the <i>array</i> element at the specified, absolute position in
	 * the {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param index absolute index of the element to return
	 * @return the <i>array</i> element at the specified absolute position
	 */
	public abstract T __get__(final int index);

	/**
	 * Set the <i>array</i> element at the specified absolute position in the
	 * {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param index absolute index of the <i>array</i> element
	 * @param value the value to set
	 */
	public abstract void __set__(final int index, final T value);

	/**
	 * Return the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param index index of the element to return
	 * @return the <i>array</i> element at the specified position
	 */
	public final T __get(final int index) {
		return __get__(index + start);
	}

	/**
	 * Set the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param index index of the <i>array</i> element
	 * @param value the value to set
	 */
	public final void __set(final int index, final T value) {
		__set__(index + start, value);
	}

	/**
	 * Return the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}.
	 *
	 * @param index index of the element to return
	 * @return the <i>array</i> element at the specified position
	 * @throws IndexOutOfBoundsException if the index it out of range
	 *         (index &lt; 0 || index &gt;= _length).
	 */
	public final T get(final int index) {
		checkIndex(index);
		return __get__(index + start);
	}

	/**
	 * Set the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}
	 *
	 * @param index the index of the element to set
	 * @param value the <i>array</i> element
	 * @throws IndexOutOfBoundsException if the index it out of range
	 *         {@code (index &lt; 0 || index &gt;= _length)}.
	 */
	public final void set(final int index, final T value) {
		checkIndex(index);
		__set__(index + start, value);
	}

	/**
	 * Return a new sub {@code ArrayProxy} object with the given start and end
	 * indexes. The underlying array storage is not copied. With the returned
	 * sub-array proxy it is possible to <i>write through</i> the original
	 * array.
	 *
	 * @param from the start index of the new sub {@code ArrayProxy} object,
	 *        inclusively.
	 * @param until the end index of the new sub {@code ArrayProxy} object,
	 *        exclusively.
	 * @return a new array proxy (view) with the given start and end index.
	 * @throws IndexOutOfBoundsException if the given indexes are out of bounds.
	 */
	public final P slice(final int from, final int until) {
		final P slice = _proxyFactory.create(array, from + start, until + start);
		slice._isSealedProxy = _isSealedProxy;
		slice._sealedProxies = _sealedProxies;

		if (_isSealedProxy) {
			_sealedProxies.push(new WeakReference<>(slice));
		}

		return slice;
	}

	/**
	 * Return a new sub {@code ArrayProxy} object with the given start index.
	 * The underlying array storage is not copied. With the returned sub-array
	 * proxy it is possible to <i>write through</i> the original array.
	 *
	 * @param from the start index of the new sub {@code ArrayProxy} object,
	 *        inclusively.
	 * @return a new array proxy (view) with the given start index.
	 * @throws IndexOutOfBoundsException if the given indexes are out of bounds.
	 */
	public final P slice(final int from) {
		return slice(from, length);
	}

	/**
	 * Set the seal flag for this {@code ArrayProxy} instance and return a new
	 * {@code ArrayProxy} object with an not set <i>seal</i> flag but with the
	 * same underlying data structure.
	 *
	 * @return a new {@code ArrayProxy} instance; for command chaining.
	 */
	public final P seal() {
		assert !_isSealedProxy : "Must not be called on sealed proxies";

		final P proxy = _proxyFactory.create(array, start, end);
		proxy._sealedProxies = _sealedProxies;
		proxy._isSealedProxy = true;
		_sealedProxies.push(new WeakReference<>(proxy));

		return proxy;
	}

	/**
	 * Clone the underlying data structure of this {@code ArrayProxy} if it is
	 * sealed.
	 */
	public final void cloneIfSealed() {
		assert !_isSealedProxy : "Must not be called on sealed proxies";

		if (_sealedProxies.length > 0) {
			_sealedProxies.popAll(p -> {
				final ArrayProxy<?, ?, ?> proxy = p.get();
				if (proxy != null) {
					proxy.copyArray();
				}
			});
		}
	}

	private void copyArray() {
		assert _isSealedProxy : "Must only be called on sealed proxies";

		array = _arrayCopier.copy(array, start, end);
		_sealedProxies = new Stack<>();
		_isSealedProxy = false;
		start = 0;
		end = length;
	}

	/**
	 * Return a stream with the elements of this {@code ArrayProxy}.
	 *
	 * @since 3.0
	 *
	 * @return a stream with the elements of this {@code ArrayProxy}
	 */
	public Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * Return a parallel stream with the elements of this {@code ArrayProxy}.
	 *
	 * @since 3.0
	 *
	 * @return a stream with the elements of this {@code ArrayProxy}
	 */
	public Stream<T> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}

	public Spliterator<T> spliterator() {
		return new ArrayProxySpliterator<>(this);
	}

	/**
	 * Swap a given range with a range of the same size with another array.
	 * Implementations of this class should replace this with a optimized
	 * version, depending on the underlying data structure.
	 *
	 * <pre>
	 *            from                until
	 *              |                   |
	 * this:  +---+---+---+---+---+---+---+---+---+---+---+---+
	 *              +---------------+
	 *                          +---------------+
	 * other: +---+---+---+---+---+---+---+---+---+---+---+---+
	 *                          |
	 *                      otherFrom
	 * </pre>
	 *
	 * @param start the start index of {@code this} range, inclusively.
	 * @param end the end index of {@code this} range, exclusively.
	 * @param other the other array to swap the elements with.
	 * @param otherStart the start index of the {@code other} array.
	 * @throws IndexOutOfBoundsException if {@code start > end} or
	 *         if {@code from < 0 || until >= this.length() || otherFrom < 0 ||
	 *         otherFrom + (until - from) >= other.length()}
	 */
	public void swap(
		final int start,
		final int end,
		final ArrayProxy<T, ?, ?> other,
		final int otherStart
	) {
		checkIndex(start, end);
		other.checkIndex(otherStart, otherStart + (end - start));
		cloneIfSealed();
		other.cloneIfSealed();

		for (int i = end - start; --i >= 0;) {
			final T temp = __get(i + start);
			__set(i + start, other.__get(otherStart + i));
			other.__set(otherStart + i, temp);
		}
	}

	public final <B> ObjectArrayProxy<B> map(
		final Function<? super T, ? extends B> mapper
	) {
		return map(mapper, ObjectArrayProxy<B>::new);
	}

	public final <B, P extends ArrayProxy<B, ?, ?>> P map(
		final Function<? super T, ? extends B> mapper,
		final IntFunction<P> builder
	) {
		final P result = builder.apply(length);
		assert result.length == length;

		for (int i = 0; i < length; ++i) {
			result.__set__(i, mapper.apply(__get(i)));
		}
		return result;
	}

	@Override
	public P copy() {
		return _proxyFactory
			.create(_arrayCopier.copy(array, start, end), 0, end - start);
	}

	/**
	 * Checks the given index.s
	 *
	 * @param start the index to check.
	 * @throws java.lang.ArrayIndexOutOfBoundsException if the given index is
	 *         not in the valid range.
	 */
	protected final void checkIndex(final int start) {
		if (start < 0 || start >= length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", start, length
			));
		}
	}

	/**
	 * Check the given {@code from} and {@code until} indices.
	 *
	 * @param start the start index, inclusively.
	 * @param end the end index, exclusively.
	 * @throws java.lang.ArrayIndexOutOfBoundsException if the given index is
	 *         not in the valid range.
	 */
	protected final void checkIndex(final int start, final int end) {
		if (start > end) {
			throw new ArrayIndexOutOfBoundsException(format(
				"fromIndex(%d) > toIndex(%d)", start, end
			));
		}
		if (start < 0 || end > length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %s)", start, end
			));
		}
	}

}
