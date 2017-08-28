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
package io.jenetics;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * A population is a collection of Phenotypes. Mainly used for JAXB marshalling
 * a collection of {@link Phenotype}, aka population.
 *
 * <p>
 * <strong>This class is not synchronized.</strong> If multiple threads access
 * a {@code Population} concurrently, and at least one of the threads modifies
 * it, it <strong>must</strong> be synchronized externally.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
 */
public final class Population<G extends Gene<?, G>, C extends Comparable<? super C>>
	implements ISeq<Phenotype<G, C>>, Serializable
{
	private static final long serialVersionUID = 3L;

	private ISeq<Phenotype<G, C>> _population;

	/**
	 * Constructs a population containing the elements of the specified collection,
	 * in the order they are returned by the collection's iterator.
	 *
	 * @param population the collection whose elements are to be placed into
	 *         this list.
	 * @throws NullPointerException if the specified population is {@code null}.
	 */
	public Population(final ISeq<Phenotype<G, C>> population) {
		_population = requireNonNull(population);
	}

	@Override
	public ISeq<Phenotype<G, C>> append(
		final Iterable<? extends Phenotype<G, C>> values
	) {
		return _population.append(values);
	}

	@SafeVarargs
	@Override
	public final ISeq<Phenotype<G, C>> append(final Phenotype<G, C>... values) {
		return _population.append(values);
	}

	@Override
	public List<Phenotype<G, C>> asList() {
		return _population.asList();
	}

	@Override
	public boolean contains(final Object element) {
		return _population.contains(element);
	}

	@Override
	public MSeq<Phenotype<G, C>> copy() {
		return _population.copy();
	}

	@Override
	public boolean equals(final Object object) {
		return _population.equals(object);
	}

	@Override
	public boolean forAll(final Predicate<? super Phenotype<G, C>> predicate) {
		return _population.forAll(predicate);
	}

	@Override
	public void forEach(final Consumer<? super Phenotype<G, C>> action) {
		_population.forEach(action);
	}

	@Override
	public Phenotype<G, C> get(final int index) {
		return _population.get(index);
	}

	@Override
	public int hashCode() {
		return _population.hashCode();
	}

	@Override
	public int indexOf(final Object element) {
		return _population.indexOf(element);
	}

	@Override
	public int indexOf(final Object element, final int start) {
		return _population.indexOf(element, start);
	}

	@Override
	public int indexOf(final Object element, final int start, final int end) {
		return _population.indexOf(element, start, end);
	}

	@Override
	public int indexWhere(final Predicate<? super Phenotype<G, C>> predicate) {
		return _population.indexWhere(predicate);
	}

	@Override
	public int indexWhere(
		final Predicate<? super Phenotype<G, C>> predicate,
		final int start
	) {
		return _population.indexWhere(predicate, start);
	}

	@Override
	public int indexWhere(
		final Predicate<? super Phenotype<G, C>> predicate,
		final int start,
		final int end
	) {
		return _population.indexWhere(predicate, start, end);
	}

	@Override
	public boolean isEmpty() {
		return _population.isEmpty();
	}

	@Override
	public boolean isSorted() {
		return _population.isSorted();
	}

	@Override
	public boolean isSorted(
		final Comparator<? super Phenotype<G, C>> comparator
	) {
		return _population.isSorted(comparator);
	}

	@Override
	public Iterator<Phenotype<G, C>> iterator() {
		return _population.iterator();
	}

	@Override
	public int lastIndexOf(final Object element) {
		return _population.lastIndexOf(element);
	}

	@Override
	public int lastIndexOf(final Object element, final int end) {
		return _population.lastIndexOf(element, end)
			;}

	@Override
	public int lastIndexOf(final Object element, final int start, final int end) {
		return _population.lastIndexOf(element, start, end);
	}

	@Override
	public int lastIndexWhere(final Predicate<? super Phenotype<G, C>> predicate) {
		return _population.lastIndexWhere(predicate);
	}

	@Override
	public int lastIndexWhere(
		final Predicate<? super Phenotype<G, C>> predicate,
		final int end
	) {
		return _population.lastIndexWhere(predicate, end);
	}

	@Override
	public int lastIndexWhere(
		final Predicate<? super Phenotype<G, C>> predicate,
		final int start,
		final int end
	) {
		return _population.lastIndexWhere(predicate, start, end);
	}

	@Override
	public int length() {
		return _population.length();
	}

	@Override
	public ListIterator<Phenotype<G, C>> listIterator() {
		return _population.listIterator();
	}

	@Override
	public <B> ISeq<B> map(
		final Function<? super Phenotype<G, C>, ? extends B> mapper
	) {
		return _population.map(mapper);
	}

	public static <T> ISeq<T> of(
		final Supplier<? extends T> supplier,
		final int length
	) {
		return ISeq.of(supplier, length);
	}

	public static <T> ISeq<T> of(final Iterable<? extends T> values) {
		return ISeq.of(values);
	}

	@Override
	public Stream<Phenotype<G, C>> parallelStream() {
		return _population.parallelStream();
	}

	@Override
	public ISeq<Phenotype<G, C>> prepend(
		final Iterable<? extends Phenotype<G, C>> values
	) {
		return _population.prepend(values);
	}

	@SafeVarargs
	@Override
	public final ISeq<Phenotype<G, C>> prepend(final Phenotype<G, C>... values) {
		return _population.prepend(values);
	}

	@Override
	public int size() {
		return _population.size();
	}

	@Override
	public Spliterator<Phenotype<G, C>> spliterator() {
		return _population.spliterator();
	}

	@Override
	public Stream<Phenotype<G, C>> stream() {
		return _population.stream();
	}

	@Override
	public ISeq<Phenotype<G, C>> subSeq(final int start) {
		return _population.subSeq(start);
	}

	@Override
	public ISeq<Phenotype<G, C>> subSeq(final int start, final int end) {
		return _population.subSeq(start, end);
	}

	@Override
	public Object[] toArray() {
		return _population.toArray();
	}

	@Override
	public String toString(
		final String prefix,
		final String separator,
		final String suffix
	) {
		return _population.toString(prefix, separator, suffix);
	}

	@Override
	public String toString(final String separator) {
		return _population.toString(separator);
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
			(Supplier<List<Phenotype<G, C>>>)ArrayList::new,
			List::add,
			(left, right) -> { left.addAll(right); return left; },
			l -> new Population<>(ISeq.of(l))
		);
	}

		/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();
		out.writeInt(length());

		for (Phenotype<G, C> pt : _population) {
			out.writeObject(pt);
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final MSeq<Phenotype<G, C>> population = MSeq.ofLength(in.readInt());

		for (int i = 0; i < population.length(); ++i) {
			population.set(i, (Phenotype<G, C>) in.readObject());
		}

		_population = new Population<>(population.toISeq());
	}

}
