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
package io.jenetics.internal.util;

import static java.lang.Math.max;
import static java.security.AccessController.doPrivileged;

import java.security.PrivilegedAction;
import java.util.concurrent.RecursiveAction;

import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 2.0
 */
final class RunnablesAction extends RecursiveAction {
	private static final long serialVersionUID = 1;

	private static final int DEFAULT_THRESHOLD = 7;

	private final Seq<? extends Runnable> _runnables;
	private final int _high;
	private final int _low;

	private RunnablesAction(
		final Seq<? extends Runnable> runnables,
		final int low,
		final int high
	) {
		_runnables = runnables;
		_low = low;
		_high = high;
	}

	RunnablesAction(final Seq<? extends Runnable> runnables) {
		this(runnables, 0, runnables.size());
	}

	@Override
	protected void compute() {
		if ((_high - _low) <= Env.splitThreshold ||
			getSurplusQueuedTaskCount() > Env.maxSurplusQueuedTaskCount)
		{
			for (int i = _low; i < _high; ++i) {
				_runnables.get(i).run();
			}
		} else {
			final int mid = (_low + _high) >>> 1;
			invokeAll(
				new RunnablesAction(_runnables, _low, mid),
				new RunnablesAction(_runnables, mid, _high)
			);
		}
	}

	private static final class Env {
		private static final int splitThreshold = max(
			doPrivileged(
				(PrivilegedAction<Integer>)() -> Integer.getInteger(
					"io.jenetics.concurrency.splitThreshold",
					5
				)),
			1
		);

		private static final int maxSurplusQueuedTaskCount = max(
			doPrivileged(
				(PrivilegedAction<Integer>)() -> Integer.getInteger(
					"io.jenetics.concurrency.maxSurplusQueuedTaskCount",
					3
				)),
			1
		);
	}

}
