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
package io.jenetics.xml;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.xml.stream.Reader.attr;
import static io.jenetics.xml.stream.Reader.elem;
import static io.jenetics.xml.stream.Reader.elems;
import static io.jenetics.xml.stream.Reader.text;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import io.jenetics.BoundedGene;
import io.jenetics.Chromosome;
import io.jenetics.DoubleGene;
import io.jenetics.EnumGene;
import io.jenetics.Gene;
import io.jenetics.IntegerGene;
import io.jenetics.LongGene;
import io.jenetics.util.CharSeq;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.xml.stream.AutoCloseableXMLStreamReader;
import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.XML;

/**
 * This class contains static fields and methods, for creating chromosome- and
 * genotype readers for different gene types.
 *
 * {@snippet lang="java":
 * final Reader<Genotype<BitGene>> bgr =
 *     Readers.Genotype.reader(Readers.BitChromosome.reader());
 *
 * final Reader<Genotype<IntegerGene>> igr =
 *     Writers.Genotype.reader(Readers.IntegerChromosome.reader());
 *
 * final Reader<Genotype<DoubleGene>> dgr =
 *     Readers.Genotype.reader(Readers.DoubleChromosome.reader());
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public final class Readers {
	private Readers() {}

	/**
	 * Bit chromosome reader methods, which reads XML-representations of
	 * bit-chromosomes.
	 * <p>
	 * <b>XML</b>
	 * <pre> {@code
	 * <bit-chromosome length="20" ones-probability="0.5">11100011101011001010</bit-chromosome>
	 * } </pre>
	 */
	public static final class BitChromosome {
		private BitChromosome() {}

		/**
		 * Return a XML reader for {@link io.jenetics.BitChromosome} objects.
		 *
		 * @return a chromosome reader
		 */
		public static Reader<io.jenetics.BitChromosome> reader() {
			return elem(
				v -> io.jenetics.BitChromosome.of(
					(String)v[2], (int)v[0], (double)v[1]
				),
				Writers.BitChromosome.ROOT_NAME,
				attr(Writers.BitChromosome.LENGTH_NAME)
					.map(Integer::parseInt),
				attr(Writers.BitChromosome.ONES_PROBABILITY_NAME)
					.map(Double::parseDouble),
				text()
			);
		}

		/**
		 * Read a new {@link io.jenetics.BitChromosome} from the given input
		 * stream.
		 *
		 * @param in the data source of the bit-chromosome
		 * @return the bit-chromosome read from the input stream
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static io.jenetics.BitChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
				xml.next();
				return reader().read(xml);
			}
		}
	}

	/**
	 * Reader methods for {@link io.jenetics.CharacterChromosome} objects.
	 * <p>
	 * <b>XML format</b>
	 * <pre> {@code
	 * <character-chromosome length="4">
	 *     <valid-alleles>ABCDEFGHIJKLMNOPQRSTUVWXYZ<valid-alleles>
	 *     <alleles>ASDF</alleles>
	 * </character-chromosome>
	 * } </pre>
	 */
	public static final class CharacterChromosome {
		private CharacterChromosome() {}

		/**
		 * Return a XML reader for {@link io.jenetics.CharacterChromosome}
		 * objects.
		 *
		 * @return a chromosome reader
		 */
		public static Reader<io.jenetics.CharacterChromosome> reader() {
			return elem(
				v -> io.jenetics.CharacterChromosome.of(
					(String)v[2], (CharSeq)v[1]
				),
				Writers.CharacterChromosome.ROOT_NAME,
				attr(Writers.CharacterChromosome.LENGTH_NAME)
					.map(Integer::parseInt),
				elem(Writers.CharacterChromosome.VALID_ALLELES_NAME,
					text().map(CharSeq::new)),
				elem(Writers.CharacterChromosome.ALLELES_NAME, text())
			);
		}

		/**
		 * Read a new {@link io.jenetics.CharacterChromosome} from the given
		 * input stream.
		 *
		 * @param in the data source of the chromosome
		 * @return the bit-chromosome read from the input stream
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static io.jenetics.CharacterChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
				xml.next();
				return reader().read(xml);
			}
		}

	}

	/**
	 * Reader methods for {@link io.jenetics.BoundedChromosome} objects.
	 * <p>
	 * <b>XML format</b>
	 * <pre> {@code
	 * <root-name length="3">
	 *     <min>aaa</min>
	 *     <max>zzz</max>
	 *     <alleles>
	 *         <allele>iii</allele>
	 *         <allele>fff</allele>
	 *         <allele>ggg</allele>
	 *     </alleles>
	 * </root-name>
	 * } </pre>
	 */
	public static final class BoundedChromosome {
		private BoundedChromosome() {}

		/**
		 * Create a bounded chromosome reader with the given configuration.
		 *
		 * @param name the root element name
		 * @param gene the gene creator
		 * @param genes the gene array creator
		 * @param chromosome the chromosome creator
		 * @param alleleReader the allele reader
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @return a bounded chromosome reader
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static <
			A extends Comparable<? super A>,
			G extends BoundedGene<A, G>,
			C extends io.jenetics.BoundedChromosome<A, G>
		>
		Reader<C> reader(
			final String name,
			final BoundedGeneCreator<A, G> gene,
			final IntFunction<G[]> genes,
			final Function<G[], C> chromosome,
			final Reader<? extends A> alleleReader
		) {
			return elem(v -> {
				final int length = (int)v[0];
				@SuppressWarnings("unchecked")
				final A min = (A)v[1];
				@SuppressWarnings("unchecked")
				final A max = (A)v[2];
				@SuppressWarnings("unchecked")
				final List<A> alleles = (List<A>)v[3];

				if (alleles.size() != length) {
					throw new IllegalArgumentException(format(
						"Expected %d alleles, but got %d,",
						length, alleles.size()
					));
				}

				return chromosome.apply(
					alleles.stream()
						.map(value -> gene.create(value, min, max))
						.toArray(genes)
				);
			}, name,
				attr(Writers.BoundedChromosome.LENGTH_NAME).map(Integer::parseInt),
				elem(Writers.BoundedChromosome.MIN_NAME, alleleReader),
				elem(Writers.BoundedChromosome.MAX_NAME, alleleReader),
				elem(Writers.BoundedChromosome.ALLELES_NAME,
					elems(elem(Writers.BoundedChromosome.ALLELE_NAME, alleleReader))
				)
			);
		}

	}

	/**
	 * Reader methods for {@link io.jenetics.IntegerChromosome} objects.
	 * <p>
	 * <b>XML format</b>
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
	 * } </pre>
	 */
	public static final class IntegerChromosome {
		private IntegerChromosome() {}

		/**
		 * Return the default allele reader for the {@code IntegerChromosome}.
		 *
		 * @return the default allele reader
		 */
		public static Reader<Integer> alleleReader() {
			return text().map(Integer::parseInt);
		}

		/**
		 * Return a {@link io.jenetics.IntegerChromosome} reader.
		 *
		 * @return a integer chromosome reader
		 */
		public static Reader<io.jenetics.IntegerChromosome> reader() {
			return BoundedChromosome.reader(
				Writers.IntegerChromosome.ROOT_NAME,
				IntegerGene::of,
				IntegerGene[]::new,
				io.jenetics.IntegerChromosome::of,
				alleleReader()
			);
		}

		/**
		 * Read a new {@link io.jenetics.IntegerChromosome} from the given
		 * input stream.
		 *
		 * @param in the data source of the chromosome
		 * @return a new chromosome
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static io.jenetics.IntegerChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader reader = XML.reader(in)) {
				reader.next();
				return reader().read(reader);
			}
		}

	}

	/**
	 * Reader methods for {@link io.jenetics.LongChromosome} objects.
	 * <p>
	 * <b>XML format</b>
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
	 * } </pre>
	 */
	public static final class LongChromosome {
		private LongChromosome() {}

		/**
		 * Return the default allele reader for the {@code LongChromosome}.
		 *
		 * @return the default allele reader
		 */
		public static Reader<Long> alleleReader() {
			return text().map(Long::parseLong);
		}

		/**
		 * Return a {@link io.jenetics.LongChromosome} reader.
		 *
		 * @return a long chromosome reader
		 */
		public static Reader<io.jenetics.LongChromosome> reader() {
			return BoundedChromosome.reader(
				Writers.LongChromosome.ROOT_NAME,
				LongGene::of,
				LongGene[]::new,
				io.jenetics.LongChromosome::of,
				alleleReader()
			);
		}

		/**
		 * Read a new {@link io.jenetics.LongChromosome} from the given
		 * input stream.
		 *
		 * @param in the data source of the chromosome
		 * @return a new chromosome
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static io.jenetics.LongChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (var reader = XML.reader(in)) {
				reader.next();
				return reader().read(reader);
			}
		}

	}

	/**
	 * Reader methods for {@link io.jenetics.DoubleChromosome} objects.
	 * <p>
	 * <b>XML format</b>
	 * <pre> {@code
	 * <double-chromosome length="3">
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

		/**
		 * Return the default allele reader for the {@code DoubleChromosome}.
		 *
		 * @return the default allele reader
		 */
		public static Reader<Double> alleleReader() {
			return text().map(Double::parseDouble);
		}

		/**
		 * Return a {@link io.jenetics.DoubleChromosome} reader.
		 *
		 * @return a double chromosome reader
		 */
		public static Reader<io.jenetics.DoubleChromosome> reader() {
			return BoundedChromosome.reader(
				Writers.DoubleChromosome.ROOT_NAME,
				DoubleGene::of,
				DoubleGene[]::new,
				io.jenetics.DoubleChromosome::of,
				alleleReader()
			);
		}

		/**
		 * Read a new {@link io.jenetics.DoubleChromosome} from the given
		 * input stream.
		 *
		 * @param in the data source of the chromosome
		 * @return a new chromosome
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if the given input stream is {@code null}
		 */
		public static io.jenetics.DoubleChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (var reader = XML.reader(in)) {
				reader.next();
				return reader().read(reader);
			}
		}

	}

	/**
	 * Reader methods for {@link io.jenetics.PermutationChromosome} objects.
	 * <p>
	 * <b>XML format</b>
	 * <pre> {@code
	 * <permutation-chromosome length="5">
	 *     <valid-alleles type="java.lang.Integer">
	 *         <allele>0</allele>
	 *         <allele>1</allele>
	 *         <allele>2</allele>
	 *         <allele>3</allele>
	 *         <allele>4</allele>
	 *     </valid-alleles>
	 *     <order>2 1 3 5 4</order>
	 * </permutation-chromosome>
	 * } </pre>
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
		public static <A> Reader<io.jenetics.PermutationChromosome<A>>
		reader(final Reader<? extends A> alleleReader) {
			requireNonNull(alleleReader);

			return elem(v -> {
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

				return new io.jenetics.PermutationChromosome<>(alleles.toISeq());
			},
				Writers.PermutationChromosome.ROOT_NAME,
				attr(Writers.PermutationChromosome.LENGTH_NAME).map(Integer::parseInt),
				elem(Writers.PermutationChromosome.VALID_ALLELES_NAME,
					elems(elem(Writers.PermutationChromosome.ALLELE_NAME, alleleReader))
				),
				elem(Writers.PermutationChromosome.ORDER_NAME, text())
			);
		}

		/**
		 * Reads a new {@link io.jenetics.PermutationChromosome} from the given
		 * input stream.
		 *
		 * @param <A> the allele type
		 * @param in the data source of the chromosome
		 * @param alleleReader the allele reader
		 * @return a new permutation chromosome
		 * @throws XMLStreamException if reading the chromosome fails
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static <A> io.jenetics.PermutationChromosome<A>
		read(final InputStream in, final Reader<? extends A> alleleReader)
			throws XMLStreamException
		{
			requireNonNull(alleleReader);
			requireNonNull(in);

			try (var xml = XML.reader(in)) {
				xml.next();
				return PermutationChromosome.<A>reader(alleleReader).read(xml);
			}
		}

	}

	/**
	 * Writer methods for {@link io.jenetics.Genotype} objects.
	 * <p>
	 * <b>XML format</b>
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
	 *         <alleles>
	 *     </double-chromosome>
	 * </genotype>
	 * } </pre>
	 */
	public static final class Genotype {
		private Genotype() {}

		/**
		 * Create a genotype reader with the given chromosome reader.
		 *
		 * @param chromosomeReader the underlying chromosome reader
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @return a genotype reader with the given chromosome reader
		 * @throws NullPointerException if the given {@code chromosomeReader} is
		 *         {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		Reader<io.jenetics.Genotype<G>>
		reader(final Reader<? extends C> chromosomeReader) {
			requireNonNull(chromosomeReader);

			return elem(v -> {
				@SuppressWarnings("unchecked")
				final List<C> chromosomes = (List<C>)v[2];
				final io.jenetics.Genotype<G> genotype =
					io.jenetics.Genotype.of(chromosomes);

				final int length = (int)v[0];
				final int ngenes = (int)v[1];
				if (length != genotype.length()) {
					throw new IllegalArgumentException(format(
						"Expected %d chromosome, but read %d.",
						length, genotype.length()
					));
				}
				if (ngenes != genotype.geneCount()) {
					throw new IllegalArgumentException(format(
						"Expected %d genes, but read %d.",
						ngenes, genotype.geneCount()
					));
				}

				return genotype;
			},
				Writers.Genotype.ROOT_NAME,
				attr(Writers.Genotype.LENGTH_NAME).map(Integer::parseInt),
				attr(Writers.Genotype.NGENES_NAME).map(Integer::parseInt),
				elems(chromosomeReader)
			);
		}

		/**
		 * Reads a genotype by using the given chromosome reader.
		 *
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @param in the input stream to read the genotype from
		 * @param chromosomeReader the used chromosome reader
		 * @return a genotype by using the given chromosome reader
		 * @throws XMLStreamException if reading the genotype fails
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		io.jenetics.Genotype<G>
		read(final InputStream in, final Reader<? extends C> chromosomeReader)
			throws XMLStreamException
		{
			requireNonNull(chromosomeReader);
			requireNonNull(in);

			try (var xml = XML.reader(in)) {
				xml.next();
				return reader(chromosomeReader).read(xml);
			}
		}
	}

	/**
	 * This class contains static reader methods for
	 * {@link io.jenetics.Genotype} objects.
	 * <p>
	 * <b>XML format</b>
	 * <pre> {@code
	 * <genotypes length="1">
	 *     <genotype length="2" ngenes="5">
	 *         <double-chromosome length="3">
	 *             <min>0.0</min>
	 *             <max>1.0</max>
	 *             <alleles>
	 *                 <allele>0.27251556008507416</allele>
	 *                 <allele>0.003140816229067145</allele>
	 *                 <allele>0.43947528327497376</allele>
	 *             </alleles>
	 *         </double-chromosome>
	 *         <double-chromosome length="2">
	 *             <min>0.0</min>
	 *             <max>1.0</max>
	 *             <alleles>
	 *                 <allele>0.4026521545744768</allele>
	 *                 <allele>0.36137605952663554</allele>
	 *             <alleles>
	 *         </double-chromosome>
	 *     </genotype>
	 * </genotypes>
	 * } </pre>
	 */
	public static final class Genotypes {
		private Genotypes() {}

		/**
		 * Return a genotype reader using the given chromosome reader.
		 *
		 * @param chromosomeReader the underlying chromosome reader
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @return a genotype reader using the given chromosome reader
		 * @throws NullPointerException if the given {@code chromosomeReader} is
		 *         {@code null}
		 */
		@SuppressWarnings("unchecked")
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		Reader<List<io.jenetics.Genotype<G>>>
		reader(final Reader<C> chromosomeReader) {
			return elem(
				p -> (List<io.jenetics.Genotype<G>>)p[0],
				Writers.Genotypes.ROOT_NAME,
				elems(Genotype.reader(chromosomeReader))
			);
		}

		/**
		 * Reads the genotypes by using the given chromosome reader.
		 *
		 * @param <A> the allele type
		 * @param <G> the gene type
		 * @param <C> the chromosome type
		 * @param in the input stream to read the genotype from
		 * @param chromosomeReader the used chromosome reader
		 * @return a genotype by using the given chromosome reader
		 * @throws XMLStreamException if reading the genotype fails
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		List<io.jenetics.Genotype<G>>
		read(final InputStream in, final Reader<? extends C> chromosomeReader)
			throws XMLStreamException
		{
			requireNonNull(chromosomeReader);
			requireNonNull(in);

			try (var xml = XML.reader(in)) {
				xml.next();
				return reader(chromosomeReader).read(xml);
			}
		}

	}

	/**
	 * Reads the genotypes by using the given chromosome reader.
	 *
	 * @see Genotypes#read(InputStream, Reader)
	 *
	 * @param <A> the allele type
	 * @param <G> the gene type
	 * @param <C> the chromosome type
	 * @param in the input stream to read the genotype from
	 * @param chromosomeReader the used chromosome reader
	 * @return a genotype by using the given chromosome reader
	 * @throws XMLStreamException if reading the genotype fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <
		A,
		G extends Gene<A, G>,
		C extends Chromosome<G>
	>
	List<io.jenetics.Genotype<G>>
	read(final InputStream in, final Reader<? extends C> chromosomeReader)
		throws XMLStreamException
	{
		return Genotypes.read(in, chromosomeReader);
	}

}
