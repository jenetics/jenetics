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

import io.jenetics.util.RandomRegistry;

/**
 * Implementation of a BitGene.
 *
 * @see BitChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
 */
public enum BitGene
	implements
		Gene<Boolean, BitGene>,
		Comparable<BitGene>
{

	FALSE(false),
	TRUE(true);

	private static final long serialVersionUID = 3L;

	public static final BitGene ZERO = FALSE;
	public static final BitGene ONE = TRUE;

	private final boolean _value;

	private BitGene(final boolean value) {
		_value = value;
	}

	/**
	 * Return the value of the BitGene.
	 *
	 * @return The value of the BitGene.
	 */
	public final boolean getBit() {
		return _value;
	}

	/**
	 * Return the {@code boolean} value of this gene.
	 *
	 * @see #getAllele()
	 *
	 * @return the {@code boolean} value of this gene.
	 */
	public boolean booleanValue() {
		return _value;
	}

	@Override
	public Boolean getAllele() {
		return _value;
	}

	/**
	 * Return always {@code true}.
	 *
	 * @return always {@code true}.
	 */
	@Override
	public boolean isValid() {
		return true;
	}

	/**
	 * Create a new, <em>random</em> gene.
	 */
	@Override
	public BitGene newInstance() {
		return RandomRegistry.getRandom().nextBoolean() ? TRUE : FALSE;
	}

	/**
	 * Create a new gene from the given {@code value}..
	 *
	 * @since 1.6
	 * @param value the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public BitGene newInstance(final Boolean value) {
		return of(value);
	}

	@Override
	public String toString() {
		return Boolean.toString(_value);
	}

	/**
	 * Return the corresponding {@code BitGene} for the given {@code boolean}
	 * value.
	 *
	 * @param value the value of the returned {@code BitGene}.
	 * @return the {@code BitGene} for the given {@code boolean} value.
	 */
	public static BitGene of(final boolean value) {
		return value ? TRUE : FALSE;
	}

}
