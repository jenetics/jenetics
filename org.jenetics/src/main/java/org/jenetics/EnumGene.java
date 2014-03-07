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
 *     new PermutationChromosome<>(alleles)
 * );
 * [/code]
 *
 * The following code shows the assurances of the {@code EnumGene}.
 * [code]
 * final ISeq〈Integer〉 alleles = Array.box(1, 2, 3, 4, 5, 6, 7, 8).toISeq();
 * final EnumGene〈Integer〉 gene = new EnumGene<>(5, alleles);
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
 * @version 1.6 &mdash; <em>$Date: 2014-03-06 $</em>
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
	 * @param alleleIndex the index of the allele for this gene.
	 * @param validAlleles the sequence of valid alleles.
	 * @throws java.lang.IllegalArgumentException if the give valid alleles
	 *         sequence is empty
	 * @throws NullPointerException if the valid alleles seq is {@code null}.
	 */
	public EnumGene(final int alleleIndex, final ISeq<? extends A> validAlleles) {
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
		return new EnumGene<>(_alleleIndex, _validAlleles);
	}

	@Override
	public boolean isValid() {
		return _alleleIndex >= 0 && _alleleIndex < _validAlleles.length();
	}

	@Override
	public EnumGene<A> newInstance() {
		return new EnumGene<>(
			RandomRegistry.getRandom().nextInt(_validAlleles.length()),
			_validAlleles
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
			_validAlleles.indexOf(value),
			_validAlleles
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
				return new EnumGene<>(index, validAlleles);
			}
		};
	}

	static <T> Factory<EnumGene<T>> Gene(final ISeq<? extends T> validAlleles) {
		return new Factory<EnumGene<T>>() {
			private int _index = 0;
			@Override
			public EnumGene<T> newInstance() {
				return new EnumGene<>(_index++, validAlleles);
			}
		};
	}

	/**
	 * Return a new enum gene with an allele randomly chosen from the given
	 * valid alleles.
	 *
	 * @param validAlleles the sequence of valid alleles.
	 * @throws java.lang.IllegalArgumentException if the give valid alleles
	 *         sequence is empty
	 * @throws NullPointerException if the valid alleles seq is {@code null}.
	 */
	public static <A> EnumGene<A> of(final ISeq<? extends A> validAlleles) {
		return new EnumGene<>(
			RandomRegistry.getRandom().nextInt(validAlleles.length()),
			validAlleles
		);
	}

	/**
	 * @deprecated Use {@link #EnumGene(int, org.jenetics.util.ISeq)} instead.
	 */
	@Deprecated
	public static <A> EnumGene<A> valueOf(
		final ISeq<? extends A> validAlleles,
		final int alleleIndex
	) {
		return new EnumGene<>(alleleIndex, validAlleles);
	}

	/**
	 * Create a new enum gene from the given valid genes and the chosen allele
	 * index.
	 * @param alleleIndex the index of the allele for this gene.
	 * @param validAlleles the array of valid alleles.
	 *
	 * @return a new enum gene
	 * @throws java.lang.IllegalArgumentException if the give valid alleles
	 *         array is empty of the allele index is out of range.
	 */
	@SafeVarargs
	public static <G> EnumGene<G> of(
		final int alleleIndex,
		final G... validAlleles
	) {
		return new EnumGene<>(alleleIndex, Array.of(validAlleles).toISeq());
	}

	/**
	 * @deprecated Use {@link #of(int, Object[])} instead.
	 */
	@Deprecated
	public static <G> EnumGene<G> valueOf(
		final G[] validAlleles,
		final int alleleIndex
	) {
		return of(alleleIndex, validAlleles);
	}

	/**
	 * @deprecated Use {@link #of(org.jenetics.util.ISeq)} instead.
	 */
	@Deprecated
	public static <G> EnumGene<G> valueOf(final ISeq<G> validAlleles) {
		return EnumGene.of(validAlleles);
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
	@SafeVarargs
	public static <G> EnumGene<G> of(final G... validAlleles) {
		return EnumGene.of(Array.of(validAlleles).toISeq());
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

			return new EnumGene<>(index, alleles.toISeq());
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
					m.currentAlleleIndex,
					Array.of(m.alleles).map(Unmarshaller).toISeq()
				);
			}

		}
	}
}
