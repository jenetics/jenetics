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
package io.jenetics.util;

import java.util.AbstractList;
import java.util.Objects;

/**
 * List view of an given BaseSeq. The content is not copied on creation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
final class BaseSeqListView<T> extends AbstractList<T> {

	private final BaseSeq<T> _seq;

	BaseSeqListView(final BaseSeq<T> seq) {
		_seq = Objects.requireNonNull(seq);
	}

	@Override
	public int size() {
		return _seq.length();
	}

	@Override
	public T get(final int index) {
		return _seq.get(index);
	}

}
