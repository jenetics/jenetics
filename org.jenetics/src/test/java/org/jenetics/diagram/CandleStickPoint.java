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
package org.jenetics.diagram;

import java.util.stream.Collector;

import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.Quantile;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public final class CandleStickPoint {
	public final double mean;
	public final double low;
	public final double high;
	public final double min;
	public final double max;

	private CandleStickPoint(
		final double mean,
		final double low,
		final double high,
		final double min,
		final double max
	) {
		this.mean = mean;
		this.low = low;
		this.high = high;
		this.min = min;
		this.max = max;
	}

	public static CandleStickPoint of(
		final double mean,
		final double low,
		final double high,
		final double min,
		final double max
	) {
		return new CandleStickPoint(mean, low, high, min, max);
	}

	public static Collector<IntDoublePair, ?, CandleStickPoint> toCandleStickPoint() {
		return Collector.of(
			Statistics::new,
			(r, t) -> r.accept(t._1),
			Statistics::combine,
			Statistics::toPoint
		);
	}

	private static final class Statistics {
		private final DoubleMomentStatistics data = new DoubleMomentStatistics();
		private final Quantile low = new Quantile(0.25);
		private final Quantile high = new Quantile(0.75);

		void accept(final double value) {
			data.accept(value);
			low.accept(value);
			high.accept(value);
		}

		Statistics combine(final Statistics other) {
			data.combine(other.data);
			low.combine(other.low);
			high.combine(other.high);

			return this;
		}

		CandleStickPoint toPoint() {
			return CandleStickPoint.of(
				data.getMean(),
				low.getValue(),
				high.getValue(),
				data.getMin(),
				data.getMax()
			);
		}
	}


}

/*
	gen, // 1
	generations.getMean(), // 2
	fitness.getMean(), // 3
	quartileLower.getValue(), // 4
	quartileUpper.getValue(), // 5
	generations.getMin(), // 6
	generations.getMax() // 7

	1:4:6:7:5
*/
