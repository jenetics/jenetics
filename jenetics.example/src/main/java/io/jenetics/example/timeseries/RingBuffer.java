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
package io.jenetics.example.timeseries;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A circular array buffer with a copy-and-swap cursor.
 *
 * <p>This class provides an list of T objects who's size is <em>unstable</em>.
 * It's intended for capturing data where the frequency of sampling greatly
 * outweighs the frequency of inspection (for instance, monitoring).</p>
 *
 * <p>This object keeps in memory a fixed size buffer which is used for
 * capturing objects.  It copies the objects to a snapshot array which may be
 * worked with.  The size of the snapshot array will vary based on the
 * stability of the array during the copy operation.</p>
 *
 * <p>Adding buffer to the buffer is <em>O(1)</em>, and lockless.  Taking a
 * stable copy of the sample is <em>O(n)</em>.</p>
 */
public class RingBuffer <T> {

	private static final class Cursor {
		private final int _max;

		private final AtomicInteger _index = new AtomicInteger(-1);

		private Cursor(final int max) {
			_max = max;
		}

		int next() {
			return _index.accumulateAndGet(0, this::inc);
		}

		private int inc(final int index, final int ignore) {
			return (index + 1) & (_max - 1);
		}

		int get() {
			return _index.get();
		}

		boolean isEmpty() {
			return _index.get() == -1;
		}

	}


	private final Cursor _cursor;
	private final Object[] _buffer;
	private final Class<T> type;

	/**
	 * Create a new concurrent circular buffer.
	 *
	 * @param type The type of the array.  This is captured for the same reason
	 * it's required by {@link java.util.List#toArray()}.
	 *
	 * @param size The size of the buffer.
	 *
	 * @throws IllegalArgumentException if the bufferSize is a non-positive
	 * value.
	 */
	public RingBuffer (final Class <T> type, final int size) {
		if (size < 1) {
			throw new IllegalArgumentException(
				"Buffer size must be a positive value"
			);
		}

		_cursor = new Cursor(size);

		this.type    = type;
		_buffer = new Object[size];
	}

	/**
	 * Add a new object to this buffer.
	 *
	 * <p>Add a new object to the cursor-point of the buffer.</p>
	 *
	 * @param sample The object to add.
	 */
	public void add (final T sample) {
		_buffer[_cursor.next()] = sample;
	}

	/**
	 * Return a stable snapshot of the buffer.
	 *
	 * <p>Capture a stable snapshot of the buffer as an array.  The snapshot
	 * may not be the same length as the buffer, any objects which were
	 * unstable during the copy will be factored out.</p>
	 *
	 * @return An array snapshot of the buffer.
	 */
	public T[] snapshot () {
		T[] snapshots = (T[]) new Object [ _buffer.length ];

		/* Determine the size of the snapshot by the number of affected
		 * records.  Trim the size of the snapshot by the number of records
		 * which are considered to be unstable during the copy (the amount the
		 * cursor may have moved while the copy took place).
		 *
		 * If the cursor eliminated the sample (if the sample size is so small
		 * compared to the rate of mutation that it did a full-wrap during the
		 * copy) then just treat the buffer as though the cursor is
		 * buffer.length - 1 and it was not changed during copy (this is
		 * unlikley, but it should typically provide fairly stable results).
		 */
		long before = _cursor.get();

		/* If the cursor hasn't yet moved, skip the copying and simply return a
		 * zero-length array.
		 */
		if (before == 0) {
			return (T[]) Array.newInstance(type, 0);
		}

		System.arraycopy(_buffer, 0, snapshots, 0, _buffer.length);

		long after          = _cursor.get();
		int  size           = _buffer.length - (int) (after - before);
		long snapshotCursor = before - 1;

		/* Highly unlikely, but the entire buffer was replaced while we
		 * waited...so just return a zero length array, since we can't get a
		 * stable snapshot...
		 */
		if (size <= 0) {
			return (T[]) Array.newInstance(type, 0);
		}

		long start = snapshotCursor - (size - 1);
		long end   = snapshotCursor;

		if (snapshotCursor < snapshots.length) {
			size   = (int) snapshotCursor + 1;
			start  = 0;
		}

		/* Copy the sample snapshot to a new array the size of our stable
		 * snapshot area.
		 */
		T[] result = (T[]) Array.newInstance(type, size);

		int startOfCopy = (int) (start % snapshots.length);
		int endOfCopy   = (int) (end   % snapshots.length);

		/* If the buffer space wraps the physical end of the array, use two
		 * copies to construct the new array.
		 */
		if (startOfCopy > endOfCopy) {
			System.arraycopy(snapshots, startOfCopy,
				result, 0,
				snapshots.length - startOfCopy);
			System.arraycopy(snapshots, 0,
				result, (snapshots.length - startOfCopy),
				endOfCopy + 1);
		}
		else {
			/* Otherwise it's a single continuous segment, copy the whole thing
			 * into the result.
			 */
			System.arraycopy(snapshots, startOfCopy,
				result, 0, endOfCopy - startOfCopy + 1);
		}

		return (T[]) result;
	}

	/**
	 * Get a stable snapshot of the complete buffer.
	 *
	 * <p>This operation fetches a snapshot of the buffer using the algorithm
	 * defined in {@link #snapshot()}.  If there was concurrent modification of
	 * the buffer during the copy, however, it will retry until a full stable
	 * snapshot of the buffer was acquired.</p>
	 *
	 * <p><em>Note, for very busy buffers on large symmetric multiprocessing
	 * machines and supercomputers running data processing intensive
	 * applications, this operation has the potential of being fairly
	 * expensive.  In practice on commodity hardware, dualcore processors and
	 * non-processing intensive systems (such as web services) it very rarely
	 * retries.</em></p>
	 *
	 * @return A full copy of the internal buffer.
	 */
	public T[] completeSnapshot () {
		T[] snapshot = snapshot();

		/* Try again until we get a snapshot that's the same size as the
		 * buffer...  This is very often a single iteration, but it depends on
		 * how busy the system is.
		 */
		while (snapshot.length != _buffer.length) {
			snapshot = snapshot();
		}

		return snapshot;
	}

	/**
	 * The size of this buffer.
	 */
	public int size () {
		return _buffer.length;
	}
}
