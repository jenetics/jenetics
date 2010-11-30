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

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.io.IOException;

import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Factory;
import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class PopulationTest {
	
	private static final class Function implements FitnessFunction<Float64Gene, Float64> {
		private static final long serialVersionUID = 2793605351118238308L;
		@Override
		public Float64 evaluate(final Genotype<Float64Gene> genotype) {
			final Float64Gene gene = genotype.getChromosome().getGene(0);
			return Float64.valueOf(sin(toRadians(gene.doubleValue())));
		}
	}
	
	private static final class Continous implements FitnessFunction<Float64Gene, Float64> {
		private static final long serialVersionUID = 1L;
		@Override
		public Float64 evaluate(Genotype<Float64Gene> genotype) {
			return genotype.getChromosome().getGene().getAllele();
		}
	}
	
	private static final FitnessFunction<Float64Gene, Float64> _cf = new Continous();
	private static Phenotype<Float64Gene, Float64> pt(double value) {
		return Phenotype.valueOf(Genotype.valueOf(new Float64Chromosome(Float64Gene.valueOf(value, 0, 10))), _cf, 0);
	}
	
	@Test
	public void sort() {
		final Population<Float64Gene, Float64> population = new Population<Float64Gene, Float64>();
		for (int i = 0; i < 100; ++i) {
			population.add(pt(Math.random()*9.0));
		}
		
		population.sort();
		for (int i = 0; i < population.size() - 1; ++i) {
			Float64 first = _cf.evaluate(population.get(i).getGenotype());
			Float64 second = _cf.evaluate(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) >= 0);
		}
		
		ArrayUtils.shuffle(population);
		population.sort(Optimize.MAXIMUM.<Float64>descending());
		for (int i = 0; i < population.size() - 1; ++i) {
			Float64 first = _cf.evaluate(population.get(i).getGenotype());
			Float64 second = _cf.evaluate(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) >= 0, first + "<" + second);
		}
		
		ArrayUtils.shuffle(population);
		population.sort(Optimize.MINIMUM.<Float64>descending());
		for (int i = 0; i < population.size() - 1; ++i) {
			Float64 first = _cf.evaluate(population.get(i).getGenotype());
			Float64 second = _cf.evaluate(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) <= 0, first + ">" + second);
		}
	}
	
	@Test(invocationCount = 5)
	public void xmlSerialization() throws XMLStreamException {
		final int size = 10;
		final Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(new Float64Chromosome(0, 360));
		final Function ff = new Function();
		final IdentityScaler<Float64> scaler = IdentityScaler.<Float64>valueOf();
		final Population<Float64Gene, Float64> population = new Population<Float64Gene, Float64>();
		
		for (int i = 0; i < size; ++i) {
			final Phenotype<Float64Gene, Float64> pt = Phenotype.valueOf(
				gtf.newInstance(), ff, scaler, 0
			);
			population.add(pt);
		}
		
		SerializeUtils.testXMLSerialization(population);
	}
	
	@Test(invocationCount = 5)
	public void objectSerialization() throws IOException {
		final int size = 10;
		final Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(new Float64Chromosome(0, 360));
		final Function ff = new Function();
		final IdentityScaler<Float64> scaler = IdentityScaler.<Float64>valueOf();
		final Population<Float64Gene, Float64> population = new Population<Float64Gene, Float64>();
		
		for (int i = 0; i < size; ++i) {
			final Phenotype<Float64Gene, Float64> pt = Phenotype.valueOf(
				gtf.newInstance(), ff, scaler, 0
			);
			population.add(pt);
		}
		
		SerializeUtils.testSerialization(population);
	}
	
}





