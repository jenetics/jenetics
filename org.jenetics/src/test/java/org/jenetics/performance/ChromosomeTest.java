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

import org.jenetics.Float64Chromosome;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
@Suite("Chromosome")
public class ChromosomeTest {

	private int SIZE = 1000000;
	private final int LOOPS = 20;

	public ChromosomeTest() {
	}


	@Test(1)
	public TestCase newInstance = new TestCase("newInstance()", LOOPS, SIZE) {
		private final Float64Chromosome
		_chromosome = new Float64Chromosome(0, 1, getSize());

		@Override
		protected void test() {
			_chromosome.newInstance();
		}
	};

	@Test(2)
	public TestCase newInstnaceISeq = new TestCase("newInstance(ISeq)", LOOPS, SIZE) {
		private final Float64Chromosome
		_chromosome = new Float64Chromosome(0, 1, getSize());

		@Override
		protected void test() {
			_chromosome.newInstance(_chromosome.toSeq());
		}
	};

	@Test(3)
	public TestCase isValid = new TestCase("isValid()", LOOPS, SIZE) {
		private Float64Chromosome _chromosome = new Float64Chromosome(0, 1, getSize());

		@Override
		protected void beforeTest() {
			_chromosome = new Float64Chromosome(0, 1, getSize());
		}

		@Override
		protected void test() {
			_chromosome.isValid();
		}
	};

}
