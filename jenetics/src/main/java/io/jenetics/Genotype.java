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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Verifiable;

/**
 * The central class the GA is working with, is the {@code Genotype}. It is the
 * structural representative of an individual. This class is the encoded problem
 * solution with one to many {@link Chromosome}.
 * <p>
 * <img alt="Genotype" src="doc-files/Genotype.svg" width="400" height="252" >
 * </p>
 * The chromosomes of a genotype doesn't have to have necessarily the same size.
 * It is only required that all genes are from the same type and the genes within
 * a chromosome have the same constraints; e. g. the same min- and max values
 * for number genes.
 *
 * <pre>{@code
 * final Genotype<DoubleGene> genotype = Genotype.of(
 *     DoubleChromosome.of(0.0, 1.0, 8),
 *     DoubleChromosome.of(1.0, 2.0, 10),
 *     DoubleChromosome.of(0.0, 10.0, 9),
 *     DoubleChromosome.of(0.1, 0.9, 5)
 * );
 * }</pre>
 * The code snippet above creates a genotype with the same structure as shown in
 * the figure above. In this example the {@link DoubleGene} has been chosen as
 * gene type.
 *
 * @see Chromosome
 * @see Phenotype
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
 */
public final class Genotype<G extends Gene<?, G>>
	implements
		Factory<Genotype<G>>,
		Iterable<Chromosome<G>>,
		Verifiable,
		Serializable
{
	private static final long serialVersionUID = 3L;

	private final ISeq<Chromosome<G>> _chromosomes;

	//Caching isValid value.
	private volatile Boolean _valid = null;

	/**
	 * Create a new Genotype from a given sequence of {@code Chromosomes}.
	 *
	 * @param chromosomes The {@code Chromosome} array the {@code Genotype}
	 *         consists of.
	 * @throws NullPointerException if {@code chromosomes} is null or one of its
	 *         element.
	 * @throws IllegalArgumentException if {@code chromosome.length == 0}.
	 */
	Genotype(final ISeq<? extends Chromosome<G>> chromosomes) {
		if (chromosomes.isEmpty()) {
			throw new IllegalArgumentException("No chromosomes given.");
		}

		_chromosomes = ISeq.upcast(chromosomes);
	}

	/**
	 * Return the chromosome at the given index. It is guaranteed, that the
	 * returned chromosome is not null.
	 *
	 * @param index Chromosome index.
	 * @return The Chromosome.
	 * @throws IndexOutOfBoundsException if
	 *         {@code (index < 0 || index >= _length)}.
	 */
	public Chromosome<G> getChromosome(final int index) {
		assert _chromosomes != null;
		assert _chromosomes.get(index) != null;

		return _chromosomes.get(index);
	}

	/**
	 * Return the first chromosome. This is a shortcut for
	 * <pre>{@code
	 * final Genotype<DoubleGene>; gt = ...
	 * final Chromosome<DoubleGene> chromosome = gt.getChromosome(0);
	 * }</pre>
	 *
	 * @return The first chromosome.
	 */
	public Chromosome<G> getChromosome() {
		assert _chromosomes != null;
		assert _chromosomes.get(0) != null;

		return _chromosomes.get(0);
	}

	/**
	 * Return the first {@link Gene} of the first {@link Chromosome} of this
	 * {@code Genotype}. This is a shortcut for
	 * <pre>{@code
	 * final Genotype<DoubleGene> gt = ...
	 * final DoubleGene gene = gt.getChromosome(0).getGene(0);
	 * }</pre>
	 *
	 * @return the first {@link Gene} of the first {@link Chromosome} of this
	 *         {@code Genotype}.
	 */
	public G getGene() {
		assert _chromosomes != null;
		assert _chromosomes.get(0) != null;

		return _chromosomes.get(0).getGene();
	}

	/**
	 * Return the gene from the given chromosome- and gene index. This is a
	 * shortcut for {@code gt.getChromosome(chromosomeIndex).getGene(geneIndex)}.
	 *
	 * @since 3.0
	 *
	 * @param chromosomeIndex the chromosome index
	 * @param geneIndex the gene index within the chromosome
	 * @return the gene with the given indexes
	 * @throws IndexOutOfBoundsException if the given indexes are not within the
	 *         allowed range
	 */
	public G get(final int chromosomeIndex, final int geneIndex) {
		return getChromosome(chromosomeIndex).getGene(geneIndex);
	}

	/**
	 * Return the chromosome at the given index. It is guaranteed, that the
	 * returned chromosome is not null.
	 *
	 * @see #getChromosome(int)
	 * @since 4.0
	 *
	 * @param chromosomeIndex Chromosome index.
	 * @return The Chromosome.
	 * @throws IndexOutOfBoundsException if
	 *         {@code (index < 0 || index >= _length)}.
	 */
	public Chromosome<G> get(final int chromosomeIndex) {
		return getChromosome(chromosomeIndex);
	}

	public ISeq<Chromosome<G>> toSeq() {
		return _chromosomes;
	}

	@Override
	public Iterator<Chromosome<G>> iterator() {
		return _chromosomes.iterator();
	}

	/**
	 * Returns a sequential {@code Stream} of chromosomes with this genotype as
	 * its source.
	 *
	 * @since 3.4
	 *
	 * @return a sequential {@code Stream} of chromosomes
	 */
	public Stream<Chromosome<G>> stream() {
		return _chromosomes.stream();
	}

	/**
	 * Getting the number of chromosomes of this genotype.
	 *
	 * @return number of chromosomes.
	 */
	public int length() {
		return _chromosomes.length();
	}

	/**
	 * Return the number of genes this genotype consists of. This is the sum of
	 * the number of genes of the genotype chromosomes.
	 *
	 * @return Return the number of genes this genotype consists of.
	 */
	public int geneCount() {
		int count = 0;
		for (int i = 0, n = _chromosomes.length(); i < n; ++i) {
			count += _chromosomes.get(i).length();
		}
		return count;
	}

	/**
	 * Test if this genotype is valid. A genotype is valid if all its
	 * {@link Chromosome}s are valid.
	 *
	 * @return true if this genotype is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		Boolean valid = _valid;
		if (valid == null) {
			valid = _chromosomes.forAll(Verifiable::isValid);
			_valid = valid;
		}

		return _valid;
	}

	/**
	 * Return a new, random genotype by creating new, random chromosomes (calling
	 * the {@link Chromosome#newInstance()} method) from the chromosomes of this
	 * genotype.
	 */
	@Override
	public Genotype<G> newInstance() {
		return new Genotype<>(_chromosomes.map(Factory::newInstance));
	}

	@Override
	public int hashCode() {
		return hash(_chromosomes);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Genotype &&
			Objects.equals(_chromosomes, ((Genotype)obj)._chromosomes);
	}

	@Override
	public String toString() {
		return _chromosomes.toString();
	}

	/**
	 * Create a new {@code Genotype} from a given array of {@code Chromosomes}.
	 *
	 * @since 3.0
	 *
	 * @param <G> the gene type
	 * @param first the first {@code Chromosome} of the {@code Genotype}
	 * @param rest the rest of the genotypes chromosomes.
	 * @return a new {@code Genotype} from the given chromosomes
	 * @throws NullPointerException if {@code chromosomes} is {@code null} or
	 *         one of its element.
	 */
	@SafeVarargs
	public static <G extends Gene<?, G>> Genotype<G> of(
		final Chromosome<G> first,
		final Chromosome<G>... rest
	) {
		final MSeq<Chromosome<G>> seq = MSeq.ofLength(1 + rest.length);
		seq.set(0, first);
		for (int i = 0; i < rest.length; ++i) {
			seq.set(i + 1, rest[i]);
		}
		return new Genotype<>(seq.toISeq());
	}

	/**
	 * Create a new {@code Genotype} which consists of {@code n} chromosomes,
	 * which are created by the given {@code factory}. This method can be used
	 * for easily creating a <i>gene matrix</i>. The following example will
	 * create a 10x5 {@code DoubleGene} <i>matrix</i>.
	 *
	 * <pre>{@code
	 * final Genotype<DoubleGene> gt = Genotype
	 *     .of(DoubleChromosome.of(0.0, 1.0, 10), 5);
	 * }</pre>
	 *
	 * @since 3.0
	 *
	 * @param <G> the gene type
	 * @param factory the factory which creates the chromosomes this genotype
	 *        consists of
	 * @param n the number of chromosomes this genotype consists of
	 * @return new {@code Genotype} containing {@code n} chromosomes
	 * @throws IllegalArgumentException if {@code n < 1}.
	 * @throws NullPointerException if the {@code factory} is {@code null}.
	 */
	public static <G extends Gene<?, G>> Genotype<G>
	of(final Factory<? extends Chromosome<G>> factory, final int n) {
		final ISeq<Chromosome<G>> ch = ISeq.of(factory::newInstance, n);
		return new Genotype<>(ch);
	}

	/**
	 * Create a new {@code Genotype} from a given array of {@code chromosomes}.
	 *
	 * @since 3.0
	 *
	 * @param <G> the gene type
	 * @param chromosomes the {@code Chromosome}s the returned genotype consists
	 *        of
	 * @return a new {@code Genotype} from the given chromosomes
	 * @throws NullPointerException if {@code chromosomes} is {@code null} or
	 *         one of its element.
	 * @throws IllegalArgumentException if {@code chromosome.length() < 1}.
	 */
	public static <G extends Gene<?, G>> Genotype<G>
	of(final Iterable<? extends Chromosome<G>> chromosomes) {
		return new Genotype<>(ISeq.of(chromosomes));
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.GENOTYPE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		writeInt(_chromosomes.length(), out);
		for (var ch : _chromosomes) {
			out.writeObject(ch);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Genotype read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		final int length = readInt(in);
		final MSeq<Chromosome> chromosomes = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			chromosomes.set(i, (Chromosome)in.readObject());
		}

		return new Genotype(chromosomes.asISeq());
	}

}
