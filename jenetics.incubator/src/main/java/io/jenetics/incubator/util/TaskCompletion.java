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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * This execution service executes the tasks in exactly the same order as they
 * were submitted. The tasks are executed <em>asynchronously</em>, but <b>not</b>
 * <em>concurrently</em>.
 *
 * <pre>{@code
 * final var executor = new TaskCompletion(ForkJoinPool.commonPool());
 * final var results = new ArrayList<Integer>();
 *
 * for (int i = 0; i < 100; ++i) {
 *     final int index = i;
 *     executor.execute(() -> results.add(i));
 * }
 *
 * executor.shutdown();
 * executor.awaitTermination(10, SECONDS);
 *
 * for (int i = 0; i < results.size(); ++i) {
 *     assert results.get(i) == i;
 * }
 * }</pre>
 *
 * Since the tasks are executed in the submitted order and the next task is
 * executed if the previous one has been finished, it is not necessary to use
 * <em>synchronized</em>/thread-safe objects.
 * If you run the code above with a <em>normal</em> executor service, it will
 * fail, since the order, in which the submitted tasks are executed, is not
 * defined.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.3
 * @version 6.3
 */
public final class TaskCompletion extends AbstractExecutorService {

	/**
	 * Wrapper class for the <em>runnable</em> to be executed.
	 */
	private static final class Task implements Runnable {
		private final Runnable _task;
		private final Runnable _finished;

		Task(
			final Runnable task,
			final Runnable finished
		) {
			_task = requireNonNull(task);
			_finished = requireNonNull(finished);
		}

		@Override
		public void run() {
			try {
				_task.run();
			} catch (Throwable e) {
				final var thread = Thread.currentThread();
				thread.getUncaughtExceptionHandler().uncaughtException(thread, e);
			} finally {
				_finished.run();
			}
		}
	}

	/**
	 * The default task queue size, set to 1000.
	 */
	private static final int DEFAULT_TASK_QUEUE_SIZE = 1_000;

	private final Executor _executor;
	private final BlockingQueue<Task> _tasks;
	private final int _capacity;

	private final ReentrantLock _lock = new ReentrantLock();
	private final Condition _terminated = _lock.newCondition();
	private boolean _running = false;
	private boolean _shutdown = false;

	/**
	 * Creates a new task-completion object with the given parameter.
	 *
	 * @param executor the executor service used for the asynchronous task
	 *        execution
	 * @param capacity the maximum allowed number of tasks which are
	 *        waiting for submission to the <i>executor</i>
	 * @throws NullPointerException if the given {@code executor} is {@code null}
	 * @throws IllegalArgumentException if the {@code capacity} is smaller
	 *         than one
	 */
	public TaskCompletion(final Executor executor, final int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException(format(
				"Capacity must be greater then 0: %s.", capacity
			));
		}

		_executor = requireNonNull(executor);
		_capacity = capacity;
		_tasks = new LinkedBlockingQueue<>(_capacity);
	}

	/**
	 * Creates a new task-completion object with the given {@code executor}.
	 *
	 * @param executor the executor service used for the asynchronous task
	 *        execution
	 * @throws NullPointerException if the given {@code executor} is {@code null}
	 */
	public TaskCompletion(final Executor executor) {
		this(executor, DEFAULT_TASK_QUEUE_SIZE);
	}

	/**
	 * Create a new task-completion object with the
	 * {@link ForkJoinPool#commonPool()} as executor service.
	 */
	public TaskCompletion() {
		this(ForkJoinPool.commonPool(), DEFAULT_TASK_QUEUE_SIZE);
	}

	/**
	 * Return the maximal size of the task queue.
	 *
	 * @return the maximal size of the task queue
	 */
	public int capacity() {
		return _capacity;
	}

	/**
	 * Return the number of currently queued tasks.
	 *
	 * @return the number of currently queued tasks
	 */
	public int size() {
		return _tasks.size();
	}

	/**
	 * Return {@code true} if there are no queue tasks available in {@code this}
	 * task completion, {@code false} otherwise.
	 *
	 * @return {@code true} if there are no queue tasks available in {@code this}
	 * 	       task completion, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return _tasks.size() == 0;
	}

	/**
	 * Return the list of currently queued tasks.
	 *
	 * @return the list of currently queued tasks
	 */
	public List<Runnable> tasks() {
		return Stream.of(_tasks.toArray(Task[]::new))
			.map(t -> t._task)
			.toList();
	}

	/**
	 * Removes all pending tasks from the queue and adds them to the given
	 * {@code collection}.
	 *
	 * @param collection the collection to transfer elements into
	 * @return the number of elements transferred
	 * @throws NullPointerException if the specified {@code collection} is
	 *         {@code null}
	 */
	public int drainTo(final Collection<? super Runnable> collection) {
		final var tasks = new ArrayList<Task>();
		final int drained = _tasks.drainTo(tasks);
		tasks.forEach(t -> collection.add(t._task));
		return drained;
	}

	/**
	 * Removes all of the {@code tasks}. The task completion will be empty after
	 * this method returns.
	 */
	public void clear() {
		_tasks.clear();
	}


	/* *************************************************************************
	 * Task executor methods.
	 * ************************************************************************/

	/**
	 * Enqueues the given {@code command} to the task queue. The method will
	 * return immediately after, except the {@link #capacity()} of the queue is
	 * exhausted. Then this call will block until an other task has finished or
	 * the specified waiting time has expired.
	 *
	 * @param command the code block to execute.
	 * @param timeout the maximal time to wait for a place in the task queue. If
	 *        waiting time has elapsed, and RejectedExecutionException is thrown.
	 * @return {@code true} if the given {@code command} where successfully
	 *         submitted or {@code false} otherwise. The submission is rejected
	 *         if the <i>executor</i> has been shut down or the executor queue
	 *         was full and the maximal waiting time is elapsed.
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws RejectedExecutionException if the task cannot be scheduled for
	 *         execution, because {@code this} task completion has been shut
	 *         down
	 * @throws InterruptedException if the calling thread is interrupted while
	 *         waiting for a place in the executor queue.
	 */
	public boolean enqueue(final Runnable command, final Duration timeout)
		throws InterruptedException
	{
		requireNonNull(command);
		requireNonNull(timeout);
		checkShutdown();

		final var task = new Task(command, this::finished);
		final boolean submitted = _tasks.offer(task, timeout.toNanos(), NANOSECONDS);
		execute();
		return submitted;
	}

	private void checkShutdown() {
		if (isShutdown()) {
			throw new RejectedExecutionException(
				"TaskCompletion has been shutdown, no task submission possible."
			);
		}
	}

	/**
	 * Enqueues the given {@code command} to the task queue. The method will
	 * return immediately without waiting in the case of an exhausted task queue.
	 * Return {@code true} if the task has been successfully submitted,
	 * {@code false} otherwise.
	 *
	 * @param command the code block to execute.
	 * @return {@code true} if the given {@code command} where successfully
	 *         submitted or {@code false} otherwise. The submission is rejected
	 *         if the <i>executor</i> has been shut down or the executor queue
	 *         was full and the maximal waiting time is elapsed.
	 * @throws RejectedExecutionException if the task cannot be scheduled for
	 *         execution, because {@code this} task completion has been shut
	 *         down
	 * @throws NullPointerException if the given {@code command} is {@code null}
	 */
	public boolean enqueue(final Runnable command) {
		requireNonNull(command);
		checkShutdown();

		final var task = new Task(command, this::finished);
		final var submitted = _tasks.offer(task);
		execute();
		return submitted;
	}

	private void execute() {
		_lock.lock();
		try {
			if (!_running) {
				execute0();
			}
		} finally {
			_lock.unlock();
		}
	}

	private void execute0() {
		final var task = _tasks.poll();
		if (task != null) {
			_executor.execute(task);
			_running = true;
		}
	}

	private void finished() {
		_lock.lock();
		try {
			_running = false;
			execute0();
			_terminated.signal();
		} finally {
			_lock.unlock();
		}
	}

	@Override
	public void execute(final Runnable command) {
		if (!enqueue(command)) {
			throw new RejectedExecutionException(format(
				"Command not accepted, capacity of %d is exhausted.",
				capacity()
			));
		}
	}

	/**
	 * Initiates an orderly shutdown in which previously submitted tasks are
	 * executed, but no new tasks will be accepted. Invocation has no additional
	 * effect if already shut down. The underlying {@link Executor} is not
	 * affected by this shutdown and is still usable after shutting down
	 * {@code this} task completion.
	 * <p>
	 * This method does not wait for previously submitted tasks to complete
	 * execution. Use {@link #awaitTermination awaitTermination} to do that.
	 */
	@Override
	public void shutdown() {
		_lock.lock();
		try {
			_shutdown = true;
			_terminated.signal();
		} finally {
			_lock.unlock();
		}
	}

	@Override
	public List<Runnable> shutdownNow() {
		final var drained = new ArrayList<Runnable>();

		_lock.lock();
		try {
			drainTo(drained);
			_shutdown = true;
			_terminated.signal();
		} finally {
			_lock.unlock();
		}

		return List.copyOf(drained);
	}

	/**
	 * Returns {@code true} if this task completion has been shut down.
	 *
	 * @return {@code true} if this task completion has been shut down
	 */
	@Override
	public boolean isShutdown() {
		_lock.lock();
		try {
			return _shutdown;
		} finally {
			_lock.unlock();
		}
	}

	@Override
	public boolean isTerminated() {
		_lock.lock();
		try {
			return isFinished();
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * Blocks until all tasks have completed execution after a shutdown request,
	 * or the timeout occurs, or the current thread is interrupted, whichever
	 * happens first.
	 *
	 * @param timeout the maximum time to wait
	 * @param unit the time unit of the timeout argument
	 * @return {@code true} if this executor terminated and
	 *         {@code false} if the timeout elapsed before termination
	 * @throws InterruptedException if interrupted while waiting
	 */
	@Override
	public boolean awaitTermination(final long timeout, final TimeUnit unit)
		throws InterruptedException
	{
		long remainingNanos = unit.toNanos(timeout);

		_lock.lock();
		try {
			while (!isFinished()) {
				if (remainingNanos <= 0L) {
					return false;
				}
				remainingNanos = _terminated.awaitNanos(remainingNanos);
			}

			return true;
		} finally {
			_lock.unlock();
		}
	}

	private boolean isFinished() {
		return _shutdown && !_running && isEmpty();
	}

}
