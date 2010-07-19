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

import static org.jenetics.util.Validator.nonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import javolution.util.FastList;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * A population is a collection of Phenotypes.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Population<G extends Gene<?, G>, C extends Comparable<C>> 
	implements List<Phenotype<G, C>>, RandomAccess, XMLSerializable
{
	private static final long serialVersionUID = 1L;
	
	private final List<Phenotype<G, C>> _population;
	
	/**
	 * Constructs a population containing the elements of the specified collection, 
	 * in the order they are returned by the collection's iterator.  
	 *
	 * @param population the collection whose elements are to be placed into 
	 *        this list.
	 * @throws NullPointerException if the specified population is {@code null}.
	 */
	public Population(final Collection<? extends Phenotype<G, C>> population) {
		_population = new ArrayList<Phenotype<G,C>>(population);
	}
	
	/**
	 * Creating a new <code>Population</code> with the preallocated population 
	 * size.
	 * 
	 * @param size Preallocated population size.
	 * @throws IllegalArgumentException if the specified initial capacity is 
	 *         negative
	 */
	public Population(final int size) {
		_population = new ArrayList<Phenotype<G, C>>(size);
	}
	
	/**
	 * Creating a new <code>Population</code>.
	 */
	public Population() {
		_population = new ArrayList<Phenotype<G, C>>();
	}
	
	/**
	 * Add <code>Phenotype</code> to the <code>Population</code>.
	 * 
	 * @param phenotype <code>Phenotype</code> to be add.
	 * @throws NullPointerException if the given {@code phenotype} is {@code null}.
	 */
	@Override
	public boolean add(final Phenotype<G, C> phenotype) {
		nonNull(phenotype, "Phenotype");
		return _population.add(phenotype);
	}
	
	/**
	 * Add <code>Phenotype</code> to the <code>Population</code>.
	 * 
	 * @param index Index of the 
	 * @param phenotype <code>Phenotype</code> to be add.
	 * @throws NullPointerException if the given {@code phenotype} is {@code null}.
	 */
	@Override
	public void add(final int index, final Phenotype<G, C> phenotype) {
		nonNull(phenotype, "Phenotype");
		_population.add(index, phenotype);
	}
	
	@Override
	public boolean addAll(final Collection<? extends Phenotype<G, C>> c) {
		return _population.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Phenotype<G, C>> c) {
		return _population.addAll(index, c);
	}

	@Override
	public Phenotype<G, C> get(final int index) {
		return _population.get(index);
	}
	
	@Override
	public Phenotype<G, C> set(final int index, final Phenotype<G, C> phenotype) {
		nonNull(phenotype, "Phenotype");
		return _population.set(index, phenotype);
	}
	
	public void remove(final Phenotype<G, C> phenotype) {
		nonNull(phenotype, "Phenotype");
		_population.remove(phenotype);
	}
	
	@Override
	public boolean remove(final Object o) {
		return _population.remove(o);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return _population.removeAll(c);
	}
	
	@Override
	public Phenotype<G, C> remove(final int index) {
		return _population.remove(index);
	}
	
	@Override
	public void clear() {
		_population.clear();
	}
	
	/**
	 * Sorting the phenotypes in this population according to its fitness
	 * value in descending order.
	 */
	public void sort() {
		Collections.sort(_population, new Comparator<Phenotype<G, C>>() {
			@Override 
			public int compare(
				final Phenotype<G, C> that, final Phenotype<G, C> other
			) {
				return other.compareTo(that);
			}
		});
	}
	
	/**
	 * Reverse the order of the population.
	 */
	public void reverse() {
		Collections.reverse(_population);
	}
	
	@Override
	public Iterator<Phenotype<G, C>> iterator() {
		return _population.iterator();
	}
	
	@Override
	public ListIterator<Phenotype<G, C>> listIterator() {
		return _population.listIterator();
	}
	
	@Override
	public ListIterator<Phenotype<G, C>> listIterator(final int index) {
		return _population.listIterator(index);
	}

	@Override
	public int size() {
		return _population.size();
	}

	@Override
	public boolean isEmpty() {
		return _population.isEmpty();
	}
	
	@Override
	public boolean contains(final Object o) {
		return _population.contains(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return _population.containsAll(c);
	}

	@Override
	public int indexOf(final Object o) {
		return _population.indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		return _population.lastIndexOf(o);
	}
	
	@Override
	public boolean retainAll(final Collection<?> c) {
		return _population.retainAll(c);
	}

	@Override
	public List<Phenotype<G, C>> subList(final int fromIndex, final int toIndex) {
		return _population.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return _population.toArray();
	}

	@Override
	public <A> A[] toArray(final A[] a) {
		return _population.toArray(a);
	}
	
	public List<Genotype<G>> getGenotypes() {
		final List<Genotype<G>> genotypes = new FastList<Genotype<G>>(_population.size());
		for (Phenotype<G, C> phenotype : _population) {
			genotypes.add(phenotype.getGenotype());
		}
		return genotypes;
	}
	
	@Override
	public int hashCode() {
		return _population.hashCode();
	}
	
	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof Population<?, ?>)) {
			return false;
		}
		
		final Population<?, ?> population = (Population<?, ?>)object;
		return _population.equals(population._population);
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		for (Phenotype<?, ?> pt : this) {
			out.append(pt.toString()).append("\n");
		}
		
		return out.toString();
	}
	
	
	@SuppressWarnings({ "unchecked" })
	static final XMLFormat<Population> 
	XML = new XMLFormat<Population>(Population.class) 
	{
		private static final String SIZE = "size";
		
		@Override
		public Population newInstance(
			final Class<Population> cls, final InputElement xml
		) 
			throws XMLStreamException 
		{
			final int size = xml.getAttribute(SIZE, 10);
			final Population p = new Population(size);
			for (int i = 0; i < size; ++i) {
				final Phenotype pt = xml.getNext();
				p.add(pt);
			}
			return p;
		}
		@Override 
		public void write(final Population p, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(SIZE, p.size());
			for (Object phenotype : p) {
				xml.add(phenotype);
			}
		}
		@Override
		public void read(final InputElement xml, final Population p) {	
		}
	};
	
}



