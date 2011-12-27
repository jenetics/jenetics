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

import java.util.Random;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class PermutationChromosomeCharacterTest 
	extends ChromosomeTester<PermutationGene<Character>> 
	{ 
	
	private final Factory<Chromosome<PermutationGene<Character>>> 
	_factory = new Factory<Chromosome<PermutationGene<Character>>>() {
		private final char[] _characters = "qwertzuiopü+asdfghjklöä#yxcvbnm,.-".toCharArray();
		private final ISeq<Character> _alleles = new Array<Character>(100).fill(new Factory<Character>() {
			private final Random _random = RandomRegistry.getRandom();
			@Override
			public Character newInstance() {
				return _characters[_random.nextInt(_characters.length)];
			}
			
		}).toISeq();
		
		@Override
		public PermutationChromosome<Character> newInstance() {
			return new PermutationChromosome<>(_alleles);
		}
	};
	
	@Override 
	protected Factory<Chromosome<PermutationGene<Character>>> getFactory() {
		return _factory;
	}

}