/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *	Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.json;

import static org.jenetics.json.stream.Writer.array;
import static org.jenetics.json.stream.Writer.number;
import static org.jenetics.json.stream.Writer.obj;
import static org.jenetics.json.stream.Writer.text;

import java.util.Collection;

import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.json.stream.Writer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Writers {
	private Writers() {}


	public static final class IntegerChromosome {
		private IntegerChromosome() {}

		public static Writer<org.jenetics.IntegerChromosome> writer() {
			return obj(
				text("name", "int-chromosome"),
				number("min").map(ch -> ch.getMin()),
				number("max").map(ch -> ch.getMax()),
				array("alleles", number())
					.map(ch -> ch.toSeq().map(g -> g.getAllele()))
            );
		}

	}

	public static final class LongChromosome {
		private LongChromosome() {}

		public static Writer<org.jenetics.LongChromosome> writer() {
			return obj(
				text("name", "long-chromosome"),
				number("min").map(ch -> ch.getMin()),
				number("max").map(ch -> ch.getMax()),
				array("alleles", number())
					.map(ch -> ch.toSeq().map(g -> g.getAllele()))
			);
		}

	}

	public static final class DoubleChromosome {
		private DoubleChromosome() {}

		public static Writer<org.jenetics.DoubleChromosome> writer() {
			return obj(
				text("name", "double-chromosome"),
				number("min").map(ch -> ch.getMin()),
				number("max").map(ch -> ch.getMax()),
				array("alleles", number())
					.map(ch -> ch.toSeq().map(g -> g.getAllele()))
			);
		}

	}

	/**
	 * <pre> {@code
	 * {
	 *   "length": 2,
	 *   "genotypes": [
	 *     {
	 *       "length": 2,
	 *       "ngenes": 10,
	 *       "chromosomes": [
	 *         {
	 *           "name": "int-chromosome",
	 *           "min": 23,
	 *           "max": 12321,
	 *           "alleles": [1, 2, 3, 4, 5]
	 *         },
	 *         {
	 *           "name": "int-chromosome",
	 *           "min": 23,
	 *           "max": 12321,
	 *           "alleles": [1, 2, 3, 4, 5]
	 *         }
	 *       ]
	 *     },
	 *     {
	 *       "length": 2,
	 *       "ngenes": 10,
	 *       "chromosomes": [
	 *         {
	 *           "name": "int-chromosome",
	 *           "min": 23,
	 *           "max": 12321,
	 *           "alleles": [1, 2, 3, 4, 5]
	 *         },
	 *         {
	 *           "name": "int-chromosome",
	 *           "min": 23,
	 *           "max": 12321,
	 *           "alleles": [1, 2, 3, 4, 5]
	 *         }
	 *       ]
	 *     }
	 *   ]
	 * }
	 * }</pre>
	 */
	public static final class Genotype {
		private Genotype() {}


		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		Writer<org.jenetics.Genotype<G>> writer(final Writer<? super C> writer) {
			return obj(
				number("length").map(org.jenetics.Genotype<G>::length),
				number("ngenes").map(org.jenetics.Genotype<G>::getNumberOfGenes),
				array("chromosomes", writer).map(gt -> cast(gt.toSeq()))
			);
		}

		@SuppressWarnings("unchecked")
		private static <A, B> B cast(final A value) {
			return (B)value;
		}
	}

	/**
	 * <pre> {@code
	 * {
	 *   "length": 2,
	 *   "genotypes": [
	 *     {
	 *       "length": 2,
	 *       "ngenes": 10,
	 *       "chromosomes": [
	 *         {
	 *           "name": "int-chromosome",
	 *           "min": 23,
	 *           "max": 12321,
	 *           "alleles": [1, 2, 3, 4, 5]
	 *         },
	 *         {
	 *           "name": "int-chromosome",
	 *           "min": 23,
	 *           "max": 12321,
	 *           "alleles": [1, 2, 3, 4, 5]
	 *         }
	 *       ]
	 *     },
	 *     {
	 *       "length": 2,
	 *       "ngenes": 10,
	 *       "chromosomes": [
	 *         {
	 *           "name": "int-chromosome",
	 *           "min": 23,
	 *           "max": 12321,
	 *           "alleles": [1, 2, 3, 4, 5]
	 *         },
	 *         {
	 *           "name": "int-chromosome",
	 *           "min": 23,
	 *           "max": 12321,
	 *           "alleles": [1, 2, 3, 4, 5]
	 *         }
	 *       ]
	 *     }
	 *   ]
	 * }
	 * }</pre>
	 */
	public static final class Genotypes {
		private Genotypes() {}

		public static <
			A,
			G extends Gene<A, G>,
			C extends Chromosome<G>
		>
		Writer<Collection<org.jenetics.Genotype<G>>>
		writer(final Writer<? super C> writer) {
			return obj(
				number("length").map(Collection::size),
				array("genotypes", Genotype.writer(writer))
			);
		}

	}

}
