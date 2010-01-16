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

import javolution.context.ConcurrentContext;


/**
 * Evaluate the fitness function of an given list of {@link Runnable}s concurrently.
 * This implementation uses the {@link ConcurrentContext} of the 
 * <a href="http://javolution.org/api/index.html">Javolution</a> library.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ConcurrentEvaluator.java,v 1.7 2010-01-16 22:26:05 fwilhelm Exp $
 */
public class ConcurrentEvaluator implements Evaluator {
	private final int _parallelTasks;
	
	/**
	 * Create a concurrent evaluator object where the number of concurrent threads
	 * is equal to the number of available cores.
	 */
	public ConcurrentEvaluator() {
		this(Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Create a concurrent evaluator object with the given number of concurrent
	 * threads.
	 * 
	 * @param parallelTasks the number of concurrent threads.
	 */
	public ConcurrentEvaluator(final int parallelTasks) {
		_parallelTasks = Math.max(parallelTasks, 1);
	}
	
	@Override
	public void evaluate(final List<? extends Runnable> runnables) {
		Validator.notNull(runnables, "Runnables");
		
		if (!runnables.isEmpty()) {
			eval(runnables);
		}
	}
	
	private void eval(final List<? extends Runnable> runnables) {
		ConcurrentContext.enter();
		try {
			final int[] parts = ArrayUtils.partition(runnables.size(), _parallelTasks);
			
			if (runnables instanceof RandomAccess) {
				for (int i = 0; i < parts.length - 1; ++i) {
					final int part = i;
					ConcurrentContext.execute(new Runnable() {
						@Override public void run() {
							for (int j = parts[part + 1]; --j >= parts[part];) {
								runnables.get(j).run();
							}
						}
					});
				}
			} else {
				for (int i = 0; i < parts.length - 1; ++i) {
					final int part = i;
					ConcurrentContext.execute(new Runnable() {
						@Override public void run() {
							for (Runnable runnable : runnables.subList(parts[part], parts[part + 1])) {
								runnable.run();
							}
						}
					});
				}
			}
		} finally {
			ConcurrentContext.exit();
		}
	}
	
	@Override
	public int getParallelTasks() {
		return _parallelTasks;
	}
	
}








