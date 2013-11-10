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
package org.jenetics.internal;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.jenetics.util.StaticObject;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-11-10 $</em>
 * @since @__version__@
 */
public final class stat extends StaticObject {
	private stat() {}


	interface Mean {
		public long getSamples();
		public double getMean();
	}

	private static final class MMean implements Mean {
		private long _samples = 0;
		private double _mean = Double.NaN;

		@Override
		public long getSamples() {
			return _samples;
		}

		@Override
		public double getMean() {
			return _mean;
		}
	}

	public static <N extends Number, Mean> Collector<N, ?, Mean> mean() {
		return new Collector<N, MMean, Mean>() {
			@Override
			public Supplier<MMean> supplier() {
				return MMean::new;
			}

			@Override
			public BiConsumer<MMean, N> accumulator() {
				return null;
			}

			@Override
			public BinaryOperator<MMean> combiner() {
				return null;
			}

			@Override
			public Function<MMean, Mean> finisher() {
				return null;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return Collections.unmodifiableSet(EnumSet.of(
					Characteristics.CONCURRENT,
					Characteristics.UNORDERED
				));
			}
		};
	}

}
