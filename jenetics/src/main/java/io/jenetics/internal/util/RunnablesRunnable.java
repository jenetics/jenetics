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
package io.jenetics.internal.util;

import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 2.0
 */
final class RunnablesRunnable implements Runnable {

	private final Seq<? extends Runnable> _runnables;
	private final int _start;
	private final int _end;

	RunnablesRunnable(
		final Seq<? extends Runnable> runnables,
		final int start,
		final int end
	) {
		_runnables = runnables;
		_start = start;
		_end = end;
	}

	@Override
	public void run() {
		for (int i = _start; i < _end; ++i) {
			_runnables.get(i).run();
		}
	}

}
