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

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javolution.context.ConcurrentContext;


/**
 * Evaluate the fitness function of an given list of {@link Runnable}s concurrently.
 * This implementation uses the {@link ExecutorService} of the 
 * {@code java.util.concurrent} libarary.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ThreadedEvaluator.java,v 1.5 2009-09-15 19:19:36 fwilhelm Exp $
 */
public class ThreadedEvaluator implements Evaluator {
	private final int _numberOfThreads;
	private final ExecutorService _pool;
	private final List<Evaluator> _tasks;
	
	/**
	 * Create a threaded evaluator object where the number of concurrent threads
	 * is equal to the number of available cores.
	 * 
	 * @param pool the executor service (thread pool).
	 * @throws NullPointerException if the given thread pool is {@code null}.
	 */
	public ThreadedEvaluator(final ExecutorService pool) {
		this(pool, ConcurrentContext.getConcurrency() + 1);
	}
	
	/**
	 * Create a concurrent evaluator object with the given number of concurrent
	 * threas.
	 * 
	 * @param numberOfThreads the number of concurrent threads.
	 * @param pool the executor service (thread pool).
	 * @throws NullPointerException if the given thread pool is {@code null}.
	 */
	public ThreadedEvaluator(final ExecutorService pool, final int numberOfThreads) {
		Validator.notNull(pool, "Thread pool");
		
		_numberOfThreads = Math.max(numberOfThreads, 1);
		_pool = pool;
		
		_tasks = new ArrayList<Evaluator>(_numberOfThreads);
		for (int i = 0; i < _numberOfThreads; ++i) {
			_tasks.add(new Evaluator());
		}
	}
	
	@Override
	public synchronized void evaluate(final List<? extends Runnable> evaluables) {
		Validator.notNull(evaluables, "Population");
		
		//Creating the tasks.
		final int[] parts = ArrayUtils.partition(evaluables.size(), _numberOfThreads);
		for (int i = 0; i < parts.length - 1; ++i) {
			_tasks.get(i).init(evaluables, parts[i], parts[i + 1]);
		}
		for (int i = parts.length - 1; i < _numberOfThreads; ++i) {
			_tasks.get(i).init(null, 0, 0);
		}
		
		//Executing the tasks.
		try {
			_pool.invokeAll(_tasks);
		} catch (InterruptedException e) {
			_pool.shutdown();
			Thread.currentThread().interrupt();
		} 
	}
	
	private static final class Evaluator implements Callable<Void> {
		private List<? extends Runnable> _runnables;
		private int _fromIndex;
		private int _toIndex;
		
		public Evaluator() {
		}
		
		public void init(
			final List<? extends Runnable> runnables, 
			final int fromIndex, final int toIndex
		) {
			_runnables = runnables;
			_fromIndex = fromIndex;
			_toIndex = toIndex;
		}
		
		@Override
		public Void call() throws Exception {
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
	
}







