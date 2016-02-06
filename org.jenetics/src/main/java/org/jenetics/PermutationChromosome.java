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

import static java.lang.String.format;
import static org.jenetics.internal.util.bit.getAndSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.math.base;
import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.array;
import org.jenetics.internal.util.bit;
import org.jenetics.internal.util.jaxb;
import org.jenetics.internal.util.require;

import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.MSeq;
import org.jenetics.util.Seq;

/**
 * This chromosome can be used to model permutations of a given (sub) set of
 * alleles.
 *
 * <pre>{@code
 * final ISeq<String> alleles = ISeq.of("one", "two", "three", "four", "five");
 *
 * // Create a new randomly permuted chromosome from the given alleles.
 * final PermutationChromosome<String> ch1 = PermutationChromosome.of(alleles);
 * System.out.println(ch1);
 * System.out.println(ch1.newInstance());
 *
 * // Create a new randomly permuted chromosome from a subset of the given alleles.
 * final PermutationChromosome<String> ch2 = PermutationChromosome.of(alleles, 3);
 * System.out.println(ch2);
 * System.out.println(ch2.newInstance());
 *
 * // Console output:
 * // > three|two|one|five|four
 * // > two|one|four|five|three
 * // > three|one|five
 * // > five|three|one
 * }</pre>
 *
 * <i>
 * The factory methods of the {@link AbstractChromosome} has been overridden so
 * that no invalid permutation will be created.
 * </i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version !__version__!
 */
@XmlJavaTypeAdapter(PermutationChromosome.Model.Adapter.class)
public final class PermutationChromosome<T>
	extends AbstractChromosome<EnumGene<T>>
	implements Serializable
{
	private static final long serialVersionUID = 2L;

	private ISeq<T> _validAlleles;

	private PermutationChromosome(
		final ISeq<EnumGene<T>> genes,
		final Boolean valid
	) {
		super(genes);

		assert !genes.isEmpty();
		_validAlleles = genes.get(0).getValidAlleles();
		_valid = valid;
	}

	/**
	 * Create a new {@code PermutationChromosome} from the given {@code genes}.
	 * If the given {@code genes} sequence contains duplicate entries, the
	 * created {@code PermutationChromosome} will be invalid
	 * ({@code ch.isValid() == false}).
	 *
	 * @param genes the enum genes the new chromosome consists of
	 * @throws NullPointerException if the given {@code genes} are null
	 * @throws IllegalArgumentException if the given {@code genes} sequence is
	 *         {@code null}
	 */
	public PermutationChromosome(final ISeq<EnumGene<T>> genes) {
		this(genes, null);
	}

	/**
	 * Return the sequence of valid alleles of this chromosome.
	 *
	 * @return the sequence of valid alleles of this chromosome
	 */
	public ISeq<T> getValidAlleles() {
		return _validAlleles;
	}

	/**
	 * Check if this chromosome represents still a valid permutation (or subset)
	 * of the given valid alleles.
	 */
	@Override
	public boolean isValid() {
		if (_valid == null) {
			final byte[] check = bit.newArray(_validAlleles.length());
			_valid = _genes.forAll(g -> !getAndSet(check, g.getAlleleIndex()));
		}

		return _valid;
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public PermutationChromosome<T> newInstance() {
		return of(_validAlleles, length());
	}

	@Override
	public PermutationChromosome<T> newInstance(final ISeq<EnumGene<T>> genes) {
		return new PermutationChromosome<>(genes);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
				.and(super.hashCode())
				.value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}

	@Override
	public String toString() {
		return _genes.asList().stream()
			.map(g -> g.getAllele().toString())
			.collect(Collectors.joining("|"));
	}

	/**
	 * Create a new, random chromosome with the given valid alleles and the
	 * desired length.
	 * <p>
	 * The following example shows how to create a {@code PermutationChromosome}
	 * for encoding a sub-set problem (of a fixed {@code length}).
	 * <pre>{@code
	 * final ISeq<String> basicSet = ISeq.of("a", "b", "c", "d", "e", "f");
	 *
	 * // The chromosome has a length of 3 and will only contain values from the
	 * // given basic-set, with no duplicates.
	 * final PermutationChromosome<String> ch = PermutationChromosome.of(basicSet, 3);
	 * }</pre>
	 *
	 * @since !__version__!
	 *
	 * @param validAlleles the base-set of the valid alleles
	 * @param length the length of the created chromosomes
	 * @param <T> the allele type
	 * @return a new chromosome with the given valid alleles and the desired
	 *         length
	 * @throws IllegalArgumentException if the given {@code length} is smaller
	 *         than one or greater than {@code validAlleles.length()}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> PermutationChromosome<T> of(
		final ISeq<? extends T> validAlleles,
		final int length
	) {
		require.positive(length);
		if (length > validAlleles.size()) {
			throw new IllegalArgumentException(format(
				"The sub-set size must be be greater then the base-set: %d > %d",
				length, validAlleles.size()
			));
		}

		final int[] subset = array.shuffle(base.subset(validAlleles.size(), length));
		return new PermutationChromosome<>(
			IntStream.of(subset)
				.mapToObj(i -> EnumGene.of(i, validAlleles))
				.collect(ISeq.toISeq()),
			true
		);
	}

	/**
	 * Create a new, random chromosome with the given valid alleles.
	 *
	 * @param <T> the gene type of the chromosome
	 * @param validAlleles the valid alleles used for this permutation arrays.
	 * @return a new chromosome with the given alleles
	 */
	public static <T> PermutationChromosome<T>
	of(final ISeq<? extends T> validAlleles) {
		return of(validAlleles, validAlleles.size());
	}

	/**
	 * Create a new, random chromosome with the given valid alleles.
	 *
	 * @since 2.0
	 * @param <T> the gene type of the chromosome
	 * @param alleles the valid alleles used for this permutation arrays.
	 * @return a new chromosome with the given alleles
	 * @throws IllegalArgumentException if the given allele array is empty
	 * @throws NullPointerException if one of the alleles is {@code null}
	 */
	@SafeVarargs
	public static <T> PermutationChromosome<T> of(final T... alleles) {
		return of(ISeq.of(alleles));
	}

	/**
	 * Create a integer permutation chromosome with the given length.
	 *
	 * @param length the chromosome length.
	 * @return a integer permutation chromosome with the given length.
	 * @throws IllegalArgumentException if the given length is smaller than one.
	 */
	public static PermutationChromosome<Integer> ofInteger(final int length) {
		return ofInteger(0, require.positive(length));
	}

	/**
	 * Create an integer permutation chromosome with the given range.
	 *
	 * @since 2.0
	 *
	 * @param start the start of the integer range (inclusively) of the returned
	 *        chromosome.
	 * @param end the end of the integer range (exclusively) of the returned
	 *        chromosome.
	 * @return a integer permutation chromosome with the given integer range
	 *         values.
	 * @throws java.lang.IllegalArgumentException if {@code end <= start}
	 */
	public static PermutationChromosome<Integer>
	ofInteger(final int start, final int end) {
		if (end <= start) {
			throw new IllegalArgumentException(format(
				"end <= start: %d <= %d", end, start
			));
		}

		return ofInteger(IntRange.of(start, end), end - start);
	}

	/**
	 * Create an integer permutation chromosome with the given range and length
	 *
	 * @since !__version__!
	 *
	 * @param range the value range
	 * @param length the chromosome length
	 * @return a new integer permutation chromosome
	 */
	public static PermutationChromosome<Integer>
	ofInteger(final IntRange range, final int length) {
		return of(
			IntStream.range(range.getMin(), range.getMax())
				.mapToObj(Integer::new)
				.collect(ISeq.toISeq()),
			length
		);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeObject(_validAlleles);
		for (EnumGene<?> gene : _genes) {
			out.writeInt(gene.getAlleleIndex());
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		_validAlleles = (ISeq<T>)in.readObject();

		final MSeq<EnumGene<T>> genes = MSeq.ofLength(_validAlleles.length());
		for (int i = 0; i < _validAlleles.length(); ++i) {
			genes.set(i, new EnumGene<>(in.readInt(), _validAlleles));
		}

		_genes = genes.toISeq();
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "permutation-chromosome")
	@XmlType(name = "org.jenetics.PermutationChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute
		public int length;

		@XmlElementWrapper(name = "valid-alleles")
		@XmlElement(name = "allele")
		public List<Object> alleles;

		@XmlList
		@XmlElement(name = "order")
		public List<Integer> order;

		public static final class Adapter
			extends XmlAdapter<Model, PermutationChromosome> {
			@Override
			public Model marshal(final PermutationChromosome pc)
				throws Exception {
				final Model model = new Model();
				model.length = pc.length();
				model.alleles = pc.getValidAlleles()
					.map(jaxb.Marshaller(pc.getValidAlleles().get(0)))
					.asList();
				model.order = ((Seq<EnumGene<?>>)pc.toSeq())
					.map(EnumGene::getAlleleIndex)
					.asList();

				return model;
			}

			@Override
			public PermutationChromosome unmarshal(final Model model)
				throws Exception {
				final ISeq alleles = ISeq.of(model.alleles)
					.map(jaxb.Unmarshaller(model.alleles.get(0)));

				return new PermutationChromosome(
					ISeq.of(model.order).map(Gene(alleles))
				);
			}
		}

		private static Function<Integer, EnumGene<Object>>
		Gene(final ISeq<Object> alleles) {
			return value -> new EnumGene<>(value, alleles);
		}
	}
}
