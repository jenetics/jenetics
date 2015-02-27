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
package org.jenetics.diagram.problem;

import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 */
public final class Knapsack
	implements
		Problem<BitGene, Double, ISeq<Knapsack.Item>>,
		Function<Genotype<BitGene>, Double>
{

	public static final class Item {
		public final double _size;
		public final double _value;

		private Item(final double size, final double value) {
			_size = size;
			_value = value;
		}

		public double getSize() {
			return _size;
		}

		public double getValue() {
			return _value;
		}

		// Create a new random knapsack item.
		private static Item random() {
			final Random r = RandomRegistry.getRandom();
			return new Item(r.nextDouble()*100, r.nextDouble()*100);
		}

		// Create a new collector for summing up the knapsack items.
		private static Collector<Item, ?, Item> toSum() {
			return Collector.of(
				() -> new double[2],
				(a, b) -> {
					a[0] += b._size;
					a[1] += b._value;
				},
				(a, b) -> {
					a[0] += b[0];
					a[1] += b[1];
					return a;
				},
				r -> new Item(r[0], r[1])
			);
		}

		public static Item of(final double size, final double value) {
			return new Item(size, value);
		}
	}

	private final ISeq<Item> _items;
	private final double _size;

	private Knapsack(final ISeq<Item> items, final double size) {
		_items = requireNonNull(items);
		_size = size;
	}

	@Override
	public Double apply(final Genotype<BitGene> gt) {
		final Item sum = ((BitChromosome)gt.getChromosome()).ones()
			.mapToObj(_items::get)
			.collect(Item.toSum());

		return sum._size <= this._size ? sum._value : 0;
	}

	public ISeq<Item> getItems() {
		return _items;
	}

	public double getSize() {
		return _size;
	}

	@Override
	public Genotype<BitGene> genotype() {
		return Genotype.of(BitChromosome.of(_items.length(), 0.5));
	}

	@Override
	public Function<Genotype<BitGene>, Double> function() {
		return this;
	}

	@Override
	public Function<Genotype<BitGene>, ISeq<Item>> decoder() {
		return gt -> ((BitChromosome)gt.getChromosome()).ones()
			.mapToObj(_items::get)
			.collect(ISeq.toISeq());
	}

	public static Knapsack of(final ISeq<Item> items, final double size) {
		return new Knapsack(items, size);
	}

	public static Knapsack of(final int nitems) {
		final double kssize = nitems*100.0/3.0;

		return new Knapsack(
			Stream.generate(Item::random)
				.limit(nitems)
				.collect(ISeq.toISeq()),
			kssize
		);
	}

}

