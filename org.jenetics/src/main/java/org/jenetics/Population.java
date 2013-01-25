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

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import javolution.context.ConcurrentContext;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Concurrency;
import org.jenetics.util.Copyable;
import org.jenetics.util.Factory;
import org.jenetics.util.arrays;

/**
 * A population is a collection of Phenotypes.
 *
 * <strong>This class is not synchronized.</strong> If multiple threads access
 * a {@code Population} concurrently, and at least one of the threads modifies
 * it, it <strong>must</strong> be synchronized externally.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.2 &mdash; <em>$Date: 2013-01-25 $</em>
 */
public class Population<G extends Gene<?, G>, C extends Comparable<? super C>>
	implements
		List<Phenotype<G, C>>,
		Copyable<Population<G, C>>,
		RandomAccess,
		XMLSerializable
{
	private static final long serialVersionUID = 1L;

	private final List<Phenotype<G, C>> _population;

	/**
	 * Constructs a population containing the elements of the specified collection,
	 * in the order they are returned by the collection's iterator.
	 *
	 * @param population the collection whose elements are to be placed into
	 *         this list.
	 * @throws NullPointerException if the specified population is {@code null}.
	 */
	public Population(final Collection<? extends Phenotype<G, C>> population) {
		_population = new ArrayList<>(population);
	}

	/**
	 * Creating a new <code>Population</code> with the preallocated population
	 * size.
	 *
	 * @param size Preallocated population size.
	 * @throws IllegalArgumentException if the specified initial capacity is
	 *          negative
	 */
	public Population(final int size) {
		_population = new ArrayList<>(size + 1);
	}

	/**
	 * Creating a new <code>Population</code>.
	 */
	public Population() {
		_population = new ArrayList<>();
	}

	/**
	 * Fills the population with individuals created by the given factory.
	 *
	 * @param factory the {@code Phenotype} factory.
	 * @param count the number of individuals to add to this population.
	 * @return return this population, for command chanining.
	 */
	public Population<G, C> fill(
		final Factory<? extends Phenotype<G, C>> factory,
		final int count
	) {
		// Serial version.
		if (ConcurrentContext.getConcurrency() == 0) {
			for (int i = 0; i < count; ++i) {
				_population.add(factory.newInstance());
			}

		// Parallel version.
		} else {
			final PhenotypeArray<G, C> array = new PhenotypeArray<>(count);
			fill(factory, array._array);
			_population.addAll(array);
		}

		return this;
	}

	private static <
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	void fill(
		final Factory<? extends Phenotype<G, C>> factory,
		final Object[] array
	) {
		try (final Concurrency c = Concurrency.start()) {
			final int concurrency = ConcurrentContext.getConcurrency() + 1;
			final int[] parts = arrays.partition(array.length, concurrency);

			for (int i = 0; i < parts.length - 1; ++i) {
				final int part = i;

				c.execute(new Runnable() { @Override public void run() {
					for (int j = parts[part + 1]; --j >= parts[part];) {
						array[j] = factory.newInstance();
					}
				}});
			}
		}
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
		sort(Optimize.MAXIMUM.<C>descending());
	}

	public void sort(final Comparator<? super C> comparator) {
		quicksort(0, size() - 1, comparator);
	}


	private void quicksort(
		final int left, final int right,
		final Comparator<? super C> comparator
	) {
		if (right > left) {
			final int j = partition(left, right, comparator);
			quicksort(left, j - 1, comparator);
			quicksort(j + 1, right, comparator);
		}
	}

	private int partition(
		final int left, final int right,
		final Comparator<? super C> comparator
	) {
		final C pivot = _population.get(left).getFitness();
		int i = left;
		int j = right + 1;
		while (true) {
			do {
				++i;
			} while (
				i < right &&
				comparator.compare(_population.get(i).getFitness(), pivot) < 0
			);

			do {
				--j;
			} while (
				j > left &&
				comparator.compare(_population.get(j).getFitness(), pivot) > 0
			);
			if (j <= i) {
				break;
			}
			swap(i, j);
		}
		swap(left, j);

		return j;
	}

	private void swap(final int i, final int j) {
		_population.set(i, _population.set(j, _population.get(i)));
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
		final List<Genotype<G>> genotypes = new ArrayList<>(_population.size());
		for (Phenotype<G, C> phenotype : _population) {
			genotypes.add(phenotype.getGenotype());
		}
		return genotypes;
	}

	@Override
	public Population<G, C> copy() {
		return new Population<>(_population);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_population).value();
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
		return eq(_population, population._population);
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();

		for (Phenotype<?, ?> pt : this) {
			out.append(pt.toString()).append("\n");
		}

		return out.toString();
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
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
				p.add(xml.<Phenotype>getNext());
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


	private static final class PhenotypeArray<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		extends AbstractCollection<Phenotype<G, C>>
	{

		final Object[] _array;

		PhenotypeArray(final int size) {
			_array = new Object[size];
		}

		@SuppressWarnings("unchecked")
		@Override
		public Iterator<Phenotype<G, C>> iterator() {
			return Arrays.asList((Phenotype<G, C>[])_array).iterator();
		}

		@Override
		public int size() {
			return _array.length;
		}

		@Override
		public Object[] toArray() {
			return _array;
		}

	}


}




