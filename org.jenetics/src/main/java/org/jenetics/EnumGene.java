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
import static org.jenetics.internal.util.jaxb.Unmarshaller;
import static org.jenetics.internal.util.object.eq;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.internal.util.HashBuilder;
import org.jenetics.internal.util.cast;
import org.jenetics.internal.util.jaxb;
import org.jenetics.internal.util.model.ModelType;
import org.jenetics.internal.util.model.ValueType;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * <p>
 * Gene which holds enumerable (countable) genes. Will be used for combinatorial
 * problems in combination with the {@link PermutationChromosome}.
 * </p>
 * The following code shows how to create a combinatorial genotype factory which
 * can be used when creating an {@link GeneticAlgorithm} instance.
 * [code]
 * final ISeq〈Integer〉 alleles = Array.box(1, 2, 3, 4, 5, 6, 7, 8).toISeq();
 * final Factory〈Genotype〈EnumGene〈Integer〉〉〉 gtf = Genotype.of(
 *     PermutationChromosome.of(alleles)
 * );
 * [/code]
 *
 * The following code shows the assurances of the {@code EnumGene}.
 * [code]
 * final ISeq〈Integer〉 alleles = Array.box(1, 2, 3, 4, 5, 6, 7, 8).toISeq();
 * final EnumGene〈Integer〉 gene = new EnumGene<>(alleles, 5);
 *
 * assert(gene.getAlleleIndex() == 5);
 * assert(gene.getAllele() == gene.getValidAlleles().get(5));
 * assert(gene.getValidAlleles() == alleles);
 * [/code]
 *
 * @see PermutationChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date$</em>
 */
public final class EnumGene<A>
	implements
		Gene<A, EnumGene<A>>,
		Comparable<EnumGene<A>>
{

	private static final long serialVersionUID = 1L;

	private final ISeq<A> _validAlleles;
	private final int _alleleIndex;

	/**
	 * Create a new enum gene from the given valid genes and the chosen allele
	 * index.
	 *
	 * @param validAlleles the sequence of valid alleles.
	 * @param alleleIndex the index of the allele for this gene.
	 * @throws java.lang.IllegalArgumentException if the give valid alleles
	 *         sequence is empty of the allele index is out of range.
	 */
	public EnumGene(final ISeq<? extends A> validAlleles, final int alleleIndex) {
		if (validAlleles.length() == 0) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}

		if (alleleIndex < 0 || alleleIndex >= validAlleles.length()) {
			throw new IndexOutOfBoundsException(format(
				"Allele index is not in range [0, %d).", alleleIndex
			));
		}

		_validAlleles = cast.apply(validAlleles);
		_alleleIndex = alleleIndex;
	}

	/**
	 * Return a new enum gene with an allele randomly chosen from the given
	 * valid alleles.
	 *
	 * @param validAlleles the sequence of valid alleles.
	 * @throws java.lang.IllegalArgumentException if the give valid alleles
	 *         sequence is empty
	 */
	public EnumGene(final ISeq<? extends A> validAlleles) {
		this(
			validAlleles,
			RandomRegistry.getRandom().nextInt(validAlleles.length())
		);
	}

	/**
	 * Return sequence of the valid alleles where this gene is a part of.
	 *
	 * @return the sequence of the valid alleles.
	 */
	public ISeq<A> getValidAlleles() {
		return _validAlleles;
	}

	/**
	 * Return the index of the allele this gene is representing.
	 *
	 * @return the index of the allele this gene is representing.
	 */
	public int getAlleleIndex() {
		return _alleleIndex;
	}

	@Override
	public A getAllele() {
		return _validAlleles.get(_alleleIndex);
	}

	@Deprecated
	@Override
	public EnumGene<A> copy() {
		return new EnumGene<>(_validAlleles, _alleleIndex);
	}

	@Override
	public boolean isValid() {
		return _alleleIndex >= 0;
	}

	@Override
	public EnumGene<A> newInstance() {
		return new EnumGene<>(
			_validAlleles,
			RandomRegistry.getRandom().nextInt(_validAlleles.length())
		);
	}

	/**
	 * Create a new gene from the given {@code value} and the gene context.
	 *
	 * @since 1.6
	 * @param value the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public EnumGene<A> newInstance(final A value) {
		return new EnumGene<>(
			_validAlleles,
			_validAlleles.indexOf(value)
		);
	}

	@Override
	public int compareTo(final EnumGene<A> gene) {
		int result = 0;
		if (_alleleIndex > gene._alleleIndex) {
			result = 1;
		} else if (_alleleIndex < gene._alleleIndex) {
			result = -1;
		}

		return result;
	}

	/**
	 * @deprecated No longer needed after adding new factory methods to the
	 *             {@link Array} class.
	 */
	@Deprecated
	public Factory<EnumGene<A>> asFactory() {
		return this;
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(EnumGene.class)
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
		return Objects.toString(getAllele());
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
				return new EnumGene<>(validAlleles, index);
			}
		};
	}

	static <T> Factory<EnumGene<T>> Gene(final ISeq<? extends T> validAlleles) {
		return new Factory<EnumGene<T>>() {
			private int _index = 0;
			@Override
			public EnumGene<T> newInstance() {
				return new EnumGene<>(validAlleles, _index++);
			}
		};
	}


	/**
	 * @deprecated Use {@link #EnumGene(org.jenetics.util.ISeq, int)} instead.
	 */
	@Deprecated
	public static <A> EnumGene<A> valueOf(
		final ISeq<? extends A> validAlleles,
		final int alleleIndex
	) {
		return new EnumGene<>(validAlleles, alleleIndex);
	}

	/**
	 * Create a new enum gene from the given valid genes and the chosen allele
	 * index.
	 *
	 * @param validAlleles the array of valid alleles.
	 * @param alleleIndex the index of the allele for this gene.
	 * @return a new enum gene
	 * @throws java.lang.IllegalArgumentException if the give valid alleles
	 *         array is empty of the allele index is out of range.
	 */
	public static <G> EnumGene<G> of(
		final G[] validAlleles,
		final int alleleIndex
	) {
		return new EnumGene<>(Array.of(validAlleles).toISeq(), alleleIndex);
	}

	/**
	 * @deprecated Use {@link #of(Object[], int)} instead.
	 */
	@Deprecated
	public static <G> EnumGene<G> valueOf(
		final G[] validAlleles,
		final int alleleIndex
	) {
		return of(validAlleles, alleleIndex);
	}

	/**
	 * @deprecated Use {@link #EnumGene(org.jenetics.util.ISeq)} instead.
	 */
	@Deprecated
	public static <G> EnumGene<G> valueOf(final ISeq<G> validAlleles) {
		return new EnumGene<>(validAlleles);
	}

	/**
	 * Return a new enum gene with an allele randomly chosen from the given
	 * valid alleles.
	 *
	 * @param validAlleles the array of valid alleles.
	 * @return a new enum gene
	 * @throws java.lang.IllegalArgumentException if the give valid alleles
	 *         array is empty
	 */
	public static <G> EnumGene<G> of(final G[] validAlleles) {
		return new EnumGene<>(Array.of(validAlleles).toISeq());
	}

	/**
	 * @deprecated Use {@link #of(Object[])} instead.
	 */
	@Deprecated
	public static <G> EnumGene<G> valueOf(final G[] validAlleles) {
		return of(validAlleles);
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	@SuppressWarnings("rawtypes")
	static final XMLFormat<EnumGene>
		XML = new XMLFormat<EnumGene>(EnumGene.class)
	{
		private static final String LENGTH = "length";
		private static final String CURRENT_ALLELE_INDEX = "allele-index";

		@Override
		public EnumGene newInstance(
			final Class<EnumGene> cls, final InputElement xml
		)
			throws XMLStreamException
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final int index = xml.getAttribute(CURRENT_ALLELE_INDEX, 0);
			final Array<Object> alleles = new Array<>(length);
			for (int i = 0; i < length; ++i) {
				final Object allele = xml.getNext();
				alleles.set(i, allele);
			}

			return new EnumGene<>(alleles.toISeq(), index);
		}

		@Override
		public void write(final EnumGene eg, final OutputElement xml)
			throws XMLStreamException
		{
			xml.setAttribute(LENGTH, eg.getValidAlleles().length());
			xml.setAttribute(CURRENT_ALLELE_INDEX, eg.getAlleleIndex());
			for (Object allele : eg.getValidAlleles()) {
				xml.add(allele);
			}
		}

		@Override
		public void read(final InputElement xml, final EnumGene eg) {
		}
	};

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.EnumGene")
	@XmlType(name = "org.jenetics.EnumGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	final static class Model {
		@XmlAttribute
		int length;

		@XmlAttribute(name = "allele-index")
		int currentAlleleIndex;

		@XmlAnyElement
		List<Object> alleles;

		@ValueType(EnumGene.class)
		@ModelType(Model.class)
		public static final class Adapter
			extends XmlAdapter<Model, EnumGene>
		{
			@Override
			public Model marshal(final EnumGene value) {
				final Model m = new Model();
				m.length = value.getValidAlleles().length();
				m.currentAlleleIndex = value.getAlleleIndex();
				m.alleles = value.getValidAlleles()
					.map(jaxb.Marshaller(value.getValidAlleles().get(0))).asList();
				return m;
			}

			@Override
			public EnumGene unmarshal(final Model m) {
				return new EnumGene<>(
					Array.of(m.alleles).map(Unmarshaller).toISeq(),
					m.currentAlleleIndex
				);
			}

		}
	}
}
