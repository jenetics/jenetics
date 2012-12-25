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

import static org.jenetics.stat.StatisticsAssert.assertDistribution;
import static org.jenetics.util.accumulators.accumulate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Integer64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.accumulators.MinMax;
import org.jenetics.util.IO;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class Integer64ChromosomeTest
	extends NumberChromosomeTester<Integer64, Integer64Gene>
{

	private final Integer64Chromosome
	_factory = new Integer64Chromosome(0, Long.MAX_VALUE, 500);
	@Override protected Integer64Chromosome getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstanceDistribution() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random(12345));

			final Integer64 min = Integer64.ZERO;
			final Integer64 max = Integer64.valueOf(10000000);

			final MinMax<Integer64> mm = new MinMax<>();
			final Variance<Integer64> variance = new Variance<>();
			final Histogram<Integer64> histogram = Histogram.valueOf(min, max, 10);

			for (int i = 0; i < 1000; ++i) {
				final Integer64Chromosome chromosome = new Integer64Chromosome(min, max, 500);

				accumulate(
						chromosome,
						mm.map(Integer64Gene.Value),
						variance.map(Integer64Gene.Value),
						histogram.map(Integer64Gene.Value)
					);
			}

			Assert.assertTrue(mm.getMin().compareTo(0) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100) <= 100);
			assertDistribution(histogram, new UniformDistribution<>(min, max));
		} finally {
			LocalContext.exit();
		}
    }

	@Test
	public void firstGeneConverter() {
		final Integer64Chromosome c = getFactory().newInstance();

		Assert.assertEquals(Integer64Chromosome.Gene.apply(c), c.getGene(0));
	}

	@Test
	public void geneConverter() {
		final Integer64Chromosome c = getFactory().newInstance();

		for (int i = 0; i < c.length(); ++i) {
			Assert.assertEquals(
					Integer64Chromosome.Gene(i).apply(c),
					c.getGene(i)
				);
		}
	}

	@Test
	public void genesConverter() {
		final Integer64Chromosome c = getFactory().newInstance();
		Assert.assertEquals(
				Integer64Chromosome.Genes.apply(c),
				c.toSeq()
			);
	}

	@Test
	public void objectSerializationCompatibility() throws IOException {
		final Random random = new LCG64ShiftRandom.ThreadSafe(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			final Object chromosome = new Integer64Chromosome(Integer.MIN_VALUE, Integer.MAX_VALUE, 500);

			final String resource = "/org/jenetics/Integer64Chromosome.object";
			try (InputStream in = getClass().getResourceAsStream(resource)) {
				final Object object = IO.object.read(in);

				Assert.assertEquals(object, chromosome);
			}
		} finally {
			LocalContext.exit();
		}
	}

	@Test
	public void xmlSerializationCompatibility() throws IOException {
		final Random random = new LCG64ShiftRandom.ThreadSafe(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			final Object chromosome = new Integer64Chromosome(Integer.MIN_VALUE, Integer.MAX_VALUE, 500);

			final String resource = "/org/jenetics/Integer64Chromosome.xml";
			try (InputStream in = getClass().getResourceAsStream(resource)) {
				final Object object = IO.xml.read(in);

				Assert.assertEquals(object, chromosome);
			}
		} finally {
			LocalContext.exit();
		}
	}

	private static String Source = "/home/fwilhelm/Workspace/Development/Projects/Jenetics/" +
			"org.jenetics/src/test/resources/org/jenetics/";

	public static void main(final String[] args) throws Exception {
		final Random random = new LCG64ShiftRandom.ThreadSafe(0);

		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			Object c = new Integer64Chromosome(Integer.MIN_VALUE, Integer.MAX_VALUE, 500);

			IO.xml.write(c, Source + "Integer64Chromosome.xml");
			IO.object.write(c, Source + "Integer64Chromosome.object");
		} finally {
			LocalContext.exit();
		}
	}

}




