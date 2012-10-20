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

import java.util.Random;

import javolution.context.LocalContext;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class CharacterChromosomeTest extends ChromosomeTester<CharacterGene> {

	private final Factory<Chromosome<CharacterGene>>
	_factory = new CharacterChromosome(500);
	@Override protected Factory<Chromosome<CharacterGene>> getFactory() {
		return _factory;
	}

	
	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstanceDistribution() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random(12345));
			
			final CharSeq characters = new CharSeq("0123456789");
			final CharacterChromosome chromosome = new CharacterChromosome(characters, 5000);
			
			final Histogram<Long> histogram = Histogram.valueOf(0L, 10L, 10);
			
			for (CharacterGene gene : chromosome) {
				histogram.accumulate(Long.valueOf(gene.getAllele().toString()));
			}
			
			assertDistribution(histogram, new UniformDistribution<>(0L, 10L));
		} finally {
			LocalContext.exit();
		}
    }
	
	@Test(dataProvider = "genes")
	public void newCharacterChromosome(final String genes) {
		final CharSeq characters = new CharSeq("0123456789");
		CharacterChromosome chromosome = new CharacterChromosome(genes, characters);
		
		Assert.assertEquals(new String(new StringBuilder(chromosome)), genes);
	}
	
	@Test(dataProvider = "genes", expectedExceptions = IllegalArgumentException.class)
	public void newIllegalCharacterChromosome(final String genes) {
		final CharSeq characters = new CharSeq("012356789");
		CharacterChromosome chromosome = new CharacterChromosome(genes, characters);
		
		Assert.assertEquals(new String(new StringBuilder(chromosome)), genes);		
	}
	
	@DataProvider(name = "genes")
	public Object[][] genes() {
		return new Object[][] {
				{"54374"},
				{"543794578"},
				{"54374546"},
				{"543345647"},
				{"54304897"},
				{"5433457245"}
		};
	}

}




