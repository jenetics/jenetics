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
package org.jenetics.example;


import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.jenetics.BitGene;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.Problem;
import org.jenetics.engine.codecs;
import org.jenetics.util.ISeq;

/**
 * Solves:
 * <a href="https://programmers.stackexchange.com/questions/326378/finding-the-best-combination-of-sets-that-gives-the-maximum-number-of-unique-ite">
 *   Finding the best combination of sets that gives the maximum number of unique items</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.6
 * @version 3.6
 */
public class Springsteen
	implements Problem<ISeq<Springsteen.Record>, BitGene, Double>
{

	public static final class Record {
		final String name;
		final double price;
		final ISeq<String> songs;

		public Record(
			final String name,
			final double price,
			final ISeq<String> songs
		) {
			this.name = requireNonNull(name);
			this.price = price;
			this.songs = requireNonNull(songs);
		}
	}

	private final ISeq<Record> _records;
	private final double _maxPricePerUniqueSong;

	public Springsteen(final ISeq<Record> records, final double maxPricePerUniqueSong) {
		_records = requireNonNull(records);
		_maxPricePerUniqueSong = maxPricePerUniqueSong;
	}

	@Override
	public Function<ISeq<Record>, Double> fitness() {
		return records -> {
			final double cost = records.stream()
				.mapToDouble(r -> r.price)
				.sum();

			final int uniqueSongCount = records.stream()
				.flatMap(r -> r.songs.stream())
				.collect(Collectors.toSet())
				.size();

			final double pricePerUniqueSong = cost/uniqueSongCount;

			return pricePerUniqueSong <= _maxPricePerUniqueSong
				? uniqueSongCount
				: 0.0;
		};
	}

	@Override
	public Codec<ISeq<Record>, BitGene> codec() {
		return codecs.ofSubSet(_records);
	}

	public static void main(final String[] args) {
		final double maxPricePerUniqueSong = 2.5;

		final Springsteen springsteen = new Springsteen(
			ISeq.of(
				new Record("Record1", 25, ISeq.of("Song1", "Song2", "Song3", "Song4", "Song5", "Song6")),
				new Record("Record2", 15, ISeq.of("Song2", "Song3", "Song4", "Song5", "Song6", "Song7")),
				new Record("Record3", 35, ISeq.of("Song5", "Song6", "Song7", "Song8", "Song9", "Song10")),
				new Record("Record4", 17, ISeq.of("Song9", "Song10", "Song12", "Song4", "Song13", "Song14")),
				new Record("Record5", 29, ISeq.of("Song1", "Song2", "Song13", "Song14", "Song15", "Song16")),
				new Record("Record6", 5, ISeq.of("Song18", "Song20", "Song30", "Song40"))
			),
			maxPricePerUniqueSong
		);

		final Engine<BitGene, Double> engine = Engine.builder(springsteen)
			.build();

		final ISeq<Record> result = springsteen.codec().decoder().apply(
			engine.stream()
				.limit(10)
				.collect(EvolutionResult.toBestGenotype())
		);

		final double cost = result.stream()
			.mapToDouble(r -> r.price)
			.sum();

		final int uniqueSongCount = result.stream()
			.flatMap(r -> r.songs.stream())
			.collect(Collectors.toSet())
			.size();

		final double pricePerUniqueSong = cost/uniqueSongCount;

		System.out.println("Overall cost:  " + cost);
		System.out.println("Unique songs:  " + uniqueSongCount);
		System.out.println("Cost per song: " + (cost/uniqueSongCount));
		System.out.println("Records:       " + result.map(r -> r.name).toString(", "));

	}

}
