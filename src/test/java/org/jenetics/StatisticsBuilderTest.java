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

import java.lang.reflect.Method;
import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class StatisticsBuilderTest {
	
	public Object newBuilder() {
		return new Statistics.Builder<Float64Gene, Float64>();
	}
	
	@DataProvider(name = "properties")
	public Object[][] builderProperties() {
		LocalContext.enter();
		try {
			final Random random = new Random(123456);
			RandomRegistry.setRandom(random);
			
			return new Object[][] {
					{"generation", Integer.TYPE, random.nextInt(1000)},
					{"invalid", Integer.TYPE, random.nextInt(1000)},
					{"killed", Integer.TYPE, random.nextInt(10000)},
					{"samples", Integer.TYPE, random.nextInt(1000)},
					{"ageMean", Double.TYPE, random.nextDouble()},
					{"ageVariance", Double.TYPE, random.nextDouble()},
					{"bestPhenotype", Phenotype.class, TestUtils.newFloat64Phenotype()},
					{"worstPhenotype", Phenotype.class, TestUtils.newFloat64Phenotype()},
					{"optimize", Optimize.class, Optimize.MINIMUM},
					{"optimize", Optimize.class, Optimize.MAXIMUM}
			};
		} finally {
			LocalContext.exit();
		}
	}
	
	@Test(dataProvider = "properties")
	public void build(final String name, final Class<?> valueType, final Object value)
		throws Exception
	{
		final Object builder = newBuilder();
		final Method setter = builder.getClass().getMethod(name, valueType);
		final Method build = builder.getClass().getMethod("build");
		
		setter.invoke(builder, value);
		final Object statistics = build.invoke(builder);
		final Method getter = statistics.getClass().getMethod(toGetter(name));
		final Object result = getter.invoke(statistics);
		
		Assert.assertEquals(result, value);
	}
	
	private static String toGetter(final String name) {
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

}
