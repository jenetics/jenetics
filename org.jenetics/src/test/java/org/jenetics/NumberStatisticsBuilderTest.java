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
package org.jenetics;

import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.testng.annotations.DataProvider;

import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class NumberStatisticsBuilderTest extends StatisticsBuilderTest {

	@Override
	public Object newBuilder() {
		return new NumberStatistics.Builder<Float64Gene, Float64>();
	}
	
	@Override
	@DataProvider(name = "properties")
	public Object[][] builderProperties() {
		LocalContext.enter();
		try {
			final Random random = new Random(12345678);
			RandomRegistry.setRandom(random);
			
			return new Object[][] {
					{"generation", Integer.TYPE, random.nextInt(1000)},
					{"invalid", Integer.TYPE, random.nextInt(1000)},
					{"killed", Integer.TYPE, random.nextInt(10000)},
					{"samples", Integer.TYPE, random.nextInt(1000)},
					{"ageMean", Double.TYPE, random.nextDouble()},
					{"ageVariance", Double.TYPE, random.nextDouble()},
					{"fitnessMean", Double.TYPE, random.nextDouble()},
					{"fitnessVariance", Double.TYPE, random.nextDouble()},
					{"standardError", Double.TYPE, random.nextDouble()},
					{"bestPhenotype", Phenotype.class, TestUtils.newFloat64Phenotype()},
					{"worstPhenotype", Phenotype.class, TestUtils.newFloat64Phenotype()},
					{"optimize", Optimize.class, Optimize.MINIMUM},
					{"optimize", Optimize.class, Optimize.MAXIMUM}
			};
		} finally {
			LocalContext.exit();
		}
	}
	
}
