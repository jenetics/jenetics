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
package org.jenetics;

import java.util.Random;

import org.jenetics.util.Array;
import org.jenetics.util.CharSet;
import org.jenetics.util.RandomRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class SinglePointCrossoverTest {

	private static final class ConstRandom extends Random {
		private static final long serialVersionUID = 1L;
		private final int _value;
		
		public ConstRandom(final int value) {
			_value = value;
		}
		
		@Override
		public int nextInt() {
			return _value;
		}

		@Override
		public int nextInt(int n) {
			return _value;
		}
		
	}
	
	@Test
	public void crossover() {		
		final CharSet chars = CharSet.valueOf("a-zA-Z");
		
		final Array<CharacterGene> g1 = new CharacterChromosome(chars, 20).toArray();
		final Array<CharacterGene> g2 = new CharacterChromosome(chars, 20).toArray();
		
		final Random random = RandomRegistry.getRandom();
		try {
			final SinglePointCrossover<CharacterGene> 
			crossover = new SinglePointCrossover<CharacterGene>();
			
			int rv = 12;
			RandomRegistry.setRandom(new ConstRandom(rv));
			Array<CharacterGene> g1c = g1.copy();
			Array<CharacterGene> g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c.subArray(0, rv), g2.subArray(0, rv));
			Assert.assertEquals(g1c.subArray(rv), g2.subArray(rv));
			
			rv = 0;
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c, g2);
			Assert.assertEquals(g2c, g1);
			Assert.assertEquals(g1c.subArray(0, rv), g2.subArray(0, rv));
			Assert.assertEquals(g1c.subArray(rv), g2.subArray(rv));

			rv = 1;
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c.subArray(0, rv), g2.subArray(0, rv));
			Assert.assertEquals(g1c.subArray(rv), g2.subArray(rv));
			
			rv = g1.length();
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c, g2);
			Assert.assertEquals(g2c, g1);
			Assert.assertEquals(g1c.subArray(0, rv), g2.subArray(0, rv));
			Assert.assertEquals(g1c.subArray(rv), g2.subArray(rv));
		} finally {
			RandomRegistry.setRandom(random);
		}
	}
	
}


















