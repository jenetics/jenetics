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

import java.util.List;
import java.util.RandomAccess;

import jsr166y.ForkJoinPool;
import jsr166y.RecursiveAction;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ForkJoinEvaluator.java,v 1.3 2010-01-12 15:58:26 fwilhelm Exp $
 */
public class ForkJoinEvaluator implements Evaluator {
	private static final int DEFAULT_TASK_SIZE = 5;
	
	private final int _taskSize;
	private final ForkJoinPool _pool;
	
	/**
	 * Create a threaded evaluator object where the number of concurrent threads
	 * is equal to the number of available cores.
	 * 
	 * @param pool the executor service (thread pool).
	 * @throws NullPointerException if the given thread pool is {@code null}.
	 */
	public ForkJoinEvaluator(final ForkJoinPool pool) {
		this(pool, DEFAULT_TASK_SIZE);
	}
	
	public ForkJoinEvaluator(final ForkJoinPool pool, final int taskSize) {
		Validator.notNull(pool, "Thread pool");
		
		_pool = pool;
		_taskSize = taskSize;
	}
	
	@Override
	public void evaluate(final List<? extends Runnable> runnables) {
		Validator.notNull(runnables, "Runnables");
		_pool.invoke(new EvaluatorTask(runnables, 0, runnables.size(), _taskSize));
	}

	@Override
	public int getParallelTasks() {
		return _pool.getParallelism();
	}
	
	
	private static class EvaluatorTask extends RecursiveAction {
		private static final long serialVersionUID = -7886596400215187705L;
		
		private final List<? extends Runnable> _runnables;
		private final int _from;
		private final int _to;
		private final int _taskSize;
		
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
		
		
		private void exec(final int from, final int to) {
			if (_runnables instanceof RandomAccess) {
				for (int i = _from; i < _to; ++i) {
					_runnables.get(i).run();
				}
			} else {
				for (Runnable runnable : _runnables.subList(_from, _to)) {
					runnable.run();
				}
			}
		}
		
		@Override
		protected void compute() {
			if (_to - _from <= _taskSize) {
				exec(_from, _to);
			} else {
				final int mid = (_from + _to) >>> 1;
				invokeAll(
						new EvaluatorTask(_runnables, _from, mid, _taskSize), 
						new EvaluatorTask(_runnables, mid, _to, _taskSize)
					);
			}
		}
		
	}

}
