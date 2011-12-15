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

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import javolution.context.ObjectFactory;

import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.arrays;
import org.jenetics.util.object;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class PermutationGene<T> implements Gene<T, PermutationGene<T>> {

	private static final long serialVersionUID = 1L;
	
	private int _index = -1;
	
	T[] _validAlleles;
	private T _allele;
	
	PermutationGene() {
	}
	
	@Override
	public T getAllele() {
		return _allele;
	}
	
	@Override
	public PermutationGene<T> copy() {
		return this;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public PermutationGene<T> newInstance() {
		@SuppressWarnings("unchecked")
		final PermutationGene<T> gene = (PermutationGene<T>)FACTORY.object();
		
		gene._index = RandomRegistry.getRandom().nextInt(_validAlleles.length);
		gene._validAlleles = _validAlleles;
		gene._allele = _validAlleles[gene._index];
		return gene;
	}
	
	public Factory<PermutationGene<T>> asFactory() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(PermutationGene.class)
				.and(_index)
				.and(_allele)
				.and(_validAlleles).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final PermutationGene<?> pg = (PermutationGene<?>)obj;
		return eq(_index, pg._index) && 
				eq(_allele, pg._allele) && 
				eq(_validAlleles, pg._validAlleles);
	}
	
	@Override
	public String toString() {
		return object.str(_allele);
	}
	
	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/
	
	@SuppressWarnings("rawtypes")
	private static final ObjectFactory<PermutationGene> 
	FACTORY = new ObjectFactory<PermutationGene>() {
		@Override
		protected PermutationGene create() {
			return new PermutationGene();
		}
	};
	
	public static <T> PermutationGene<T> valueOf(final T allele, final T[] validAlleles ) {
		if (validAlleles.length == 0) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}
		
		final int index = arrays.indexOf(validAlleles, allele);
		if (index == -1) {
			throw new IllegalArgumentException(String.format(
				"Array of valid alleles doesn't contain allele '%s'.", allele
			));
		}
		
		@SuppressWarnings("unchecked")
		final PermutationGene<T> gene = (PermutationGene<T>)FACTORY.object();
		
		gene._index = index;
		gene._allele = allele;
		gene._validAlleles = validAlleles.clone();
		return gene;
	}
	
	public static <T> PermutationGene<T> valueOf(final T[] validAlleles) {
		if (validAlleles.length == 0) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}
		
		@SuppressWarnings("unchecked")
		final PermutationGene<T> gene = (PermutationGene<T>)FACTORY.object();
		gene._index = RandomRegistry.getRandom().nextInt(validAlleles.length);
		gene._validAlleles = validAlleles;
		gene._allele = validAlleles[gene._index];
		return gene;
	}

}





