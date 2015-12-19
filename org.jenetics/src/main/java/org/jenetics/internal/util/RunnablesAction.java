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

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.RecursiveAction;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0
 * @since 2.0
 */
final class RunnablesAction extends RecursiveAction {
	private static final long serialVersionUID = 1;

	private static final int DEFAULT_THRESHOLD = 7;

	private final List<? extends Runnable> _runnables;
	private final int _high;
	private final int _low;
	private final Integer _threshold;

	private RunnablesAction(
		final List<? extends Runnable> runnables,
		final int low,
		final int high,
		final Integer threshold
	) {
		_runnables = runnables;
		_low = low;
		_high = high;
		_threshold = threshold;
	}

	public RunnablesAction(final List<? extends Runnable> runnables) {
		this(
			runnables instanceof RandomAccess ?
				runnables :
				new ArrayList<>(runnables),
			0,
			runnables.size(),
			null
		);
	}

	@Override
	protected void compute() {
		final int threshold = _threshold != null ? _threshold : threshold();

		if (_high - _low < threshold) {
			for (int i = _low; i < _high; ++i) {
				_runnables.get(i).run();
			}
		} else {
			final int mid = (_low + _high) >>> 1;
			invokeAll(
				new RunnablesAction(_runnables, _low, mid, threshold),
				new RunnablesAction(_runnables, mid, _high, threshold)
			);
		}
	}

	private int threshold() {
		return max(_runnables.size()/(Concurrency.CORES*2), DEFAULT_THRESHOLD);
	}

}
