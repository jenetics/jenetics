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
package org.jenetics.stat;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.AbstractAccumulatorTester;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class QuantileTest extends AbstractAccumulatorTester<Quantile<Double>> {

	private final Factory<Quantile<Double>> _factory = new Factory<Quantile<Double>>() {
		@Override
		public Quantile<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new Quantile<Double>(random.nextDouble());
		}
	};
	@Override
	protected Factory<Quantile<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void quantile() {
		final Quantile<Integer> quantile = new Quantile<Integer>(0.5);
		for (int i = 0; i < 1000; ++i) {
			quantile.accumulate(i); 
			Assert.assertEquals(quantile.getQuantile(), Math.floor(i/2.0), 1.0);
		}
	}

}
