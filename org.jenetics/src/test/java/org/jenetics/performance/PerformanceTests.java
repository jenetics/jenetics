/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.performance;

import javolution.context.LogContext;
import javolution.lang.ClassInitializer;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
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


















