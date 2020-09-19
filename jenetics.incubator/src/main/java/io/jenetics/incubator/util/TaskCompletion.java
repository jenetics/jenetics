package io.jenetics.incubator.util;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

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

	private boolean taskSubmitted = false;

	/**
	 * Creates a new task-completion object with the given parameter.
	 *
	 * @param executor the executor service used for the asynchronous task
	 *        execution
	 * @param taskQueueSize the maximum allowed number of tasks which are
	 *        waiting for submission to the <i>executor</i>
	 */
	public TaskCompletion(final Executor executor, final int taskQueueSize) {
		_executor = requireNonNull(executor);
		_taskQueueSize = taskQueueSize;
		_tasks = new LinkedBlockingQueue<>(_taskQueueSize);
	}

	/**
	 * Creates a new task-completion object with the given {@code executor} and
	 * a task queue size of {@link #DEFAULT_TASK_QUEUE_SIZE}.
	 *
	 * @param executor the executor service used for the asynchronous task
	 *        execution
	 */
	public TaskCompletion(final Executor executor) {
		this(executor, DEFAULT_TASK_QUEUE_SIZE);
	}

	/**
	 *  Executes the given <i>block</i> asynchronously. The method will return
	 * immediately after, except the <i>executorQueueSize</i> is exhausted. Then
	 * this call will block until an other task has finished or the specified
	 * waiting time has expired.
	 *
	 * @param timeout the maximal time to wait for a place in the task queue. If
	 *        waiting time has elapsed, and RejectedExecutionException is thrown.
	 * @param block the code block to execute.
	 * @throws RejectedExecutionException if the <i>executor</i> has been shut
	 *         down or the executor queue was full and the maximal waiting time
	 *         is elapsed.
	 * @throws InterruptedException if the calling thread is interrupted while
	 *         waiting for a place in the executor queue.
	 */
	public void submit(final Duration timeout, final Runnable block)
		throws InterruptedException
	{
		submit(
			() -> {
				try {
					block.run();
				} finally {
					taskFinished();
				}
			},
			timeout
		);
	}

	/**
	 * Executes the given <i>block</i> asynchronously. The method will return
	 * immediately after, except the <i>executorQueueSize</i> is exhausted. Then
	 * this call will block until an other task has finished or a predefined
	 * timeout occurred.
	 *
	 * @param block the code block to execute.
	 * @throws RejectedExecutionException if the <i>executor</i> has been shut
	 *         down or the executor queue was full and the maximal waiting time
	 *         is elapsed.
	 * @throws InterruptedException if the calling thread is interrupted while
	 *         waiting for a place in the executor queue.
	 */
	public void submit(final Runnable block) throws InterruptedException {
		submit(DEFAULT_ASYNC_TIMEOUT, block );
	}

	private void submit(final Runnable block, final Duration timeout)
		throws InterruptedException
	{
		synchronized (_lock) {
			if (_tasks.offer(block, timeout.toMillis(), TimeUnit.MILLISECONDS)) {
				if (!taskSubmitted) {
					final var task = _tasks.poll();

					if (task != null) {
						_executor.execute(task);
						taskSubmitted = true;
					}
				}
			} else {
				throw new RejectedExecutionException(format(
					"Task queue size (%d) was full", _taskQueueSize
				));
			}
		}
	}

	private void taskFinished() {
		synchronized (_lock) {
			final var task = _tasks.poll();

			if (task != null) {
				_executor.execute(task);
				taskSubmitted = true;
			} else {
				taskSubmitted = false;
			}
		}
	}

}
