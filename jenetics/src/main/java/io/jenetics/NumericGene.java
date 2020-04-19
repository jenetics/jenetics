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
package io.jenetics;

/**
 * Base interface for numeric genes.
 *
 * @implSpec
 * <em>Jenetics</em> requires that the individuals ({@link Genotype} and
 * {@link Phenotype}) are not changed after they have been created. Therefore,
 * all implementations of the {@code NumericGene} interface must also be
 * <em>immutable</em>.
 *
 * @see NumericChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 3.0
 */
public interface NumericGene<
	N extends Number & Comparable<? super N>,
	G extends NumericGene<N, G>
>
	extends
		BoundedGene<N, G>,
		Comparable<G>
{

	/**
	 * Returns the value of the specified gene as an byte. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code byte}.
	 */
	default byte byteValue() {
		return allele().byteValue();
	}

	/**
	 * Returns the value of the specified gene as an short. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code short}.
	 */
	default short shortValue() {
		return allele().shortValue();
	}

	/**
	 * Returns the value of the specified gene as an int. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code int}.
	 */
	default int intValue() {
		return allele().intValue();
	}

	/**
	 * Returns the value of the specified gene as an long. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code long}.
	 */
	default long longValue() {
		return allele().longValue();
	}

	/**
	 * Returns the value of the specified gene as an float. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code float}.
	 */
	default float floatValue() {
		return allele().floatValue();
	}

	/**
	 * Returns the value of the specified gene as an double. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code double}.
	 */
	default double doubleValue() {
		return allele().doubleValue();
	}

	@Override
	G newInstance(final Number number);

}
