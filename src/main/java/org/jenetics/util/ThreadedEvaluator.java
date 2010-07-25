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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics.util;

import static org.jenetics.util.Validator.nonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


/**
 * Evaluate the fitness function of an given list of {@link Runnable}s concurrently.
 * This implementation uses the {@link ExecutorService} of the 
 * {@code java.util.concurrent} library.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ThreadedEvaluator implements Evaluator {
	private final int _parallelTasks;
	private final ExecutorService _pool;
	
	/**
	 * Create a threaded evaluator object where the number of concurrent threads
	 * is equal to the number of available cores.
	 * 
	 * @param pool the executor service (thread pool).
	 * @throws NullPointerException if the given thread pool is {@code null}.
	 */
	public ThreadedEvaluator(final ExecutorService pool) {
		this(pool, Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Create a concurrent evaluator object with the given number of concurrent
	 * threads.
	 * 
	 * @param parallelTasks the number of concurrent threads.
	 * @param pool the executor service (thread pool).
	 * @throws NullPointerException if the given thread pool is {@code null}.
	 */
	public ThreadedEvaluator(final ExecutorService pool, final int parallelTasks) {
		nonNull(pool, "Thread pool");
		_parallelTasks = Math.max(parallelTasks, 1);
		_pool = pool;
	}
	
	@Override
	public void evaluate(final List<? extends Runnable> runnables) {
		nonNull(runnables, "Runnables");
		
		if (!runnables.isEmpty()) {
			eval(runnables);
		}
	}
	
	private synchronized void eval(final List<? extends Runnable> runnables) {
		//Executing the tasks.
		try {
			_pool.invokeAll(partition(runnables, _parallelTasks));
		} catch (InterruptedException e) {
			_pool.shutdown();
			Thread.currentThread().interrupt();
		} 
	}
	
	@Override
	public int getParallelTasks() {
		return _parallelTasks;
	}
	
	private static final class EvaluatorCallable implements Callable<Void> {
		private final List<? extends Runnable> _runnables;
		private final int _fromIndex;
		private final int _toIndex;
		
		EvaluatorCallable(
			final List<? extends Runnable> runnables, 
			final int fromIndex, final int toIndex
		) {
			assert (runnables != null);
			_runnables = runnables;
			_fromIndex = fromIndex;
			_toIndex = toIndex;
		}

		
		@Override
		public Void call() {
			if (_runnables instanceof RandomAccess) {
				for (int i = _fromIndex; i < _toIndex; ++i) {
					_runnables.get(i).run();
				}
			} else {
				for (Runnable runnable : _runnables.subList(_fromIndex, _toIndex)) {
					runnable.run();
				}
			}
			
			return null;
		}
		
	}
	
	
	private static List<? extends Callable<Void>> partition(
		final List<? extends Runnable> runnables, 
		final int parts
	) {
		final int[] indexes = ArrayUtils.partition(runnables.size(), parts);
		final List<EvaluatorCallable> workers = new ArrayList<EvaluatorCallable>(indexes.length);
		for (int i = 0; i < indexes.length - 1; ++i) {
			workers.add(new EvaluatorCallable(runnables, indexes[i], indexes[i + 1]));
		}
		
		return workers;
	}
	
	
	
	
}







