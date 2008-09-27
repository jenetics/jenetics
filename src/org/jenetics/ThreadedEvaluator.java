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
package org.jenetics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javolution.context.ConcurrentContext;

import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Validator;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ThreadedEvaluator.java,v 1.1 2008-09-27 16:20:12 fwilhelm Exp $
 */
public class ThreadedEvaluator implements FitnessEvaluator {
	private final int _maxThreads;
	private final ExecutorService _pool;
	private final List<Evaluator> _tasks;
	
	public ThreadedEvaluator(final ExecutorService pool) {
		this(pool, ConcurrentContext.getConcurrency() + 1);
	}
	
	public ThreadedEvaluator(final ExecutorService pool, final int maxThreads) {
		Validator.notNull(pool, "Thread pool");
		
		if (maxThreads <= 0) {
			_maxThreads = 1;
		} else {
			_maxThreads = maxThreads;
		}
		_pool = pool;
		
		_tasks = new ArrayList<Evaluator>(_maxThreads);
		for (int i = 0; i < _maxThreads; ++i) {
			_tasks.add(new Evaluator());
		}
	}
	
	@Override
	public void evaluate(final List<? extends Runnable> evaluables) {
		Validator.notNull(evaluables, "Population");
		
		//Creating the tasks.
		final int[] parts = ArrayUtils.partition(evaluables.size(), _maxThreads);
		for (int i = 0; i < parts.length - 1; ++i) {
			_tasks.get(i).init(evaluables, parts[i], parts[i + 1]);
		}
		
		//Executing the tasks.
		try {
			_pool.invokeAll(_tasks);
		} catch (InterruptedException e) {
			_pool.shutdown();
			Thread.currentThread().interrupt();
		} 
	}
	
	private static final class Evaluator  implements Callable<Void> {
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
			for (int i = _fromIndex; i < _toIndex; ++i) {
				_runnables.get(i).run();
			}
			return null;
		}
		
	}
	
}







