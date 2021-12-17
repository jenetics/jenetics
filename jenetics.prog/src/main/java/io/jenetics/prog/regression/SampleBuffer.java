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

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * This class holds the actual sample values which are used for the symbolic
 * regression example. This class is <em>thread-safe</em> and can be used in a
 * <em>producer-consumer</em> setup. You can add single sample values
 * ({@link #add(Sample)}) or a list ({@link #addAll(Collection)}) of new values.
 * These values will be made available for evaluation after an explicit call of
 * the {@link #publish()} method.
 *
 * @implNote
 * This class is thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
public final class SampleBuffer<T> implements Sampling<T> {

	private final RingBuffer _buffer;

	private volatile SampleList<T> _snapshot = null;

	public SampleBuffer(final int capacity) {
		_buffer = new RingBuffer(capacity);
	}

	/**
	 * Adding a new sample point to the buffer. <em>You need to explicitly
	 * call {@link #publish()} to make it available for the {@link #eval(Tree)}
	 * method.</em>
	 *
	 * @param sample the sample point to add
	 * @throws NullPointerException if the given {@code sample} point is
	 *         {@code null}
	 */
	public void add(final Sample<T> sample) {
		_buffer.add(requireNonNull(sample));
	}

	/**
	 * The the given sample points to the buffer.  <em>You need to explicitly
	 * call {@link #publish()} to make it available for the {@link #eval(Tree)}
	 * method.</em>
	 *
	 * @param samples the samples to add to the buffer
	 * @throws NullPointerException if the given {@code samples} is {@code null}
	 */
	public void addAll(final Collection<? extends Sample<T>> samples) {
		samples.forEach(Objects::requireNonNull);
		_buffer.addAll(samples);
	}

	/**
	 * Making the current sample points available for the {@link #eval(Tree)}
	 * function.
	 *
	 * @return the number of <em>published</em> sample points
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public int publish() {
		final Object[] values = _buffer.snapshot();

		SampleList<T> snapshot = null;
		if (values != null && values.length > 0) {
			final List samples = Arrays.asList(values);
			snapshot = new SampleList(samples);
		}

		try {
			return snapshot != null ? snapshot.size() : 0;
		} finally {
			_snapshot = snapshot;
		}
	}

	/**
	 * Return the currently <em>published</em> sample points.
	 *
	 * @see #publish()
	 *
	 * @return the currently <em>published</em> sample points
	 */
	List<Sample<T>> samples() {
		final SampleList<T> snapshot = _snapshot;
		return snapshot != null ? snapshot : List.of();
	}

	@Override
	public Result<T> eval(final Tree<? extends Op<T>, ?> program) {
		requireNonNull(program);

		final SampleList<T> snapshot = _snapshot;
		return snapshot != null && !snapshot.isEmpty()
			? snapshot.eval(program)
			: null;
	}

}
