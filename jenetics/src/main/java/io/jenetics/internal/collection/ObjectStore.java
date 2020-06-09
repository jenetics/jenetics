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

import static java.util.Arrays.copyOfRange;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.SerialIO.readObjectArray;
import static io.jenetics.internal.util.SerialIO.writeObjectArray;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import io.jenetics.internal.collection.Array.Store;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
public final class ObjectStore<T> implements Store<T>, Serializable {

	private static final long serialVersionUID = 2L;

	private final Object[] _array;

	private ObjectStore(final Object[] array) {
		_array = requireNonNull(array);
	}

	@Override
	public void set(final int index, T value) {
		_array[index] = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		return (T)_array[index];
	}

	@Override
	@SuppressWarnings("unchecked")
	public void sort(
		final int from,
		final int until,
		final Comparator<? super T> comparator
	) {
		Arrays.sort((T[])_array, from, until, comparator);
	}

	@Override
	public ObjectStore<T> copy(final int from, final int until) {
		return new ObjectStore<>(copyOfRange(_array, from, until));
	}

	@Override
	public ObjectStore<T> newInstance(final int length) {
		return ofLength(length);
	}

	@Override
	public int length() {
		return _array.length;
	}

	public static <T> ObjectStore<T> of(final Object[] array) {
		return new ObjectStore<>(array);
	}

	public static <T> ObjectStore<T> ofLength(final int length) {
		return new ObjectStore<>(new Object[length]);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.OBJECT_STORE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		writeObjectArray(_array, out);
	}

	@SuppressWarnings("rawtypes")
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		return new ObjectStore(readObjectArray(in));
	}

}
