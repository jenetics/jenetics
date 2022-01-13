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

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.SerialIO.readCharArray;
import static io.jenetics.internal.util.SerialIO.writeCharArray;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 6.0
 */
public final class CharStore implements Array.Store<Character>, Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	public final char[] array;

	private CharStore(final char[] chars) {
		array = requireNonNull(chars);
	}

	public CharStore(final int length) {
		this(new char[length]);
	}

	@Override
	public Character get(final int index) {
		return array[index];
	}

	@Override
	public void sort(
		final int from,
		final int until,
		final Comparator<? super Character> comparator
	) {
		if (comparator == null) {
			Arrays.sort(array, from, until);
		} else {
			final Character[] chars = new Character[array.length];
			for (int i = 0; i < array.length; ++i) {
				chars[i] = array[i];
			}
			Arrays.sort(chars, from, until, comparator);
			for (int i = 0; i < array.length; ++i) {
				array[i] = chars[i];
			}
		}
	}

	@Override
	public void set(final int index, final Character value) {
		array[index] = value;
	}

	@Override
	public CharStore copy(final int from, final int until) {
		final char[] array = new char[until - from];
		System.arraycopy(this.array, from, array, 0, until - from);
		return new CharStore(array);
	}

	@Override
	public CharStore newInstance(final int length) {
		return new CharStore(length);
	}

	@Override
	public int length() {
		return array.length;
	}

	public static CharStore of(final char[] chars) {
		return new CharStore(chars);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.CHAR_STORE, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		writeCharArray(array, out);
	}

	static CharStore read(final ObjectInput in) throws IOException {
		return new CharStore(readCharArray(in));
	}

}
