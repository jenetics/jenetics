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
package org.jenetics.problem;

import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;

import org.jenetics.BitGene;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Problem;
import org.jenetics.engine.codecs;
import org.jenetics.problem.Knapsack.Item;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Knapsack implements Problem<ISeq<Item>, BitGene, Double> {

	/**
	 * This class represents a knapsack item with the specific "size" and
	 * "value".
	 */
	public static final class Item {
		public final double size;
		public final double value;

		private Item(final double size, final double value) {
			this.size = size;
			this.value = value;
		}

		// Create a new random knapsack item.
		private static Item random(final Random r) {
			return new Item(r.nextDouble()*100, r.nextDouble()*100);
		}

		// Create a new collector for summing up the knapsack items.
		private static Collector<Item, ?, Item> toSum() {
			return Collector.of(
				() -> new double[2],
				(a, b) -> {a[0] += b.size; a[1] += b.value;},
				(a, b) -> {a[0] += b[0]; a[1] += b[1]; return a;},
				r -> new Item(r[0], r[1])
			);
		}
	}


	private final Codec<ISeq<Item>, BitGene> _codec;
	private final double _knapsackSize;

	public Knapsack(final ISeq<Item> items, final double knapsackSize) {
		_codec = codecs.ofSubSet(items);
		_knapsackSize = knapsackSize;
	}

	@Override
	public Function<ISeq<Item>, Double> fitness() {
		return items -> {
			final Item sum = items.stream().collect(Item.toSum());
			return sum.size <= _knapsackSize ? sum.value : 0;
		};
	}

	@Override
	public Codec<ISeq<Item>, BitGene> codec() {
		return _codec;
	}

	public static Knapsack of(final int itemCount, final Random random) {
		requireNonNull(random);

		final double knapsackSize = itemCount*100.0/3.0;
		final ISeq<Item> items = MSeq.<Item>ofLength(itemCount)
			.fill(() -> Item.random(random))
			.toISeq();

		return new Knapsack(items, knapsackSize);
	}

}
