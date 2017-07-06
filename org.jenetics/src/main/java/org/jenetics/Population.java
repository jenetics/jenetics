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
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.stream.Collector;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.jaxb;

import org.jenetics.util.Copyable;
import org.jenetics.util.Factory;

/**
 * A population is a collection of Phenotypes.
 *
 * <p>
 * <strong>This class is not synchronized.</strong> If multiple threads access
 * a {@code Population} concurrently, and at least one of the threads modifies
 * it, it <strong>must</strong> be synchronized externally.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0
 */
@XmlJavaTypeAdapter(Population.Model.Adapter.class)
public class Population<G extends Gene<?, G>, C extends Comparable<? super C>>
	implements
		List<Phenotype<G, C>>,
		Copyable<Population<G, C>>,
		RandomAccess,
		Serializable
{
	private static final long serialVersionUID = 2L;

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static final Population<?, ?> EMPTY =
		new Population(Collections.EMPTY_LIST);


	private final List<Phenotype<G, C>> _population;

	/**
	 * Private <i>primary</i> constructor which assigns the underlying
	 * population without copying and precondition check.
	 */
	private Population(final List<Phenotype<G, C>> population, boolean foo) {
		_population = requireNonNull(population);
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
		this(new ArrayList<>(population), true);
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
		this(new ArrayList<>(size), true);
	}

	/**
	 * Creating a new {@code Population}.
	 */
	public Population() {
		this(new ArrayList<>(), true);
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
		for (int i = 0; i < count; ++i) {
			_population.add(factory.newInstance());
		}
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

	@Override
	public Stream<Phenotype<G, C>> stream() {
		return _population.stream();
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
	public void populationSort() {
		sortWith(Optimize.MAXIMUM.descending());
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
		_population.sort((a, b) ->
			comparator.compare(a.getFitness(), b.getFitness())
		);
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

	@Override
	public Population<G, C> copy() {
		return new Population<>(new ArrayList<>(_population), true);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_population).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(p -> eq(_population, p._population));
	}

	@Override
	public String toString() {
		return _population.stream()
			.map(Object::toString)
			.collect(joining("\n", "", "\n"));
	}

	/**
	 * Return an empty population.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return an empty population
	 */
	@SuppressWarnings("unchecked")
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Population<G, C> empty() {
		return (Population<G, C>)EMPTY;
	}

	/**
	 * Returns a {@code Collector} that accumulates the input elements into a
	 * new {@code Population}.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a {@code Collector} which collects all the input elements into a
	 *         {@code Population}, in encounter order
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<Phenotype<G, C>, ?, Population<G, C>> toPopulation() {
		return Collector.of(
			Population::new,
			Population::add,
			(left, right) -> { left.addAll(right); return left; }
		);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "population")
	@XmlType(name = "org.jenetics.Population")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute(name = "size", required = true)
		public int size;

		@XmlElement(name = "phenotype", required = true)
		public List phenotypes;

		public static final class Adapter
			extends XmlAdapter<Model, Population>
		{
			@Override
			public Model marshal(final Population p) throws Exception {
				final Model model = new Model();
				model.size = p.size();
				if (!p.isEmpty()) {
					model.phenotypes = (List)p.stream()
						.map(jaxb.Marshaller(p.get(0)))
						.collect(toList());
				}

				return model;
			}

			@Override
			public Population unmarshal(final Model model) throws Exception {
				return (Population)model.phenotypes.stream()
					.map(jaxb.Unmarshaller(model.phenotypes.get(0)))
					.collect(toPopulation());
			}
		}

	}
}
