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

/**
 * The Evaluator is used for evaluating the fitness of each phenotype of 
 * an population. This is necessary to have an defined point where the fitness
 * calculation is performed. Otherwise the fitness calculation is performed
 * at the time where the {@link org.jenetics.Phenotype#run()} is called the first time.
 * 
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public interface Evaluator {

	/**
	 * Evaluates (calls the {@link Runnable#run()} of the given list of
	 * {@link Runnable} objects. This method returns when all given runnables
	 * has been executed.
	 * 
	 * @param runnables the runnables to evaluate.
	 * @throws NullPointerException if the runnable list (or one of its element)
	 *         is {@code null}.
	 */
	public void evaluate(final List<? extends Runnable> runnables);
	
	/**
	 * Return the number of parallel tasks of this evaluator.
	 * 
	 * @return the number of parallel tasks this evaluator tries to execute the
	 *         given {@link Runnable}s.
	 */
	public int getParallelTasks();
	
}
