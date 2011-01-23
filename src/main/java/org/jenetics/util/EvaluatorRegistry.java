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

import java.util.List;

import javolution.context.LocalContext;

/**
 * You can temporarily (and locally) change the evaluator by using the 
 * {@link LocalContext} from the <a href="http://javolution.org/">javolution</a> 
 * project.
 * 
 * [code]
 *     LocalContext.enter();
 *     try {
 *         EvaluatorRegistry.setEvaluator(new MyEvaluator());
 *         ...
 *     } finally {
 *         LocalContext.exit(); // Restore the previous evaluator.
 *     }
 * [/code]
 * 
 * @see LocalContext
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class EvaluatorRegistry {
	
	private static final LocalContext.Reference<Evaluator> EVALUATOR = 
		new LocalContext.Reference<Evaluator>(new ConcurrentEvaluator());
	
	
	private EvaluatorRegistry() {
		throw new AssertionError("Don't create an 'EvaluatorRegistry' instance.");
	}
	
	/**
	 * Return the number of parallel tasks of the current evaluator.
	 * {@code getEvaluator().getParallelTasks()}.
	 * 
	 * @return the number of parallel tasks of the current evaluator.
	 */
	public static int getParallelTasks() {
		return EVALUATOR.get().getParallelTasks();
	}
	
	/**
	 * Return the currently registered evaluator.
	 * 
	 * @return the currently registered evaluator.
	 */
	public static Evaluator getEvaluator() {
		return EVALUATOR.get();
	}
	
	/**
	 * Set the evaluator to use.
	 * 
	 * @param evaluator set the evaluator to use.
	 * @throws NullPointerException if the given {@code evaluator} is {@code null}.
	 */
	public static void setEvaluator(final Evaluator evaluator) {
		EVALUATOR.set(nonNull(evaluator, "Evaluator"));
	}
	
	/**
	 * Evaluates the given task list with the currently registered evaluator.
	 * 
	 * @param tasks the tasks to evaluate.
	 * @throws NullPointerException if the given {@code tasks} list is {@code null}.
	 */
	public static void evaluate(final List<? extends Runnable> tasks) {
		EVALUATOR.get().evaluate(nonNull(tasks, "Task list"));
	}
	
}
