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

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Batch executor interface, which is used for evaluating a <em>batch</em> of
 * runnables. The tasks of a batch are executed concurrently and the
 * {@link #execute(BaseSeq)} method will return, if all tasks of the batch have
 * been executed.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
@FunctionalInterface
public interface BatchExecutor {

	/**
	 * Executes the runnables of the {@code batch} concurrently and returns,
	 * when all tasks have been executed.
	 *
	 * @param batch the sequence of runnable to be executed concurrently
	 * @throws NullPointerException if the given {@code batch} is {@code null}
	 */
	void execute(final BaseSeq<? extends Runnable> batch);

	/**
	 * Create a batch executor, where the execution is forwarded to the given
	 * {@code executor}.
	 *
	 * @param executor the executor, which is actually executing the tasks
	 * @return a new batch executor
	 * @throws NullPointerException if the given {@code executor} is {@code null}
	 */
	static BatchExecutor of(final Executor executor) {
		requireNonNull(executor);

		if (executor instanceof ForkJoinPool pool) {
			return new BatchForkJoinPool(pool);
		} else {
			return new PartitionBatchExecutor(executor);
		}
	}

	/**
	 * Return a batch executor, where each task of a given <em>batch</em> is
	 * executed in its own <em>virtual</em> thread.
	 *
	 * @return a new <em>virtual</em> thread batch executor object
	 */
	static BatchExecutor ofVirtualThreads() {
		return batch -> {
			try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
				batch.forEach(executor::execute);
			}
		};
	}

}
