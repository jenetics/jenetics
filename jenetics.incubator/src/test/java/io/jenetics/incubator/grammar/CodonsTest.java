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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.grammar;

import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.BitChromosome;
import io.jenetics.incubator.grammar.StandardSentenceGenerator.Expansion;
import io.jenetics.incubator.grammar.bnf.Bnf;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CodonsTest {

	@Test(dataProvider = "chromosomeSizes")
	public void toByteArray(final int size) {
		final var ch = BitChromosome.of(size);

		assertThat(Codons.toByteArray(ch)).isEqualTo(ch.toByteArray());
	}

	@DataProvider
	public Object[][] chromosomeSizes() {
		return new Object[][] {
			{1}, {2}, {3}, {7}, {8}, {9}, {10}, {15}, {16}, {17}, {31},
			{32}, {33}, {100}, {1_000}, {10_000}, {100_000}
		};
	}

	@DataProvider
	public Object[][] codonsSizes() {
		return new Object[][] {
			{1, 1},
			{2, 10},
			{3, 2},
			{8, 8},
			{10, 5},
			{16, 10},
			{31, 15},
			{33, 20},
			{100, 5}
		};
	}

	//@Test
	public void paper() {
		final Cfg cfg = Bnf.parse("""
			<expr> ::= <expr><op><expr> | (<expr><op><expr>) | <pre-op>(<expr>) | <var>
			<op> ::= + | - | / | *
			<pre-op> ::= sin
			<var> ::= x | 1.0
			"""
		);
		System.out.println(Bnf.format(cfg));

		final var random = new Random();

		final int[] values = new int[] {
			220, 240, 220, 203, 101, 53, 202, 203, 102, 55, 220, 202,
			241, 130, 37, 202, 203, 140, 39, 202, 203, 102
		};
		final Codons codons = Codons.ofIntArray(values);

		final var cds = new TrackingCodons(random);

		var generator = new StandardSentenceGenerator(
			cds,
			Expansion.LEFT_FIRST,
			MAX_VALUE
		);

		final String sentence = generator.generate(cfg).stream()
			.map(Cfg.Symbol::value)
			.collect(Collectors.joining());

		if (sentence.equals("1.0-sin(x)*sin(x)-sin(x)-sin(x)")) {
			final var val = cds.values();
			System.out.println(sentence);
			System.out.println(Arrays.toString(val));
		}
	}

	//@Test
	public void statistics() {
		final Cfg cfg = Bnf.parse("""
			<expr> ::= <expr><op><expr> | (<expr><op><expr>) | <pre-op>(<expr>) | <var>
			<op> ::= + | - | / | *
			<pre-op> ::= sin
			<var> ::= x | 1.0
			"""
		);

		final var codons = Codons.ofIntArray(
			RandomGenerator.getDefault().ints(0, 256)
				.limit(500)
				.toArray()
		);

		final var random = RandomGenerator.getDefault();
		final var lengths = new HashMap<Integer, AtomicInteger>();
		for (int i = 0; i < 1_000_000; ++i) {
			var generator = new StandardSentenceGenerator(
				SymbolIndex.of(random),
				Expansion.LEFT_FIRST,
				200
			);

			final var sentence = generator.generate(cfg);
			lengths.computeIfAbsent(sentence.size(), key -> new AtomicInteger()).incrementAndGet();
		}

		final var csv = lengths.entrySet().stream()
			.sorted(Entry.comparingByKey())
			.toList();

		for (var e : csv) {
			System.out.println(e.getKey() + "," + e.getValue());
		}

		System.out.println(lengths);
		// {0=5716, 1=1787, 4=1427, 7=357, 10=357, 12=356}
	}

}
