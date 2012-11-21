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

import org.jenetics.Float64Chromosome;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
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
