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
package io.jenetics.incubator.util;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public class TaskCompletion {

	/**
	 * The default timeout for waiting for a free place in the task queue:
	 * 5 min.
	 */
	public static final Duration DEFAULT_ASYNC_TIMEOUT = Duration.ofMinutes(5);

	/**
	 * The default task queue size, set to 1000.
	 */
	public static final int DEFAULT_TASK_QUEUE_SIZE = 1000;

	private final Object _lock = new Object() {};

	private final Executor _executor;
	private final int _taskQueueSize;
	private final BlockingQueue<Runnable> _tasks;

	private boolean _submitted = false;

	/**
	 * Creates a new task-completion object with the given parameter.
	 *
	 * @param executor the executor service used for the asynchronous task
	 *        execution
	 * @param taskQueueSize the maximum allowed number of tasks which are
	 *        waiting for submission to the <i>executor</i>
	 * @throws NullPointerException if the given {@code executor} is {@code null}
	 * @throws IllegalArgumentException if the {@code taskQueueSize} is smaller
	 *         than one
	 */
	public TaskCompletion(final Executor executor, final int taskQueueSize) {
		_executor = requireNonNull(executor);
		_taskQueueSize = taskQueueSize;
		_tasks = new ArrayBlockingQueue<>(_taskQueueSize, true);
	}

	/**
	 * Creates a new task-completion object with the given {@code executor} and
	 * a task queue size of {@link #DEFAULT_TASK_QUEUE_SIZE}.
	 *
	 * @param executor the executor service used for the asynchronous task
	 *        execution
	 * @throws NullPointerException if the given {@code executor} is {@code null}
	 */
	public TaskCompletion(final Executor executor) {
		this(executor, DEFAULT_TASK_QUEUE_SIZE);
	}

	/**
	 * Return the maximal size of the task queue.
	 *
	 * @return the maximal size of the task queue
	 */
	public int taskQueueSize() {
		return _taskQueueSize;
	}

	/**
	 * Return the number of currently <em>waiting</em> tasks in the task queue.
	 *
	 * @return the number of currently <em>waiting</em> tasks in the task queue
	 */
	public int taskSize() {
		return _tasks.size();
	}

	/**
	 *  Executes the given <i>block</i> asynchronously. The method will return
	 * immediately after, except the <i>executorQueueSize</i> is exhausted. Then
	 * this call will block until an other task has finished or the specified
	 * waiting time has expired.
	 *
	 * @param block the code block to execute.
	 * @param timeout the maximal time to wait for a place in the task queue. If
	 *        waiting time has elapsed, and RejectedExecutionException is thrown.
	 * @return {@code true} if the given {@code block} where successfully
	 *         submitted or {@code false} otherwise. The submission is rejected
	 *         if the <i>executor</i> has been shut down or the executor queue
	 *         was full and the maximal waiting time is elapsed.
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws InterruptedException if the calling thread is interrupted while
	 *         waiting for a place in the executor queue.
	 */
	public boolean submit(final Runnable block, final Duration timeout)
		throws InterruptedException
	{
		requireNonNull(timeout);
		requireNonNull(block);

		final Runnable task = () -> {
			try {
				block.run();
			} finally {
				taskFinished();
			}
		};

		return submit0(task, timeout);
	}

	/**
	 * Executes the given <i>block</i> asynchronously. The method will return
	 * immediately after, except the <i>executorQueueSize</i> is exhausted. Then
	 * this call will block until an other task has finished or a predefined
	 * timeout occurred.
	 *
	 * @param block the code block to execute.
	 * @return {@code true} if the given {@code block} where successfully
	 *         submitted or {@code false} otherwise. The submission is rejected
	 *         if the <i>executor</i> has been shut down or the executor queue
	 *         was full and the maximal waiting time is elapsed.
	 * @throws InterruptedException if the calling thread is interrupted while
	 *         waiting for a place in the executor queue.
	 */
	public boolean submit(final Runnable block) throws InterruptedException {
		return submit(block, DEFAULT_ASYNC_TIMEOUT);
	}

	private boolean submit0(final Runnable task, final Duration timeout)
		throws InterruptedException
	{
		synchronized (_lock) {
			if (_tasks.offer(task, timeout.toMillis(), MILLISECONDS)) {
				if (!_submitted) {
					final var runnable = _tasks.poll();

					if (runnable != null) {
						_executor.execute(runnable);
						_submitted = true;
					}
				}

				return true;
			} else {
				return false;
			}
		}
	}

	private void taskFinished() {
		synchronized (_lock) {
			final var runnable = _tasks.poll();

			if (runnable != null) {
				_executor.execute(runnable);
				_submitted = true;
			} else {
				_submitted = false;
			}
		}
	}

}
