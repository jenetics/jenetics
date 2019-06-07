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
package io.jenetics.prog.regression;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
final class Samples<T> extends AbstractList<Sample<T>> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final List<Sample<T>> _samples;

	private final T[][] _arguments;
	private final T[] _results;

	@SuppressWarnings("unchecked")
	Samples(final List<Sample<T>> samples) {
		_samples = samples;

		_arguments = (T[][])_samples.stream()
			.map(Sample::args)
			.toArray();

		_results = (T[])_samples.stream()
			.map(Sample::result)
			.toArray();
	}

	T[][] arguments() {
		return _arguments;
	}

	T[] results() {
		return _results;
	}

	@Override
	public Sample<T> get(int index) {
		return _samples.get(index);
	}

	@Override
	public int size() {
		return _samples.size();
	}

}
