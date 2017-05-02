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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.xml.stream.XMLStreamException;

import org.jenetics.DoubleGene;
import org.jenetics.IntegerGene;
import org.jenetics.LongGene;
import org.jenetics.util.CharSeq;
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

	@FunctionalInterface
	private static interface GeneCreator<A, G> {
		G create(final A value, final A min, final A max);
	}

	@FunctionalInterface
	private static interface ChromosomeCreator<G, C> {
		C create(final G[] genes);
	}

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
			return Reader.of(
				p -> {
					final int length = Integer.parseInt((String)p[0]);
					final double prob = Double.parseDouble((String)p[1]);
					final String genes = (String)p[2];

					return org.jenetics.BitChromosome.of(genes, length, prob);
				},
				"bit-chromosome",
				Reader.attrs("length", "ones-probability")
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
			return Reader.of(
				p -> {
					final int length = Integer.parseInt((String)p[0]);
					final CharSeq valid = new CharSeq((String)p[1]);
					final String alleles = (String)p[2];

					return org.jenetics.CharacterChromosome.of(alleles, valid);
				},
				"character-chromosome",
				Reader.attrs("length"),
				Reader.of("valid-alleles"),
				Reader.of("alleles")
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

	public static final class PermutationChromosome {
		private PermutationChromosome() {}

	}

	/**
	 * Integer chromosome reader methods.
	 */
	public static final class IntegerChromosome {
		private IntegerChromosome() {}

		public static Reader<org.jenetics.IntegerChromosome> reader() {
			return chromosome(
				"integer-chromosome",
				Integer::parseInt,
				IntegerGene::of,
				IntegerGene[]::new,
				org.jenetics.IntegerChromosome::of
			);
		}

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
	 * Long chromosome reader methods.
	 */
	public static final class LongChromosome {
		private LongChromosome() {}

		public static Reader<org.jenetics.LongChromosome> reader() {
			return chromosome(
				"long-chromosome",
				Long::parseLong,
				LongGene::of,
				LongGene[]::new,
				org.jenetics.LongChromosome::of
			);
		}

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
	 * Double chromosome reader methods.
	 */
	public static final class DoubleChromosome {
		private DoubleChromosome() {}

		public static Reader<org.jenetics.DoubleChromosome> reader() {
			return chromosome(
				"double-chromosome",
				Double::parseDouble,
				DoubleGene::of,
				DoubleGene[]::new,
				org.jenetics.DoubleChromosome::of
			);
		}

		public static org.jenetics.DoubleChromosome read(final InputStream in)
			throws XMLStreamException
		{
			try (AutoCloseableXMLStreamReader reader = XML.reader(in)) {
				reader.next();
				return reader().read(reader);
			}
		}

	}

	public static final class Genotype {

	}

	public static final class Genotypes {

	}

	private static <A, G, C> Reader<C> chromosome(
		final String name,
		final Function<String, A> allele,
		final GeneCreator<A, G> gene,
		final IntFunction<G[]> genes,
		final ChromosomeCreator<G, C> chromosome
	) {
		requireNonNull(name);
		requireNonNull(allele);
		requireNonNull(gene);
		requireNonNull(genes);
		requireNonNull(chromosome);

		return Reader.of(
			p -> {
				final A min = allele.apply((String)p[0]);
				final A max = allele.apply((String)p[1]);
				final int length = Integer.parseInt((String)p[2]);

				@SuppressWarnings("unchecked")
				final List<A> alleles = (List<A>)p[3];
				if (alleles.size() != length) {
					throw new IllegalArgumentException(format(
						"Expected %d alleles, but got %d,",
						length, alleles.size()
					));
				}

				return chromosome.create(
					alleles.stream()
						.map(value -> gene.create(value, min, max))
						.toArray(genes)
				);
			},
			name,
			Reader.attrs("min", "max", "length"),
			Reader.ofList(Reader.of("allele").map(allele))
		);
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
