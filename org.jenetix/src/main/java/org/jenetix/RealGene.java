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
package org.jenetix;

import javolution.context.ObjectFactory;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.structure.GroupMultiplicative;

import org.jenetics.NumberGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-05-23 $</em>
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


	@Override
	protected org.jenetics.NumberGene.Builder<Real, RealGene> getBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

}








