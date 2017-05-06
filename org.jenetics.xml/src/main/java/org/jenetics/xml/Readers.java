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
package org.jenetics.xml;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.xml.stream.Reader.attr;
import static org.jenetics.xml.stream.Reader.elem;
import static org.jenetics.xml.stream.Reader.elems;
import static org.jenetics.xml.stream.Reader.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import org.jenetics.BoundedGene;
import org.jenetics.Chromosome;
import org.jenetics.DoubleGene;
import org.jenetics.EnumGene;
import org.jenetics.Gene;
import org.jenetics.IntegerGene;
import org.jenetics.LongGene;
import org.jenetics.util.CharSeq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.xml.stream.AutoCloseableXMLStreamReader;
import org.jenetics.xml.stream.Reader;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Readers {
	private Readers() {}

	/**
	 * Bit chromosome reader methods, which reads XML-representations of
	 * bit-chromosomes.
	 * <p>
	 * {@code
	 * <bit-chromosome length="20" ones-probability="0.5">11100011101011001010</bit-chromosome>
	 * }
	 */
	public static final class BitChromosome {
		private BitChromosome() {}

		/**
		 * Return a XML reader for {@link org.jenetics.BitChromosome} objects.
		 *
		 * @return a chromosome reader
		 */
		public static Reader<org.jenetics.BitChromosome> reader() {
			return elem("bit-chromosome",
				v -> org.jenetics.BitChromosome.of(
					(String)v[2], (int)v[0], (double)v[1]
				),
				attr("length").map(Integer::parseInt),
				attr("ones-probability").map(Double::parseDouble),
				text()
			);
		}

		/**
		 * Read a new {@link org.jenetics.BitChromosome} from the given input
		 * stream.
		 *
		 * @param in the data source of the bit-chromosome
		 * @return the bit-chromosome read from the input stream
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static org.jenetics.BitChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
				xml.next();
				return reader().read(xml);
			}
		}
	}

	/**
	 * Reader methods for {@link org.jenetics.CharacterChromosome} objects.
	 *
	 * <pre> {@code
	 * <character-chromosome length="4">
	 *     <valid-alleles>ABCDEFGHIJKLMNOPQRSTUVWXYZ<valid-alleles>
	 *     <alleles>ASDF</alleles>
	 * </character-chromosome>
	 * }</pre>
	 */
	public static final class CharacterChromosome {
		private CharacterChromosome() {}

		/**
		 * Return a XML reader for {@link org.jenetics.CharacterChromosome}
		 * objects.
		 *
		 * @return a chromosome reader
		 */
		public static Reader<org.jenetics.CharacterChromosome> reader() {
			return elem("character-chromosome",
				v -> org.jenetics.CharacterChromosome.of(
					(String)v[2], (CharSeq)v[1]
				),
				attr("length").map(Integer::parseInt),
				elem("valid-alleles", text(CharSeq::new)),
				elem("alleles", text())
			);
		}

		/**
		 * Read a new {@link org.jenetics.CharacterChromosome} from the given
		 * input stream.
		 *
		 * @param in the data source of the chromosome
		 * @return the bit-chromosome read from the input stream
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static org.jenetics.CharacterChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
				xml.next();
				return reader().read(xml);
			}
		}

	}

	/**
	 * Reader methods for {@link org.jenetics.CharacterChromosome} objects.
	 *
	 * <pre> {@code
	 * <permutation-chromosome length="15">
	 *     <valid-alleles>
	 *         <allele>0.2725</allele>
	 *         <allele>0.0031</allele>
	 *         <allele>0.4395</allele>
	 *         <allele>0.1065</allele>
	 *         <allele>0.1970</allele>
	 *         <allele>0.7450</allele>
	 *         <allele>0.5594</allele>
	 *         <allele>0.0282</allele>
	 *         <allele>0.5741</allele>
	 *         <allele>0.4534</allele>
	 *         <allele>0.8111</allele>
	 *         <allele>0.5710</allele>
	 *         <allele>0.3017</allele>
	 *         <allele>0.5455</allele>
	 *         <allele>0.2107</allele>
	 *     </valid-alleles>
	 *     <order>13 12 4 6 8 14 7 2 11 5 3 0 9 10 1</order>
	 * </permutation-chromosome>
	 * }</pre>
	 */
	public static final class PermutationChromosome {
		private PermutationChromosome() {}

		/**
		 * Return a reader for permutation chromosomes with the given allele
		 * reader.
		 *
		 * @param alleleReader the allele reader
		 * @param <A> the allele type
		 * @return a permutation chromosome reader
		 * @throws NullPointerException if the given allele reader is
		 *        {@code null}
		 */
		public static <A> Reader<org.jenetics.PermutationChromosome<A>>
		reader(final Reader<A> alleleReader) {
			requireNonNull(alleleReader);

			return elem("permutation-chromosome",
				v -> {
					final int length = (int)v[0];
					@SuppressWarnings("unchecked")
					final ISeq<A> validAlleles = ISeq.of((List<A>)v[1]);

					final int[] order = Stream.of(((String) v[2]).split("\\s"))
						.mapToInt(Integer::parseInt)
						.toArray();

					final MSeq<EnumGene<A>> alleles = MSeq.ofLength(length);
					for (int i = 0; i < length; ++i) {
						final EnumGene<A> gene = EnumGene.of(order[i], validAlleles);
						alleles.set(i, gene);
					}

					return new org.jenetics.PermutationChromosome<A>(alleles.toISeq());
				},
				attr("length").map(Integer::parseInt),
				elem("valid-alleles",
					elems(elem("allele", alleleReader))
				),
				elem("order", text())
			);
		}

		/**
		 * Reads a new {@link org.jenetics.PermutationChromosome} from the given
		 * input stream.
		 *
		 * @param alleleReader the allele reader
		 * @param in the data source of the chromosome
		 * @param <A> the allele type
		 * @return a new permutation chromosome
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static <A> org.jenetics.PermutationChromosome<A>
		read(final Reader<A> alleleReader, final InputStream in)
			throws XMLStreamException
		{
			requireNonNull(alleleReader);
			requireNonNull(in);

			try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
				xml.next();
				return reader(alleleReader).read(xml);
			}
		}

	}

	public static final class BoundedChromosome {
		private BoundedChromosome() {}

		@SuppressWarnings("unchecked")
		public static <
			A extends Comparable<? super A>,
			G extends BoundedGene<A, G>,
			C extends org.jenetics.BoundedChromosome<A, G>
		>
		Reader<C> reader(
			final String name,
			final Function3<A, A, A, G> gene,
			final IntFunction<G[]> genes,
			final Function<G[], C> chromosome,
			final Reader<? extends A> alleleReader
		) {
			return elem(name,
				v -> {
					final int length = (int)v[0];
					final A min = (A)v[1];
					final A max = (A)v[2];
					final List<A> alleles = (List<A>)v[3];

					if (alleles.size() != length) {
						throw new IllegalArgumentException(format(
							"Expected %d alleles, but got %d,",
							length, alleles.size()
						));
					}

					return chromosome.apply(
						alleles.stream()
							.map(value -> gene.apply(value, min, max))
							.toArray(genes)
					);
				},
				attr("length").map(Integer::parseInt),
				elem("min", alleleReader),
				elem("max", alleleReader),
				elem("alleles",
					elems(elem("allele", alleleReader))
				)
			);
		}

	}

	/**
	 * Reader methods for {@link org.jenetics.IntegerChromosome} objects.
	 *
	 * <pre> {@code
	 * <int-chromosome length="3">
	 *     <min>-2147483648</min>
	 *     <max>2147483647</max>
	 *     <alleles>
	 *         <allele>-1878762439</allele>
	 *         <allele>-957346595</allele>
	 *         <allele>-88668137</allele>
	 *     </alleles>
	 * </int-chromosome>
	 * }</pre>
	 */
	public static final class IntegerChromosome {
		private IntegerChromosome() {}

		public static Reader<Integer> alleleReader() {
			return text(Integer::parseInt);
		}

		/**
		 * Return a {@link org.jenetics.IntegerChromosome} reader.
		 *
		 * @return a integer chromosome reader
		 */
		public static Reader<org.jenetics.IntegerChromosome> reader() {
			return BoundedChromosome.reader(
				"int-chromosome",
				IntegerGene::of,
				IntegerGene[]::new,
				org.jenetics.IntegerChromosome::of,
				alleleReader()
			);
		}

		/**
		 * Read a new {@link org.jenetics.IntegerChromosome} from the given
		 * input stream.
		 *
		 * @param in the data source of the chromosome
		 * @return a new chromosome
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static org.jenetics.IntegerChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader reader = XML.reader(in)) {
				reader.next();
				return reader().read(reader);
			}
		}

	}

	/**
	 * Reader methods for {@link org.jenetics.LongChromosome} objects.
	 *
	 * <pre> {@code
	 * <long-chromosome length="3">
	 *     <min>-9223372036854775808</min>
	 *     <max>9223372036854775807</max>
	 *     <alleles>
	 *         <allele>-1345217698116542402</allele>
	 *         <allele>-7144755673073475303</allele>
	 *         <allele>6053786736809578435</allele>
	 *     </alleles>
	 * </long-chromosome>
	 * }</pre>
	 */
	public static final class LongChromosome {
		private LongChromosome() {}

		public static Reader<Long> alleleReader() {
			return text(Long::parseLong);
		}

		/**
		 * Return a {@link org.jenetics.LongChromosome} reader.
		 *
		 * @return a long chromosome reader
		 */
		public static Reader<org.jenetics.LongChromosome> reader() {
			return BoundedChromosome.reader(
				"long-chromosome",
				LongGene::of,
				LongGene[]::new,
				org.jenetics.LongChromosome::of,
				alleleReader()
			);
		}

		/**
		 * Read a new {@link org.jenetics.LongChromosome} from the given
		 * input stream.
		 *
		 * @param in the data source of the chromosome
		 * @return a new chromosome
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static org.jenetics.LongChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader reader = XML.reader(in)) {
				reader.next();
				return reader().read(reader);
			}
		}

	}

	/**
	 * Reader methods for {@link org.jenetics.DoubleChromosome} objects.
	 *
	 * <pre> {@code
	 * <double-chromosome length="3" min="0.0" max="1.0">
	 *     <min>0.0</min>
	 *     <max>1.0</max>
	 *     <alleles>
	 *         <allele>0.27251556008507416</allele>
	 *         <allele>0.003140816229067145</allele>
	 *         <allele>0.43947528327497376</allele>
	 *     </alleles>
	 * </double-chromosome>
	 * }</pre>
	 */
	public static final class DoubleChromosome {
		private DoubleChromosome() {}

		public static Reader<Double> alleleReader() {
			return text(Double::parseDouble);
		}

		/**
		 * Return a {@link org.jenetics.DoubleChromosome} reader.
		 *
		 * @return a double chromosome reader
		 */
		public static Reader<org.jenetics.DoubleChromosome> reader() {
			return BoundedChromosome.reader(
				"double-chromosome",
				DoubleGene::of,
				DoubleGene[]::new,
				org.jenetics.DoubleChromosome::of,
				alleleReader()
			);
		}

		/**
		 * Read a new {@link org.jenetics.DoubleChromosome} from the given
		 * input stream.
		 *
		 * @param in the data source of the chromosome
		 * @return a new chromosome
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static org.jenetics.DoubleChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader reader = XML.reader(in)) {
				reader.next();
				return reader().read(reader);
			}
		}

	}

	/**
	 * Writer methods for {@link org.jenetics.Genotype} objects.
	 *
	 * <pre>{@code
	 * final Writer<Genotype<DoubleGene>> writer =
	 *     genotypeWriter(DOUBLE_CHROMOSOME_WRITER);
	 * }</pre>
	 *
	 * Example output:
	 * <pre> {@code
	 * <genotype length="2" ngenes="5">
	 *     <double-chromosome length="3">
	 *         <min>0.0</min>
	 *         <max>1.0</max>
	 *         <alleles>
	 *             <allele>0.27251556008507416</allele>
	 *             <allele>0.003140816229067145</allele>
	 *             <allele>0.43947528327497376</allele>
	 *         </alleles>
	 *     </double-chromosome>
	 *     <double-chromosome length="2">
	 *         <min>0.0</min>
	 *         <max>1.0</max>
	 *         <alleles>
	 *             <allele>0.4026521545744768</allele>
	 *             <allele>0.36137605952663554</allele>
	 *         </alleles>
	 *     </double-chromosome>
	 * </genotype>
	 * }</pre>
	 */
	public static final class Genotype {
		private Genotype() {}

		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		Reader<org.jenetics.Genotype<G>>
		reader(final Reader<C> chromosomeReader) {
			requireNonNull(chromosomeReader);

			return elem("genotype",
				v -> {
					@SuppressWarnings("unchecked")
					final List<C> chromosomes = (List<C>)v[2];
					final org.jenetics.Genotype<G> genotype =
						org.jenetics.Genotype.of(chromosomes);

					final int length = (int)v[0];
					final int ngenes = (int)v[1];
					if (length != genotype.length()) {
						throw new IllegalArgumentException(format(
							"Expected %d chromosome, but read %d.",
							length, genotype.length()
						));
					}
					if (ngenes != genotype.getNumberOfGenes()) {
						throw new IllegalArgumentException(format(
							"Expected %d genes, but read %d.",
							ngenes, genotype.getNumberOfGenes()
						));
					}

					return genotype;
				},
				attr("length").map(Integer::parseInt),
				attr("ngenes").map(Integer::parseInt),
				elems(chromosomeReader)
			);
		}

		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		org.jenetics.Genotype<G>
		read(final Reader<C> chromosomeReader, final InputStream in)
			throws XMLStreamException
		{
			requireNonNull(chromosomeReader);
			requireNonNull(in);

			try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
				xml.next();
				return reader(chromosomeReader).read(xml);
			}
		}
	}

	public static final class Genotypes {
		private Genotypes() {}

		@SuppressWarnings("unchecked")
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		Reader<Collection<org.jenetics.Genotype<G>>>
		reader(final Reader<C> chromosomeReader) {
			return elem("genotypes",
				p -> (Collection<org.jenetics.Genotype<G>>)p[0],
				elems(Genotype.reader(chromosomeReader))
			);
		}
	}

	public static void main(final String[] args) throws Exception {
		final org.jenetics.BitChromosome bch = org.jenetics.BitChromosome.of(10);

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writers.BitChromosome.write(bch, out);

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		final org.jenetics.BitChromosome bch1 = BitChromosome.read(in);
		System.out.println(bch1);
	}

}
