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

import java.math.BigDecimal;

import org.jenetics.NumericGene;
import org.jenetics.util.Mean;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-16 $</em>
 */
public final class BigDecimalGene implements
	NumericGene<BigDecimal, BigDecimalGene>,
	Mean<BigDecimalGene>
{
	private static final long serialVersionUID = 1L;

	@Override
	public BigDecimal getAllele() {
		return null;
	}

	@Override
	public BigDecimal getMin() {
		return null;
	}

	@Override
	public BigDecimal getMax() {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public BigDecimalGene mean(BigDecimalGene that) {
		return null;
	}

	@Override
	public BigDecimalGene newInstance(Number number) {
		return null;
	}

	@Override
	public BigDecimalGene newInstance(BigDecimal value) {
		return null;
	}

	@Override
	public int compareTo(BigDecimalGene o) {
		return 0;
	}

	@Override
	public BigDecimalGene newInstance() {
		return null;
	}

}
