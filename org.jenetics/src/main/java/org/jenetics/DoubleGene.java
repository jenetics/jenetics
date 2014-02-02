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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
public final class DoubleGene implements Gene<Double, DoubleGene> {

	private final Double _value;
	private final Double _min;
	private final Double _max;


	public DoubleGene(final Double value, final Double min, final Double max) {
		_value = value;
		_min = min;
		_max = max;
	}

	public Double getMin() {
		return _min;
	}

	public Double getMax() {
		return _max;
	}

	@Override
	public Double getAllele() {
		return _value;
	}

	@Override
	public DoubleGene newInstance() {
		return null;
	}

	@Override
	public Object copy() {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}
}
