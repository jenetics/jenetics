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

import static org.jenetics.TestUtils.newFloat64GenePopulation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.testng.Assert;
import org.testng.annotations.Test;

import javolution.xml.XMLSerializable;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Function;
import org.jenetics.util.IO;
import org.jenetics.util.arrays;
import org.jenetics.util.Serialize;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-11-30 $</em>
 */
public class PopulationTest {

	private static final class Continous
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 1L;
		@Override
		public Float64 apply(Genotype<Float64Gene> genotype) {
			return genotype.getChromosome().getGene().getAllele();
		}
	}

	private static final Function<Genotype<Float64Gene>, Float64> _cf = new Continous();
	private static Phenotype<Float64Gene, Float64> pt(double value) {
		return Phenotype.valueOf(Genotype.valueOf(new Float64Chromosome(Float64Gene.valueOf(value, 0, 10))), _cf, 0);
	}

	@Test
	public void sort() {
		final Population<Float64Gene, Float64> population = new Population<>();
		for (int i = 0; i < 100; ++i) {
			population.add(pt(Math.random()*9.0));
		}

		population.sort();
		for (int i = 0; i < population.size() - 1; ++i) {
			Float64 first = _cf.apply(population.get(i).getGenotype());
			Float64 second = _cf.apply(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) >= 0);
		}

		arrays.shuffle(population);
		population.sort(Optimize.MAXIMUM.<Float64>descending());
		for (int i = 0; i < population.size() - 1; ++i) {
			Float64 first = _cf.apply(population.get(i).getGenotype());
			Float64 second = _cf.apply(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) >= 0, first + "<" + second);
		}

		arrays.shuffle(population);
		population.sort(Optimize.MINIMUM.<Float64>descending());
		for (int i = 0; i < population.size() - 1; ++i) {
			Float64 first = _cf.apply(population.get(i).getGenotype());
			Float64 second = _cf.apply(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) <= 0, first + ">" + second);
		}
	}

	@Test
	public void phenotypeXMLSerialization()
		throws IOException
	{
		final Population<Float64Gene, Float64> population = new Population<>();
		for (int i = 0; i < 1000; ++i) {
			population.add(pt(Math.random()*9.0));
		}

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IO.xml.write(population, out);

		final byte[] data = out.toByteArray();

		final ByteArrayInputStream in = new ByteArrayInputStream(data);

		@SuppressWarnings("unchecked")
		final Population<Float64Gene, Float64>
		copy = (Population<Float64Gene, Float64>)IO.xml.read(XMLSerializable.class, in);

		for (int i = 1; i < population.size(); ++i) {
			Assert.assertSame(
				copy.get(i - 1).getFitnessFunction(),
				copy.get(i).getFitnessFunction()
			);
			Assert.assertSame(
				copy.get(i - 1).getFitnessScaler(),
				copy.get(i).getFitnessScaler()
			);
		}
	}

	@Test
	public void xmlSerialization() throws IOException {
		final Population<Float64Gene, Float64> population = newFloat64GenePopulation(23, 34, 123);
		Serialize.xml.test(population);
	}

	@Test
	public void objectSerialization() throws IOException {
		final Population<Float64Gene, Float64> population = newFloat64GenePopulation(23, 34, 123);
		Serialize.object.test(population);
	}

}





