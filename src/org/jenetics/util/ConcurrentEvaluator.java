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

import javolution.context.ConcurrentContext;


/**
 * Evaluate the fitness function of an given list of {@link Runnable}s concurrently.
 * This implementation uses the {@link ConcurrentContext} of the 
 * <a href="http://javolution.org/api/index.html">Javolution</a> libarary.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ConcurrentEvaluator.java,v 1.1 2008-09-29 20:39:33 fwilhelm Exp $
 */
public class ConcurrentEvaluator implements Evaluator {
	private final int _numberOfThreads;
	
	/**
	 * Create a concurrent evaluator object where the number of concurrent threads
	 * is equal to the number of available cores.
	 */
	public ConcurrentEvaluator() {
		this(ConcurrentContext.getConcurrency() + 1);
	}
	
	/**
	 * Create a concurrent evaluator object with the given number of concurrent
	 * threas.
	 * 
	 * @param numberOfThreads the number of concurrent threads.
	 */
	public ConcurrentEvaluator(final int numberOfThreads) {
		if (numberOfThreads <= 0) {
			_numberOfThreads = 1;
		} else {
			_numberOfThreads = numberOfThreads;
		}
	}
	
	@Override
	public void evaluate(final List<? extends Runnable> runnables) {
		Validator.notNull(runnables, "Runnables");
		
		ConcurrentContext.enter();
		try {
			final int[] parts = ArrayUtils.partition(runnables.size(), _numberOfThreads);
			
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
		} finally {
			ConcurrentContext.exit();
		}
	}
	
}
