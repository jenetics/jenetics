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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Empty {

	public static final MSeq<Object> MSEQ = new MSeq<Object>() {
		@Override
		public void set(int index, Object value) {
			throw new ArrayIndexOutOfBoundsException("Seq is empty.");
		}

		@Override
		public ListIterator<Object> listIterator() {
			return asList().listIterator();
		}

		@Override
		public MSeq<Object> subSeq(int start, int end) {
			throw new ArrayIndexOutOfBoundsException("Seq is empty.");
		}

		@Override
		public MSeq<Object> subSeq(int start) {
			throw new ArrayIndexOutOfBoundsException("Seq is empty.");
		}

		@Override
		public <B> MSeq<B> map(Function<? super Object, ? extends B> mapper) {
			return null;
		}

		@Override
		public ISeq<Object> toISeq() {
			return ISEQ;
		}

		@Override
		public MSeq<Object> copy() {
			return this;
		}

		@Override
		public Object get(int index) {
			throw new ArrayIndexOutOfBoundsException("Seq is empty.");
		}

		@Override
		public int length() {
			return 0;
		}

		@Override
		public List<Object> asList() {
			return Collections.emptyList();
		}

		@Override
		public Iterator<Object> iterator() {
			return listIterator();
		}
	};



	public static final ISeq<Object> ISEQ = new ISeq<Object>() {
		@Override
		public Iterator<Object> iterator() {
			return asList().iterator();
		}

		@Override
		public ISeq<Object> subSeq(int start, int end) {
			throw new ArrayIndexOutOfBoundsException("Seq is empty.");
		}

		@Override
		public ISeq<Object> subSeq(int start) {
			throw new ArrayIndexOutOfBoundsException("Seq is empty.");
		}

		@Override
		public Object get(int index) {
			throw new ArrayIndexOutOfBoundsException("Seq is empty.");
		}

		@Override
		public int length() {
			return 0;
		}

		@Override
		public List<Object> asList() {
			return Collections.emptyList();
		}

		@Override
		@SuppressWarnings("unchecked")
		public <B> ISeq<B> map(Function<? super Object, ? extends B> mapper) {
			return (ISeq<B>)this;
		}

		@Override
		public MSeq<Object> copy() {
			return MSEQ;
		}
	};

	@SuppressWarnings("unchecked")
	public static <T> MSeq<T> emptyMSeq() {
		return (MSeq<T>)MSEQ;
	}

	@SuppressWarnings("unchecked")
	public static <T> ISeq<T> emptyISeq() {
		return (ISeq<T>)ISEQ;
	}

}
