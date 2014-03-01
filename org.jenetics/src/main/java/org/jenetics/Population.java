/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.object.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.internal.util.HashBuilder;
import org.jenetics.internal.util.jaxb;
import org.jenetics.internal.util.model;

import org.jenetics.util.Array;
import org.jenetics.util.Copyable;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

/**
 * A population is a collection of Phenotypes.
 *
 * <p/>
 * <strong>This class is not synchronized.</strong> If multiple threads access
 * a {@code Population} concurrently, and at least one of the threads modifies
 * it, it <strong>must</strong> be synchronized externally.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date: 2014-03-01 $</em>
 */
@XmlJavaTypeAdapter(Population.Model.Adapter.class)
public class Population<G extends Gene<?, G>, C extends Comparable<? super C>>
	implements
		List<Phenotype<G, C>>,
		Copyable<Population<G, C>>,
		RandomAccess,
		XMLSerializable
{
	private static final long serialVersionUID = 1L;

	private final List<Phenotype<G, C>> _population;

	private Population(final List<Phenotype<G, C>> population) {
		_population = population;
	}

	/**
	 * Constructs a population containing the elements of the specified collection,
	 * in the order they are returned by the collection's iterator.
	 *
	 * @param population the collection whose elements are to be placed into
	 *         this list.
	 * @throws NullPointerException if the specified population is {@code null}.
	 */
	public Population(final Collection<Phenotype<G, C>> population) {
		this(new ArrayList<>(population));
	}

	/**
	 * Creating a new {@code Population} with the pre-allocated population
	 * size.
	 *
	 * @param size Pre-allocated population size.
	 * @throws IllegalArgumentException if the specified initial capacity is
	 *         negative
	 */
	public Population(final int size) {
		this(new ArrayList<Phenotype<G, C>>(size + 1));
	}

	/**
	 * Creating a new {@code Population}.
	 */
	public Population() {
		this(new ArrayList<Phenotype<G, C>>());
	}

	/**
	 * Fills the population with individuals created by the given factory.
	 *
	 * @param factory the {@code Phenotype} factory.
	 * @param count the number of individuals to add to this population.
	 * @return return this population, for command chaining.
	 */
	public Population<G, C> fill(
		final Factory<Phenotype<G, C>> factory,
		final int count
	) {
		for (int i = count; --i >= 0;) {
			_population.add(factory.newInstance());
		}
		//lists.fill(_population, factory, count);
		return this;
	}

	/**
	 * Add {@code Phenotype} to the {@code Population}.
	 *
	 * @param phenotype {@code Phenotype} to be add.
	 * @throws NullPointerException if the given {@code phenotype} is
	 *         {@code null}.
	 */
	@Override
	public boolean add(final Phenotype<G, C> phenotype) {
		requireNonNull(phenotype, "Phenotype");
		return _population.add(phenotype);
	}

	/**
	 * Add {@code Phenotype} to the {@code Population}.
	 *
	 * @param index Index of the
	 * @param phenotype {@code Phenotype} to be add.
	 * @throws NullPointerException if the given {@code phenotype} is
	 *         {@code null}.
	 */
	@Override
	public void add(final int index, final Phenotype<G, C> phenotype) {
		requireNonNull(phenotype, "Phenotype");
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
	public Phenotype<G, C> set(final int index, final Phenotype<G, C> pt) {
		requireNonNull(pt, "Phenotype");
		return _population.set(index, pt);
	}

	public void remove(final Phenotype<G, C> phenotype) {
		requireNonNull(phenotype, "Phenotype");
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
		sortWith(Optimize.MAXIMUM.<C>descending());
	}

	/**
	 * Sort this population according the order defined by the given
	 * {@code comparator}.
	 *
	 * @param comparator the comparator which defines the sorting order.
	 * @throws java.lang.NullPointerException if the {@code comparator} is
	 *         {@code null}.
	 *
	 * @deprecated This method conflicts with the default method of the
	 *             {@link java.util.List} interface introduced in Java 8. Use
	 *             {@link #sortWith(java.util.Comparator)} instead.
	 */
	@Deprecated
	public void sort(final Comparator<? super C> comparator) {
		sortWith(comparator);
	}

	/**
	 * Sort this population according the order defined by the given
	 * {@code comparator}.
	 *
	 * @param comparator the comparator which defines the sorting order.
	 * @throws java.lang.NullPointerException if the {@code comparator} is
	 *         {@code null}.
	 */
	public void sortWith(final Comparator<? super C> comparator) {
		quickSort(0, size() - 1, comparator);
	}


	private void quickSort(
		final int left, final int right,
		final Comparator<? super C> comparator
	) {
		if (right > left) {
			final int j = partition(left, right, comparator);
			quickSort(left, j - 1, comparator);
			quickSort(j + 1, right, comparator);
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
		return new Population<>(new ArrayList<>(_population));
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(_population).value();
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
			out.append(pt).append("\n");
		}

		return out.toString();
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

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

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.Population")
	@XmlType(name = "org.jenetics.Population")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute
		public int size;

		@XmlAnyElement
		public List<Object> phenotypes = new ArrayList<>();

		@model.ValueType(Genotype.class)
		@model.ModelType(Model.class)
		public static final class Adapter
			extends XmlAdapter<Model, Population>
		{
			@Override
			public Model marshal(final Population p) throws Exception {
				final Model model = new Model();
				model.size = p.size();
				if (p.size() > 0) {
					model.phenotypes = new Array<>(p.size()).setAll(p)
						.map(jaxb.Marshaller(p.get(0))).asList();
				}

				return model;
			}

			@Override
			public Population unmarshal(final Model model) throws Exception {
				final ISeq pt = Array.of(model.phenotypes)
					.map(jaxb.Unmarshaller).toISeq();

				return new Population(pt.asList());
			}
		}

		public static final Adapter Adapter = new Adapter();
	}
}
