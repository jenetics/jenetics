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

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.object;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class EnumGene<T> implements Gene<T, EnumGene<T>> {

	private static final long serialVersionUID = 1L;
	
	private ISeq<T> _validAlleles;
	private int _alleleIndex = -1;
	
	EnumGene() {
	}
	
	public ISeq<T> getValidAlleles() {
		return _validAlleles;
	}
	
	public int getAlleleIndex() {
		return _alleleIndex;
	}
	
	@Override
	public T getAllele() {
		return _validAlleles.get(_alleleIndex);
	}
	
	@Override
	public EnumGene<T> copy() {
		final EnumGene<T> gene = new EnumGene<>();
		gene._validAlleles = _validAlleles;
		gene._alleleIndex = _alleleIndex;
		return gene;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public EnumGene<T> newInstance() {
		@SuppressWarnings("unchecked")
		final EnumGene<T> gene = (EnumGene<T>)FACTORY.object();
		
		gene._alleleIndex = RandomRegistry.getRandom().nextInt(_validAlleles.length());
		gene._validAlleles = _validAlleles;
		return gene;
	}
	
	public Factory<EnumGene<T>> asFactory() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(EnumGene.class)
				.and(_alleleIndex)
				.and(_validAlleles).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		final EnumGene<?> pg = (EnumGene<?>)obj;
		return eq(_alleleIndex, pg._alleleIndex) && 
				eq(_validAlleles, pg._validAlleles);
	}
	
	@Override
	public String toString() {
		return object.str(getAllele());
	}
	
	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/
	
	static <T> Function<Integer, EnumGene<T>> ToGene(
		final ISeq<T> validAlleles
	) {
		return new Function<Integer, EnumGene<T>>() {
			@Override
			public EnumGene<T> apply(final Integer index) {
				return valueOf(validAlleles, index);
			}
		};
	}
	
	static <T> Factory<EnumGene<T>> Gene(final ISeq<T> validAlleles) {
		return new Factory<EnumGene<T>>() {
			private int _index = 0;
			@Override
			public EnumGene<T> newInstance() {
				return EnumGene.valueOf(validAlleles, _index++);
			}
		};
	}
	
	
	@SuppressWarnings("rawtypes")
	private static final ObjectFactory<EnumGene> 
	FACTORY = new ObjectFactory<EnumGene>() {
		@Override
		protected EnumGene create() {
			return new EnumGene();
		}
	};
	
	public static <T> EnumGene<T> valueOf(
		final T[] validAlleles, 
		final int alleleIndex
	) {
		return valueOf(new Array<>(validAlleles).toISeq(), alleleIndex);
	}
	
	public static <T> EnumGene<T> valueOf(
		final ISeq<T> validAlleles, 
		final int alleleIndex
	) {
		if (validAlleles.length() == 0) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}
		
		if (alleleIndex < 0 || alleleIndex >= validAlleles.length()) {
			throw new IndexOutOfBoundsException(String.format(
				"Allele index is not in range [0, %d).", alleleIndex
			));
		}
		
		@SuppressWarnings("unchecked")
		final EnumGene<T> gene = (EnumGene<T>)FACTORY.object();
		
		gene._validAlleles = validAlleles;
		gene._alleleIndex = alleleIndex;
		return gene;
	}
	
	public static <T> EnumGene<T> valueOf(final T[] validAlleles) {
		return valueOf(new Array<>(validAlleles).toISeq());
	}
	
	public static <T> EnumGene<T> valueOf(final ISeq<T> validAlleles) {
		if (validAlleles.length() == 0) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}
		
		@SuppressWarnings("unchecked")
		final EnumGene<T> gene = (EnumGene<T>)FACTORY.object();
		gene._validAlleles = validAlleles;
		gene._alleleIndex = RandomRegistry.getRandom().nextInt(validAlleles.length());
		return gene;
	}

}





