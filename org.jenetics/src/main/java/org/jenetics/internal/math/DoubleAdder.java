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
package org.jenetics.internal.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-23 $</em>
 * @since 3.0
 */
public final class DoubleAdder {
	public double value = 0.0;

	private double compensation = 0.0;

	public DoubleAdder(final double value) {
		add(value);
	}

	public DoubleAdder() {
	}

	private DoubleAdder reset() {
		value = 0.0;
		compensation = 0.0;
		return this;
	}

	public DoubleAdder set(final double value) {
		return reset().add(value);
	}

	public DoubleAdder set(final DoubleAdder value) {
		return reset().add(value);
	}

	public DoubleAdder add(final double v) {
		final double y = v - compensation;
		final double t = this.value + y;
		compensation = (t - this.value) - y;
		this.value = t;
		return this;
	}

	public DoubleAdder add(final DoubleAdder value) {
		add(value.value);
		add(value.compensation);
		return this;
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}

}
