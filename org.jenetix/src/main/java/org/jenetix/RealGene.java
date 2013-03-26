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
package org.jenetix;

import javolution.context.ObjectFactory;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.structure.GroupMultiplicative;

import org.jenetics.NumberGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-03-26 $</em>
 */
public class RealGene
	extends NumberGene<Real, RealGene>
	implements GroupMultiplicative<RealGene>
{

	private static final long serialVersionUID = 1L;

	private static final Real TWO = Real.ONE.plus(Real.ONE);

	RealGene() {
	}

	@Override
	protected Real box(Number value) {
		return null;
	}

	@Override
	public RealGene newInstance() {
		int digits = Real.getExactPrecision();
		return null;
	}

	@Override
	public RealGene mean(final RealGene that) {
		final Real mean = _value.plus(that._value).divide(TWO);
		return newInstance(mean);
	}

	@Override
	public RealGene inverse() {
		return valueOf(_value.inverse(), _min, _max);
	}

	@Override
	public RealGene newInstance(final Real value) {
		return valueOf(value, _min, _max);
	}

	@Override
	public RealGene newInstance(final Number value) {
		return valueOf(Real.valueOf(value.doubleValue()), _min, _max);
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	private static final ObjectFactory<RealGene> FACTORY =
		new ObjectFactory<RealGene>() {
			@Override protected RealGene create() {
				return new RealGene();
			}
		};

	/**
	 * Create a new random {@code RealGene} with the given value and the given
	 * range. If the {@code value} isn't within the closed interval [min, max],
	 * no exception is thrown. In this case the method {@link RealGene#isValid()}
	 * returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene.
	 * @param max the maximal valid value of this gene.
	 * @return the new created gene with the given {@code value}.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static RealGene valueOf(
		final Real value,
		final Real min,
		final Real max
	) {
		final RealGene gene = FACTORY.object();
		gene.set(value, min, max);
		return gene;
	}

}








