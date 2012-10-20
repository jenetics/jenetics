/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
package org.jenetics.performance;

import javolution.context.LogContext;
import javolution.lang.ClassInitializer;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public final class PerformanceTests {
	
	private PerformanceTests() {
	}
	
	public static void main(final String[] args) {
		LogContext.enter(LogContext.NULL);
		try {
			ClassInitializer.initializeClassPath();			
		} finally {
			LogContext.exit();
		}
		
		//ForkJoinContext.setForkkJoinPool(new ForkJoinPool(10));
		//Concurrency.setContext(ForkJoinContext.class);
				
		new TestSuite(ArrayTest.class).run().print();
		new TestSuite(ChromosomeTest.class).run().print();
		new TestSuite(PopulationTest.class).run().print();
		new TestSuite(GATest.class).run().print();
	}
	
}


















