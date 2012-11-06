/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static org.jenetics.util.object.checkProbability;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

/**
 * Abstract implementation of the alterer interface.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; $Date: 2012-11-06 $
 */
public abstract class AbstractAlterer<G extends Gene<?, G>>
	implements Alterer<G>
{

	/**
	 * Return an alterer which does nothing.
	 *
	 * @return an alterer which does nothing.
	 */
	public static final <G extends Gene<?, G>> Alterer<G> Null() {
		return new Alterer<G>() {
			@Override
			public <C extends Comparable<? super C>> int alter(
				final Population<G, C> population,
				final int generation
			) {
				return 0;
			}

			@Override
			public int hashCode() {
				return hashCodeOf(getClass()).value();
			}

			@Override
			public boolean equals(final Object obj) {
				if (obj == this) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				return obj.getClass() == getClass();
			}

			@Override
			public String toString() {
				return "Alterer.Null";
			}
		};
	}

	public static final double DEFAULT_ALTER_PROBABILITY = 0.2;

	/**
	 * The altering probability.
	 */
	protected final double _probability;

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability The recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}.
	 */
	protected AbstractAlterer(final double probability) {
		_probability = checkProbability(probability);
	}

	/**
	 * Return the recombination/alter probability for this alterer.
	 *
	 * @return The recombination probability.
	 */
	public double getProbability() {
		return _probability;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_probability).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AbstractAlterer<?>)) {
			return false;
		}

		final AbstractAlterer<?> alterer = (AbstractAlterer<?>)obj;
		return eq(_probability, alterer._probability);
	}
}




