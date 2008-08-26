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

import static org.jenetics.util.Validator.notNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * A population is a collection of Phenotypes.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Population.java,v 1.3 2008-08-26 22:29:34 fwilhelm Exp $
 */
public class Population<T extends Gene<?>, C extends Comparable<C>> 
	implements List<Phenotype<T, C>>, Iterable<Phenotype<T, C>>, 
				RandomAccess, XMLSerializable
{
	private static final long serialVersionUID = -959370026031769242L;
	
	private final List<Phenotype<T, C>> _population;
	
	/**
	 * Creating a new <code>Population</code> with the prealocated population size.
	 * 
	 * @param size Prealocated population size.
	 */
	public Population(final int size) {
		_population = new ArrayList<Phenotype<T, C>>(size);
	}
	
	/**
	 * Creating a new <code>Population</code>.
	 */
	public Population() {
		_population = new ArrayList<Phenotype<T, C>>();
	}
	
	/**
	 * Add <code>Phenotype</code> to the <code>Population</code>.
	 * 
	 * @param phenotype <code>Phenotype</code> to be add.
	 */
	@Override
	public boolean add(final Phenotype<T, C> phenotype) {
		notNull(phenotype, "Phenotype");
		return _population.add(phenotype);
	}
	
	/**
	 * Add <code>Phenotype</code> to the <code>Population</code>.
	 * 
	 * @param index Index of the 
	 * @param phenotype <code>Phenotype</code> to be add.
	 */
	@Override
	public void add(final int index, final Phenotype<T, C> phenotype) {
		notNull(phenotype, "Phenotype");
		_population.add(index, phenotype);
	}
	
	@Override
	public boolean addAll(final Collection<? extends Phenotype<T, C>> c) {
		return _population.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Phenotype<T, C>> c) {
		return _population.addAll(index, c);
	}

	@Override
	public Phenotype<T, C> get(final int index) {
		return _population.get(index);
	}
	
	@Override
	public Phenotype<T, C> set(final int index, final Phenotype<T, C> phenotype) {
		notNull(phenotype, "Phenotype");
		return _population.set(index, phenotype);
	}
	
	public void remove(final Phenotype<T, C> phenotype) {
		notNull(phenotype, "Phenotype");
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
	public Phenotype<T, C> remove(final int index) {
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
		Collections.sort(_population, new Comparator<Phenotype<T, C>>() {
			@Override 
			public int compare(final Phenotype<T, C> that, final Phenotype<T, C> other) {
				return -that.compareTo(other);
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
	public Iterator<Phenotype<T, C>> iterator() {
		return _population.iterator();
	}
	
	@Override
	public ListIterator<Phenotype<T, C>> listIterator() {
		return _population.listIterator();
	}
	
	@Override
	public ListIterator<Phenotype<T, C>> listIterator(final int index) {
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
	public List<Phenotype<T, C>> subList(final int fromIndex, final int toIndex) {
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
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		for (Phenotype<?, ?> pt : this) {
			out.append(pt.toString() + "\n");
		}
		
		return out.toString();
	}
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<Population> 
	XML = new XMLFormat<Population>(Population.class) {
		@Override
		public Population newInstance(final Class<Population> cls, final InputElement xml)
			throws XMLStreamException
		{
			final int length = xml.getAttribute("length", 0);
			Population p = new Population(length);
			for (int i = 0; i < length; ++i) {
				Phenotype pt = xml.getNext();
				p.add(pt);
			}
			return p;
		}
		@Override
		public void write(final Population p, final OutputElement xml)
			throws XMLStreamException 
		{
			xml.setAttribute("length", p.size());
			for (int i = 0; i < p.size(); ++i) {
				xml.add(p.get(i)); 
			}
		}
		@Override
		public void read(final InputElement xml, final Population p) 
			throws XMLStreamException 
		{
		}
	};
	
}



