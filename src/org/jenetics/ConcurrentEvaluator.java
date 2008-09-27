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

import java.util.List;

import javolution.context.ConcurrentContext;

import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Validator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ConcurrentEvaluator.java,v 1.2 2008-09-27 16:20:12 fwilhelm Exp $
 */
public class ConcurrentEvaluator implements FitnessEvaluator {
	private final int _maxThreads;
	
	public ConcurrentEvaluator() {
		this(ConcurrentContext.getConcurrency() + 1);
	}
	
	public ConcurrentEvaluator(final int maxThreads) {
		if (maxThreads <= 0) {
			_maxThreads = 1;
		} else {
			_maxThreads = maxThreads;
		}
	}
	
	@Override
	public void evaluate(final List<? extends Runnable> runnables) {
		Validator.notNull(runnables, "Runnables");
		
		ConcurrentContext.enter();
		try {
			final int[] parts = ArrayUtils.partition(runnables.size(), _maxThreads);
			
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
