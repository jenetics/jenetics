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
package org.jenetics.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-09-16 $</em>
 */
public class SeqIteratorAdapter<T> implements Iterator<T> {

	protected final Seq<T> _seq;
	protected int _pos = 0;

	public SeqIteratorAdapter(final Seq<T> seq) {
		_seq = requireNonNull(seq, "Seq must not be null.");
	}

	@Override
	public boolean hasNext() {
		return _pos < _seq.length();
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return _seq.get(_pos++);
	}

}
