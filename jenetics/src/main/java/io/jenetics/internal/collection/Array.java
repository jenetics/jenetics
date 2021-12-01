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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.internal.collection;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import io.jenetics.internal.collection.Array.Store.Ref;

/**
 * Array implementation class. This class manages the actual array (store) and
 * the start index and the length.
 *
 * @param <T> the array element type
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 3.4
 */
public final class Array<T> implements BaseMSeq<T>, Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

	private final Store.Ref<T> _store;
	private final int _start;
	private final int _length;

	/**
	 * Private <i>primary</i> constructor.
	 *
	 * @param store the store used by this array
	 * @param from the start index of the array
	 * @param until the end index of the array
	 */
	private Array(final Store.Ref<T> store, final int from, final int until) {
		_store = requireNonNull(store);
		_start = from;
		_length = until - from;
	}

	/**
	 * Create a new array from the given store
	 *
	 * @param store the store used by this array
	 */
	private Array(final Store<T> store) {
		this(Store.Ref.of(store), 0, store.length());
	}

	/**
	 * Get the array value at the given {@code index}. The array index is not
	 * checked.
	 *
	 * @param index the array index
	 * @return the value at the given index
	 */
	@Override
	public T get(final int index) {
		return _store.get(index + _start);
	}

	/**
	 * Return the array length.
	 *
	 * @return the array length
	 */
	@Override
	public int length() {
		return _length;
	}

	/**
	 * Set the {@code value} at the given {@code index}. The array index is not
	 * checked.
	 *
	 * @param index the array index
	 * @param value the value to set
	 */
	public void set(final int index, final T value) {
		_store.set(index + _start, value);
	}

	/**
	 * Return the underlying array store.
	 *
	 * @return the underlying array store
	 */
	public Store<T> store() {
		return _store._value;
	}

	public void copyIfSealed() {
		_store.copyIfSealed();
	}

	/**
	 * Return a new <i>sealed</i> array instance. The underlying store is sealed
	 * as well, but not copied.
	 *
	 * @return a new sealed array
	 */
	public Array<T> seal() {
		return new Array<>(_store.seal(), _start, _length + _start);
	}

	/**
	 * Return the seal state of the array.
	 *
	 * @return {@code true} is the array is sealed, {@code false} otherwise
	 */
	public boolean isSealed() {
		return _store.isSealed();
	}

	/**
	 * Sort the store.
	 *
	 * @param from the start index where to start sorting (inclusively)
	 * @param until the end index where to stop sorting (exclusively)
	 * @param comparator the {@code Comparator} used to compare sequence
	 *        elements. A {@code null} value indicates that the elements'
	 *        Comparable natural ordering should be used
	 */
	public void sort(
		final int from,
		final int until,
		final Comparator<? super T> comparator
	) {
		_store.sort(from + _start, until + _start, comparator);
	}

	/**
	 * Return a <i>new</i> {@code Array} object with the given values appended.
	 *
	 * @since 3.4
	 *
	 * @param array the values to append
	 * @return a <i>new</i> {@code Array} object with the elements of
	 *         {@code this} array and the given {@code array} appended.
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public Array<T> append(final Array<T> array) {
		final Array<T> appended = newInstance(length() + array.length());
		for (int i = 0; i < _length; ++i) {
			appended.set(i, get(i));
		}
		for (int i = 0; i < array._length; ++i) {
			appended.set(i + _length, array.get(i));
		}

		return appended;
	}

	private Array<T> newInstance(final int length) {
		return of(_store._value.newInstance(length));
	}

	/**
	 * Return a <i>new</i> {@code Array} with the given {@code values} appended.
	 *
	 * @since 3.4
	 *
	 * @param values the values to append
	 * @return a <i>new</i> {@code Array} with the elements of {@code this}
	 *        array and the given {@code values} appended.
	 * @throws NullPointerException if the given {@code values} iterable is
	 *         {@code null}
	 */
	public Array<T> append(final Iterable<? extends T> values) {
		final int size = size(values);
		final Array<T> array = newInstance(_length + size);

		for (int i = 0; i < _length; ++i) {
			array.set(i, get(i));
		}

		final Iterator<? extends T> it = values.iterator();
		for (int i = 0; i < size; ++i) {
			array.set(_length + i, it.next());
		}

		return array;
	}

	/**
	 * Return a <i>new</i> {@code Array} with the given {@code values} prepended.
	 *
	 * @since 3.4
	 *
	 * @param values the values to prepend
	 * @return a <i>new</i> {@code Array} with the elements of {@code this}
	 *        array and the given {@code values} appended.
	 * @throws NullPointerException if the given {@code values} iterable is
	 *         {@code null}
	 */
	public Array<T> prepend(final Iterable<? extends T> values) {
		final int size = size(values);
		final Array<T> array = newInstance(_length + size);

		final Iterator<? extends T> it = values.iterator();
		for (int i = 0; i < size; ++i) {
			array.set(i, it.next());
		}

		for (int i = 0; i < _length; ++i) {
			array.set(size + i, get(i));
		}

		return array;
	}

	private static int size(final Iterable<?> values) {
		int size = 0;
		if (values instanceof Collection) {
			size = ((Collection<?>)values).size();
		} else {
			for (Object value : values) {
				++size;
			}
		}

		return size;
	}

	/**
	 * Return a copy of this array.
	 *
	 * @return a copy of this array
	 */
	public Array<T> copy() {
		return new Array<>(_store.copy(_start, _length + _start));
	}

	/**
	 * Return a new array slice starting with the {@code from} index.
	 *
	 * @param from the start index
	 * @return a new array slice
	 * @throws ArrayIndexOutOfBoundsException if the index is out of bounds.
	 */
	public Array<T> slice(final int from) {
		return slice(from, length());
	}

	/**
	 * Return a new array slice starting with the {@code from} index and
	 * {@code until} index.
	 *
	 * @param from the start index
	 * @param until the end index
	 * @return a new array slice
	 * @throws ArrayIndexOutOfBoundsException if the indexes are out of bounds.
	 */
	public Array<T> slice(final int from, final int until) {
		checkIndex(from, until);
		return new Array<>(_store, from + _start, until + _start);
	}

	/**
	 * Check the given array {@code index}
	 *
	 * @param index the index to check
	 * @throws ArrayIndexOutOfBoundsException if the given index is not in the
	 *         valid range.
	 */
	public void checkIndex(final int index) {
		if (index < 0 || index >= length()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", index, length()
			));
		}
	}

	/**
	 * Check the given {@code from} and {@code until} indices.
	 *
	 * @param from the from index, inclusively.
	 * @param until the until index, exclusively.
	 * @throws ArrayIndexOutOfBoundsException if the given index is not in the
	 *         valid range.
	 */
	public void checkIndex(final int from, final int until) {
		checkIndex(from, until, length());
	}

	/**
	 * Check the given {@code from} and {@code until} indices.
	 *
	 * @param from the from index, inclusively.
	 * @param until the until index, exclusively.
	 * @param size the array size
	 * @throws ArrayIndexOutOfBoundsException if the given index is not in the
	 *         valid range.
	 */
	public static void checkIndex(final int from, final int until, final int size) {
		if (from < 0) {
			throw new ArrayIndexOutOfBoundsException("fromIndex = " + from);
		}
		if (until > size) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %s)", from, until
			));
		}
		if (from > until) {
			throw new IllegalArgumentException(format(
				"fromIndex(%d) > toIndex(%d)", from, until
			));
		}
	}

	/**
	 * Create a new {@code Array} from the given object store.
	 *
	 * @param store the object store
	 * @param <T> the array type
	 * @return a new array with the given {@code store}
	 */
	public static <T> Array<T> of(final Store<T> store) {
		return new Array<>(store);
	}

	/**
	 * Create a new {@code Array} with the given length. The array is created
	 * with the <i>default</i> {@code ObjectStore} object.
	 *
	 * @param length the array length
	 * @param <T> the array type
	 * @return a new array with the given {@code length}
	 */
	public static <T> Array<T> ofLength(final int length) {
		return new Array<>(ObjectStore.ofLength(length));
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.ARRAY, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		final Store<T> store = _start == 0
			? _store._value
			: _store._value.copy(_start, _start + _length);

		out.writeBoolean(_store._sealed);
		out.writeObject(store);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		final boolean sealed = in.readBoolean();
		final Store store = (Store)in.readObject();
		final Store.Ref ref = new Ref(store, sealed);

		return new Array(ref, 0, store.length());
	}

	/**
	 * Minimal interface for accessing an underlying array structure.
	 *
	 * @param <T> the array element type
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 3.4
	 * @since 3.4
	 */
	public interface Store<T> {

		/**
		 * Write the given {@code value} at the given {@code index} to the
		 * underlying array structure.
		 *
		 * @param index the array index
		 * @param value the value to set
		 */
		void set(final int index, final T value);

		/**
		 * Return the value at the given array {@code index}.
		 *
		 * @param index the array index
		 * @return the value at the given index
		 */
		T get(final int index);

		/**
		 * Sort the store.
		 *
		 * @param from the start index where to start sorting (inclusively)
		 * @param until the end index where to stop sorting (exclusively)
		 * @param comparator the {@code Comparator} used to compare sequence
		 *        elements. A {@code null} value indicates that the elements'
		 *        Comparable natural ordering should be used
		 */
		void sort(
			final int from,
			final int until,
			final Comparator<? super T> comparator
		);

		/**
		 * Return the length of the array {@code Store}.
		 *
		 * @return the array store length
		 */
		int length();

		/**
		 * Return a new array {@code Store} with the copied portion of the
		 * underlying array.
		 *
		 * @param from the start index of the copied array
		 * @param until the end index of the copied array
		 * @return a new copy of the given range
		 */
		Store<T> copy(final int from, final int until);

		/**
		 * Return a new array {@code Store} with the copied portion of the
		 * underlying array.
		 *
		 * @param from the start index of the copied array
		 * @return a new copy of the given range
		 */
		default Store<T> copy(final int from) {
			return copy(from, length());
		}

		/**
		 * Return a new array {@code Store} copy.
		 *
		 * @return a new array {@code Store} copy
		 */
		default Store<T> copy() {
			return copy(0, length());
		}

		/**
		 * Return a new {@code Store} of the same type and the given length.
		 *
		 * @param length the length of the new store
		 * @return a new {@code Store} of the same type and the given length.
		 * @throws NegativeArraySizeException if the length is smaller than zero
		 */
		Store<T> newInstance(final int length);

		/**
		 * Mutable reference of an underlying array {@code Store}.
		 *
		 * @param <T> the array element type
		 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
		 * @version 3.4
		 * @since 3.4
		 */
		final class Ref<T> implements Store<T> {
			private Store<T> _value;
			private boolean _sealed;

			/**
			 * Private <i>primary</i> constructor.
			 *
			 * @param value the array store which is referenced
			 * @param sealed the sealed flag of the reference
			 */
			private Ref(final Store<T> value, final boolean sealed) {
				_value = value;
				_sealed = sealed;
			}

			public Ref<T> seal() {
				_sealed = true;
				return new Ref<>(_value, true);
			}

			public boolean isSealed() {
				return _sealed;
			}

			@Override
			public void set(final int index, final T value) {
				copyIfSealed();
				_value.set(index, value);
			}

			public void sort(
				final int from,
				final int until,
				final Comparator<? super T> comparator
			) {
				copyIfSealed();
				_value.sort(from, until, comparator);
			}

			void copyIfSealed() {
				if (_sealed) {
					_value = copy();
					_sealed = false;
				}
			}

			@Override
			public T get(final int index) {
				return _value.get(index);
			}

			@Override
			public int length() {
				return _value.length();
			}

			@Override
			public Store<T> copy(final int from, final int until) {
				return _value.copy(from, until);
			}

			@Override
			public Store<T> newInstance(final int length) {
				return _value.newInstance(length);
			}

			public static <T> Ref<T> of(final Store<T> value) {
				return new Ref<>(requireNonNull(value), false);
			}
		}

	}

}
