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
import static org.jenetics.EnumGene.Gene;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

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

import org.jenetics.internal.util.HashBuilder;
import org.jenetics.internal.util.cast;
import org.jenetics.internal.util.jaxb;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.bit;


/**
 * The mutable methods of the {@link AbstractChromosome} has been overridden so
 * that no invalid permutation will be created.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-04-13 $</em>
 */
@XmlJavaTypeAdapter(PermutationChromosome.Model.Adapter.class)
public final class PermutationChromosome<T>
	extends AbstractChromosome<EnumGene<T>>
	implements Serializable
{
	private static final long serialVersionUID = 2L;

	private ISeq<T> _validAlleles;

	public PermutationChromosome(final ISeq<EnumGene<T>> genes) {
		super(genes);
		_validAlleles = genes.get(0).getValidAlleles();
		_valid = true;
	}

	public ISeq<T> getValidAlleles() {
		return _validAlleles;
	}

	/**
	 * Check if this chromosome represents still a valid permutation.
	 */
	@Override
	public boolean isValid() {
		if (_valid == null) {
			byte[] check = new byte[length()/8 + 1];
			Arrays.fill(check, (byte)0);

			boolean valid = super.isValid();
			for (int i = 0; i < length() && valid; ++i) {
				final int value = _genes.get(i).getAlleleIndex();
				if (value >= 0 && value < length()) {
					if (bit.get(check, value)) {
						valid = false;
					} else {
						bit.set(check, value, true);
					}
				} else {
					valid = false;
				}
			}

			_valid = valid;
		}

		return _valid;
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public PermutationChromosome<T> newInstance() {
		return of(_validAlleles);
	}

	@Override
	public PermutationChromosome<T> newInstance(final ISeq<EnumGene<T>> genes) {
		return new PermutationChromosome<>(genes);
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass())
				.and(super.hashCode())
				.value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		out.append(_genes.get(0).getAllele());
		for (int i = 1; i < length(); ++i) {
			out.append("|").append(_genes.get(i).getAllele());
		}
		return out.toString();
	}

	/**
	 * Create a new, random chromosome with the given valid alleles.
	 *
	 * @param <T> the gene type of the chromosome
	 * @param alleles the valid alleles used for this permutation arrays.
	 * @return a new chromosome with the given alleles
	 */
	public static <T> PermutationChromosome<T> of(final ISeq<? extends T> alleles) {
		final PermutationChromosome<T> chromosome = new PermutationChromosome<>(
			new Array<EnumGene<T>>(alleles.length())
					.fill(Gene(alleles))
					.shuffle()
					.toISeq()
		);
		chromosome._validAlleles = cast.apply(alleles);

		return chromosome;
	}

	/**
	 * Create a new, random chromosome with the given valid alleles.
	 *
	 * @since 2.0
	 * @param <T> the gene type of the chromosome
	 * @param alleles the valid alleles used for this permutation arrays.
	 * @return a new chromosome with the given alleles
	 */
	@SafeVarargs
	public static <T> PermutationChromosome<T> of(final T... alleles) {
		return of(Array.of(alleles).toISeq());
	}

	/**
	 * Create a integer permutation chromosome with the given length.
	 *
	 * @param length the chromosome length.
	 * @return a integer permutation chromosome with the given length.
	 */
	public static PermutationChromosome<Integer> ofInteger(final int length) {
		return ofInteger(0, length);
	}

	/**
	 * Create a integer permutation chromosome with the given length.
	 *
	 * @since 2.0
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
		return of(new Array<Integer>(end - start).fill(Int(start, 1)).toISeq());
	}

	private static Factory<Integer> Int(final int start, final int step) {
		return new Factory<Integer>() {
			private int _value = start;

			@Override
			public Integer newInstance() {
				return next();
			}

			private int next() {
				final int next = _value;
				_value += step;
				return next;
			}
		};
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

		final Array<EnumGene<T>> genes = new Array<>(_validAlleles.length());
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
			extends XmlAdapter<Model, PermutationChromosome>
		{
			@Override
			public Model marshal(final PermutationChromosome pc)
				throws Exception
			{
				final Model model = new Model();
				model.length = pc.length();
				model.alleles = pc.getValidAlleles()
					.map(jaxb.Marshaller(pc.getValidAlleles().get(0)))
					.asList();
				model.order = pc.toSeq().map(AlleleIndex).asList();

				return model;
			}

			@Override
			public PermutationChromosome unmarshal(final Model model)
				throws Exception
			{
				final ISeq alleles = Array.of(model.alleles)
					.map(jaxb.Unmarshaller(model.alleles.get(0)))
					.toISeq();

				return new PermutationChromosome(
					Array.of(model.order).map(Gene(alleles)).toISeq()
				);
			}
		}

		private static final Function<EnumGene<?>, Integer> AlleleIndex =
			new Function<EnumGene<?>, Integer>() {
				@Override
				public Integer apply(final EnumGene<?> value) {
					return value.getAlleleIndex();
				}
			};

		private static Function<Integer, EnumGene<Object>>
		Gene(final ISeq<Object> alleles) {
			return new Function<Integer, EnumGene<Object>>() {
				@Override
				public EnumGene<Object> apply(final Integer value) {
					return new EnumGene<>(value, alleles);
				}
			};
		}
	}
}
