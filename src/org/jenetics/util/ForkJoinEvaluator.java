/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.util;

import static org.jenetics.util.Validator.nonNull;

import java.util.List;
import java.util.RandomAccess;

import jsr166y.ForkJoinPool;
import jsr166y.RecursiveAction;

/**
 * This evaluator uses the (preliminar) version of the fork-join framework from
 * Doug Lea. The fork-join framework will be included in the upcomming JDK 
 * version 1.7.
 * 
 * @see <a href="http://gee.cs.oswego.edu/dl/jsr166/dist/jsr166ydocs/">Fork-join framework javadoc</a>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ForkJoinEvaluator.java,v 1.8 2010-01-28 19:34:14 fwilhelm Exp $
 */
public class ForkJoinEvaluator implements Evaluator {
	public static final int DEFAULT_TASK_SIZE = 4;
	
	private final int _taskSize;
	private final ForkJoinPool _pool;
	
	/**
	 * Create a new ForkJoinEvaluator with the given ForjJoinPool. The task size
	 * is set to {@value #DEFAULT_TASK_SIZE}.
	 * 
	 * @param pool the executor service (fork-join pool).
	 * @throws NullPointerException if the given pool is {@code null}.
	 */
	public ForkJoinEvaluator(final ForkJoinPool pool) {
		this(pool, DEFAULT_TASK_SIZE);
	}
	
	/**
	 * Create a new ForkJoinEvaluator with the given ForjJoinPool. The task size
	 * determines the maximal number of runnables that will be executed by one
	 * {@link RecursiveAction}.
	 * 
	 * @param pool the executor service (fork-join pool).
	 * @param taskSize the maximal number of runnables that will be executed by
	 *        one {@link RecursiveAction}.
	 * @throws NullPointerException if the given pool is {@code null}.
	 */
	public ForkJoinEvaluator(final ForkJoinPool pool, final int taskSize) {
		if (taskSize < 1) {
			throw new IllegalArgumentException(String.format(
					"Task size is smaller than one: %s.", taskSize
				));
		}
		_pool = nonNull(pool, "Thread pool");
		_taskSize = taskSize;
	}
	
	@Override
	public void evaluate(final List<? extends Runnable> runnables) {
		nonNull(runnables, "Runnables");
		RecursiveAction action = null;
		if (runnables instanceof RandomAccess) {
			action = new EvaluatorTask(runnables, 0, runnables.size(), _taskSize);
		} else {
			action = new SequentialEvaluatorTask(runnables, 0, runnables.size(), _taskSize);
		}
		_pool.invoke(action);
	}

	@Override
	public int getParallelTasks() {
		return _pool.getParallelism();
	}
	
	
	private static class EvaluatorTask extends RecursiveAction {
		private static final long serialVersionUID = -7886596400215187705L;
		
		protected final List<? extends Runnable> _runnables;
		protected final int _from;
		protected final int _to;
		protected final int _taskSize;
		
		EvaluatorTask(
			final List<? extends Runnable> runnables, 
			final int from, 
			final int to, 
			final int taskSize
		) {
			assert (runnables != null);
			_runnables = runnables;
			_from = from;
			_to = to;
			_taskSize = taskSize;
		}
		
		@Override
		protected void compute() {
			if (_to - _from <= _taskSize) {
				eval();
			} else {
				final int mid = (_from + _to) >>> 1;
				invokeAll(
						newTask(_runnables, _from, mid, _taskSize), 
						newTask(_runnables, mid, _to, _taskSize)
					);
			}
		}
		
		protected void eval() {
			for (int i = _from; i < _to; ++i) {
				_runnables.get(i).run();
			}
		}
		
		protected RecursiveAction newTask(
			final List<? extends Runnable> runnables, 
			final int from, 
			final int to, 
			final int taskSize
		) {
			return new EvaluatorTask(runnables, from, to, taskSize);
		}
	}
	
	private static class SequentialEvaluatorTask extends EvaluatorTask {
		private static final long serialVersionUID = 6588074688828349438L;

		SequentialEvaluatorTask(
			final List<? extends Runnable> runnables, 
			final int from,
			final int to, 
			final int taskSize
		) {
				super(runnables, from, to, taskSize);
		}
		
		@Override
		protected void eval() {
			for (Runnable runnable : _runnables.subList(_from, _to)) {
				runnable.run();
			}
		}
		
		@Override
		protected RecursiveAction newTask(
			final List<? extends Runnable> runnables, 
			final int from, 
			final int to, 
			final int taskSize
		) {
			return new SequentialEvaluatorTask(runnables, from, to, taskSize);
		}
		
	}

}
