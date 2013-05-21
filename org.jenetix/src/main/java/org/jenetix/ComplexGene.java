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

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.structure.GroupMultiplicative;

import org.jenetics.NumberGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-05-21 $</em>
 */
public final class ComplexGene
	extends NumberGene<Complex, ComplexGene>
	implements GroupMultiplicative<ComplexGene>
{

	private static final long serialVersionUID = 1L;

	ComplexGene() {
	}

	@Override
	public ComplexGene newInstance() {
		return null;
	}

	@Override
	public ComplexGene mean(ComplexGene that) {
		return null;
	}

	@Override
	public ComplexGene inverse() {
		return null;
	}

	@Override
	public ComplexGene times(ComplexGene that) {
		return null;
	}

	@Override
	public ComplexGene newInstance(Complex value) {
		return null;
	}

	@Override
	public ComplexGene newInstance(java.lang.Number value) {
		return null;
	}

	@Override
	protected Complex box(Number value) {
		return null;
	}

}
