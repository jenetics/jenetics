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
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javolution.context.ObjectFactory;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.internal.util.cast;
import org.jenetics.internal.util.jaxb;

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
 * final Factory〈Genotype〈EnumGene〈Integer〉〉〉 gtf = Genotype.valueOf(
 *     PermutationChromosome.valueOf(alleles)
 * );
 * [/code]
 *
 * The following code shows the assurances of the {@code EnumGene}.
 * [code]
 * final ISeq〈Integer〉 alleles = Array.box(1, 2, 3, 4, 5, 6, 7, 8).toISeq();
 * final EnumGene〈Integer〉 gene = EnumGene.valueOf(alleles, 5);
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
 * @version 1.5 &mdash; <em>$Date$</em>
 */
public final class EnumGene<A>
	implements
		Gene<A, EnumGene<A>>,
		Comparable<EnumGene<A>>
{

	private static final long serialVersionUID = 1L;

	private ISeq<A> _validAlleles;
	private int _alleleIndex = -1;

	EnumGene() {
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
	public EnumGene<A> copy() {
		final EnumGene<A> gene = new EnumGene<>();
		gene._validAlleles = _validAlleles;
		gene._alleleIndex = _alleleIndex;
		return gene;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public EnumGene<A> newInstance() {
		@SuppressWarnings("unchecked")
		final EnumGene<A> gene = FACTORY.object();
		final Random random = RandomRegistry.getRandom();

		gene._alleleIndex = random.nextInt(_validAlleles.length());
		gene._validAlleles = _validAlleles;
		return gene;
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
		return hashCodeOf(EnumGene.class)
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
				return valueOf(validAlleles, index);
			}
		};
	}

	static <T> Factory<EnumGene<T>> Gene(final ISeq<? extends T> validAlleles) {
		return new Factory<EnumGene<T>>() {
			private int _index = 0;
			@Override
			public EnumGene<T> newInstance() {
				return EnumGene.valueOf(validAlleles, _index++);
			}
		};
	}


	@SuppressWarnings("rawtypes")
	private static final ObjectFactory<EnumGene>
	FACTORY = new ObjectFactory<EnumGene>() {
		@Override
		protected EnumGene create() {
			return new EnumGene();
		}
	};

	public static <G> EnumGene<G> valueOf(
		final G[] validAlleles,
		final int alleleIndex
	) {
		return valueOf(Array.valueOf(validAlleles).toISeq(), alleleIndex);
	}

	public static <G> EnumGene<G> valueOf(
		final ISeq<? extends G> validAlleles,
		final int alleleIndex
	) {
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

		@SuppressWarnings("unchecked")
		final EnumGene<G> gene = FACTORY.object();

		gene._validAlleles = cast.apply(validAlleles);
		gene._alleleIndex = alleleIndex;
		return gene;
	}

	public static <G> EnumGene<G> valueOf(final G[] validAlleles) {
		return valueOf(Array.valueOf(validAlleles).toISeq());
	}

	public static <G> EnumGene<G> valueOf(final ISeq<G> validAlleles) {
		if (validAlleles.length() == 0) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}

		@SuppressWarnings("unchecked")
		final EnumGene<G> gene = FACTORY.object();
		gene._validAlleles = validAlleles;
		gene._alleleIndex = RandomRegistry.getRandom().nextInt(validAlleles.length());
		return gene;
	}


	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	@SuppressWarnings({ "unchecked", "rawtypes"})
	static final XMLFormat<EnumGene>
		XML = new XMLFormat<EnumGene>(EnumGene.class)
	{
		private static final String LENGTH = "length";
		private static final String CURRENT_ALLELE_INDEX = "current-allele-index";

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

			return EnumGene.valueOf(alleles.toISeq(), index);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final static class Model {
		@XmlAttribute public int length;
		@XmlAttribute(name = "current-allele-index") public int currentAlleleIndex;

		//@XmlJavaTypeAdapter(model.ListModel.Adapter.class)
		//@XmlJavaTypeAdapter(model.Float64Model.Adapter.class)
		@XmlAnyElement
		public List<model.ObjectModel> alleles;

		public final static class Adapter
			extends XmlAdapter<Model, EnumGene>
		{
			@Override
			public Model marshal(final EnumGene value) {
				final Model m = new Model();
				m.alleles = value.getValidAlleles().asList();
					//.map(Marshaller(value.getValidAlleles())).asList();
				m.length = value.getValidAlleles().length();
				return m;
			}

			@Override
			public EnumGene unmarshal(final Model m) {
				final Object obj = Array.valueOf(m.alleles).toISeq();
					//.map(Unmarshaller(m.alleles.get(0))).toISeq();

				return EnumGene.valueOf(
					(ISeq<Chromosome>)obj,
					m.currentAlleleIndex
				);
			}

			private static Function<Object, Object> Marshaller(final Object c)  {
				return jaxb.marshaller(jaxb.adapterFor(c));
			}

			private static Function<Object, Object> Unmarshaller(final Object c)  {
				return jaxb.unmarshaller(jaxb.adapterFor(c));
			}
		}
	}
}





