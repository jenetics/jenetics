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
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Asynchronously executes <em>submitted</em> tasks in the given submission
 * order.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.3
 * @version 6.3
 */
public class TaskCompletion {

	/**
	 * Interface for handlers invoked when a <em>task</em> abruptly terminates
	 * due to an uncaught exception or when the execution of a task is rejected
	 * with a {@link RejectedExecutionException}.
	 *
	 * @see RejectedExecutionException
	 */
	@FunctionalInterface
	public interface ExceptionHandler {

		/**
		 * Method invoked when the executed task terminates due to the given
		 * uncaught exception or when the execution of a task is rejected with a
		 * {@link RejectedExecutionException}.
		 *
		 * @param error the exception thrown
		 * @param task the <em>task</em>, which caused the error
		 */
		public void handle(final Throwable error, final Runnable task);

	}

	/**
	 * Wrapper class for the <em>runnable</em> to be executed.
	 */
	private final class Task implements Runnable {
		private final Runnable _runnable;

		Task(final Runnable runnable) {
			_runnable = requireNonNull(runnable);
		}

		@Override
		public void run() {
			try {
				_runnable.run();
			} catch (Throwable e) {
				handleException(e, _runnable);
			} finally {
				_running.set(false);
				executeNextTask();
			}
		}
	}

	/**
	 * The default task queue size, set to 1000.
	 */
	public static final int DEFAULT_TASK_QUEUE_SIZE = 1000;

	private final Executor _executor;
	private final BlockingQueue<Task> _tasks;
	private final int _capacity;

	private final ReentrantLock _lock = new ReentrantLock();

	private final AtomicReference<ExceptionHandler> _exceptionHandler =
		new AtomicReference<>((e, r) -> {});

	private final AtomicBoolean _running = new AtomicBoolean(false);

	/**
	 * Creates a new task-completion object with the given parameter.
	 *
	 * @param executor the executor service used for the asynchronous task
	 *        execution
	 * @param capacity the maximum allowed number of tasks which are
	 *        waiting for submission to the <i>executor</i>
	 * @throws NullPointerException if the given {@code executor} is {@code null}
	 * @throws IllegalArgumentException if the {@code taskQueueSize} is smaller
	 *         than one
	 */
	public TaskCompletion(final Executor executor, final int capacity) {
		_executor = requireNonNull(executor);
		_capacity = capacity;
		_tasks = new ArrayBlockingQueue<>(_capacity, true);
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
		return _capacity;
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
	 * Return the list currently queued tasks.
	 *
	 * @return the list of currently queued tasks
	 */
	public List<Runnable> queuedTasks() {
		return Stream.of(_tasks.toArray(Task[]::new))
			.map(t -> t._runnable)
			.collect(Collectors.toUnmodifiableList());
	}

	public void drainTo(final Collection<? super Runnable> collection) {
		final var tasks = new ArrayList<Task>();
		_tasks.drainTo(tasks);
		tasks.forEach(t -> collection.add(t._runnable));
	}

	public void clear() {
		_tasks.clear();
	}

	public void setExceptionHandler(final ExceptionHandler handler) {
		_exceptionHandler.set(requireNonNull(handler));
	}

	/* *************************************************************************
	 * Task submission methods.
	 * ************************************************************************/

	/**
	 *  Executes the given {@code runnable} asynchronously. The method will return
	 * immediately after, except the <i>executorQueueSize</i> is exhausted. Then
	 * this call will block until an other task has finished or the specified
	 * waiting time has expired.
	 *
	 * @param runnable the code block to execute.
	 * @param timeout the maximal time to wait for a place in the task queue. If
	 *        waiting time has elapsed, and RejectedExecutionException is thrown.
	 * @return {@code true} if the given {@code runnable} where successfully
	 *         submitted or {@code false} otherwise. The submission is rejected
	 *         if the <i>executor</i> has been shut down or the executor queue
	 *         was full and the maximal waiting time is elapsed.
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws InterruptedException if the calling thread is interrupted while
	 *         waiting for a place in the executor queue.
	 */
	public boolean submit(final Runnable runnable, final Duration timeout)
		throws InterruptedException
	{
		final var offered = _tasks.offer(
			new Task(runnable),
			timeout.toNanos(), NANOSECONDS
		);
		if (offered) {
			executeNextTask();
		}
		return offered;
	}

	/**
	 * Executes the given {@code runnable} asynchronously. The method will return
	 * immediately without waiting in the case of an exhausted task queue. Return
	 * {@code true} if the task has been successfully submitted, {@code false}
	 * otherwise.
	 *
	 * @param runnable the code block to execute.
	 * @return {@code true} if the given {@code runnable} where successfully
	 *         submitted or {@code false} otherwise. The submission is rejected
	 *         if the <i>executor</i> has been shut down or the executor queue
	 *         was full and the maximal waiting time is elapsed.
	 */
	public boolean submit(final Runnable runnable) {
		final var offered = _tasks.offer(new Task(runnable));
		if (offered) {
			executeNextTask();
		}
		return offered;
	}

	private void executeNextTask() {
		_lock.lock();
		try {
			if (!_running.get()) {
				final var task = _tasks.poll();
				if (task != null) {
					try {
						_executor.execute(task);
						_running.set(true);
					} catch (RejectedExecutionException e) {
						handleException(e, task._runnable);
					}
				}
			}
		} finally {
			_lock.unlock();
		}
	}

	private void handleException(final Throwable e, final Runnable r) {
		_exceptionHandler.get().handle(e, r);
	}

}
