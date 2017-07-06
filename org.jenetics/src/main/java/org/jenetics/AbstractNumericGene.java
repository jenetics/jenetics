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
package org.jenetics;

import java.io.Serializable;

/**
 * Abstract base class for implementing concrete NumericGenes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6
 * @since 1.6
 */
abstract class AbstractNumericGene<
	N extends Number & Comparable<? super N>,
	G extends AbstractNumericGene<N, G>
>
	extends AbstractBoundedGene<N, G>
	implements NumericGene<N, G>, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create new {@code NumericGene}.
	 *
	 * @param value The value of the gene.
	 * @param min The allowed min value of the gene.
	 * @param max The allows max value of the gene.
	 * @throws NullPointerException if one of the given arguments is
	 *         {@code null}.
	 */
	protected AbstractNumericGene(final N value, final N min, final N max) {
		super(value, min, max);
	}

	@Override
	public abstract G newInstance(final Number number);

}
