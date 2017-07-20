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
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.jaxb;
import org.jenetics.internal.util.model.IndexedObject;

import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * <p>
 * Gene which holds enumerable (countable) genes. Will be used for combinatorial
 * problems in combination with the {@link PermutationChromosome}.
 * </p>
 * The following code shows how to create a combinatorial genotype factory which
 * can be used when creating an {@link org.jenetics.engine.Engine} instance.
 * <pre>{@code
 * final ISeq<Integer> alleles = ISeq.of(1, 2, 3, 4, 5, 6, 7, 8);
 * final Factory<Genotype<EnumGene<Integer>>> gtf = Genotype.of(
 *     PermutationChromosome.of(alleles)
 * );
 * }</pre>
 *
 * The following code shows the assurances of the {@code EnumGene}.
 * <pre>{@code
 * final ISeq<Integer> alleles = ISeq.of(1, 2, 3, 4, 5, 6, 7, 8);
 * final EnumGene<Integer> gene = new EnumGene<>(5, alleles);
 *
 * assert(gene.getAlleleIndex() == 5);
 * assert(gene.getAllele() == gene.getValidAlleles().get(5));
 * assert(gene.getValidAlleles() == alleles);
 * }</pre>
 *
 * @see PermutationChromosome
 * @see PartiallyMatchedCrossover
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.4
 */
@XmlJavaTypeAdapter(EnumGene.Model.Adapter.class)
public final class EnumGene<A>
	implements
		Gene<A, EnumGene<A>>,
		Comparable<EnumGene<A>>,
		Serializable
{

	private static final long serialVersionUID = 2L;

	private final ISeq<A> _validAlleles;
	private final int _alleleIndex;

	/**
	 * Create a new enum gene from the given valid genes and the chosen allele
	 * index.
	 *
	 * @param alleleIndex the index of the allele for this gene.
	 * @param validAlleles the sequence of valid alleles.
	 * @throws IllegalArgumentException if the give valid alleles sequence is
	 *         empty
	 * @throws NullPointerException if the valid alleles seq is {@code null}.
	 */
	EnumGene(final int alleleIndex, final ISeq<? extends A> validAlleles) {
		if (validAlleles.isEmpty()) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}

		if (alleleIndex < 0 || alleleIndex >= validAlleles.length()) {
			throw new IndexOutOfBoundsException(format(
				"Allele index is not in range [0, %d).", alleleIndex
			));
		}

		_validAlleles = ISeq.upcast(validAlleles);
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
	 *
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

	@Override
	public int hashCode() {
		return Hash.of(EnumGene.class)
				.and(_alleleIndex)
				.and(_validAlleles).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EnumGene &&
			eq(((EnumGene)obj)._alleleIndex, _alleleIndex) &&
			eq(((EnumGene)obj)._validAlleles, _validAlleles);
	}

	@Override
	public String toString() {
		return Objects.toString(getAllele());
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new enum gene from the given valid genes and the chosen allele
	 * index.
	 *
	 * @since 3.4
	 *
	 * @param <A> the allele type
	 * @param alleleIndex the index of the allele for this gene.
	 * @param validAlleles the sequence of valid alleles.
	 * @return a new {@code EnumGene} with the given with the allele
	 *        {@code validAlleles.get(alleleIndex)}
	 * @throws IllegalArgumentException if the give valid alleles sequence is
	 *         empty
	 * @throws NullPointerException if the valid alleles seq is {@code null}.
	 */
	public static <A> EnumGene<A> of(
		final int alleleIndex,
		final ISeq<? extends A> validAlleles
	) {
		return new EnumGene<>(alleleIndex, validAlleles);
	}

	/**
	 * Return a new enum gene with an allele randomly chosen from the given
	 * valid alleles.
	 *
	 * @param <A> the allele type
	 * @param validAlleles the sequence of valid alleles.
	 * @return a new {@code EnumGene} with an randomly chosen allele from the
	 *         sequence of valid alleles
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
	 * Create a new enum gene from the given valid genes and the chosen allele
	 * index.
	 *
	 * @param <A> the allele type
	 * @param alleleIndex the index of the allele for this gene.
	 * @param validAlleles the array of valid alleles.
	 * @return a new {@code EnumGene} with the given with the allele
	 *        {@code validAlleles[alleleIndex]}
	 * @throws java.lang.IllegalArgumentException if the give valid alleles
	 *         array is empty of the allele index is out of range.
	 */
	@SafeVarargs
	public static <A> EnumGene<A> of(
		final int alleleIndex,
		final A... validAlleles
	) {
		return new EnumGene<>(alleleIndex, ISeq.of(validAlleles));
	}

	/**
	 * Return a new enum gene with an allele randomly chosen from the given
	 * valid alleles.
	 *
	 * @param <A> the allele type
	 * @param validAlleles the array of valid alleles.
	 * @return a new {@code EnumGene} with an randomly chosen allele from the
	 *         sequence of valid alleles
	 * @throws IllegalArgumentException if the give valid alleles array is empty
	 */
	@SafeVarargs
	public static <A> EnumGene<A> of(final A... validAlleles) {
		return EnumGene.of(ISeq.of(validAlleles));
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "enum-gene")
	@XmlType(name = "org.jenetics.EnumGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	final static class Model {

		@XmlAttribute(name = "length", required = true)
		public int length;

		@XmlElementWrapper(name = "valid-alleles", required = true, nillable = false)
		@XmlElement(name = "allele", required = true, nillable = false)
		public List alleles;

		@XmlElement(name = "allele", required = true, nillable = false)
		public IndexedObject allele = new IndexedObject();

		public static final class Adapter
			extends XmlAdapter<Model, EnumGene>
		{
			@Override
			public Model marshal(final EnumGene gene) {
				final Function marshaller = jaxb.Marshaller(gene.getAllele());
				final Model m = new Model();
				m.length = gene.getValidAlleles().length();
				m.allele.index = gene.getAlleleIndex();
				m.allele.value = marshaller.apply(gene.getAllele());
				m.alleles = gene.getValidAlleles()
					.map(marshaller)
					.asList();

				return m;
			}

			@Override
			public EnumGene unmarshal(final Model m) {
				return new EnumGene<>(
					m.allele.index,
					ISeq.of(m.alleles)
						.map(jaxb.Unmarshaller(m.allele.value))
				);
			}

		}
	}

}
