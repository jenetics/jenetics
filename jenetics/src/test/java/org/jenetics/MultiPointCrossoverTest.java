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

import static java.lang.Math.min;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.math.base;

import org.jenetics.util.CharSeq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class MultiPointCrossoverTest extends AltererTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(final double p) {
		return new MultiPointCrossover<>(p);
	}

	@Test(dataProvider = "crossoverParameters")
	public void crossover(
		final String stringA,
		final String stringB,
		final Seq<Integer> points,
		final String expectedA,
		final String expectedB
	) {
		final ISeq<Character> a = CharSeq.toISeq(stringA);
		final ISeq<Character> b = CharSeq.toISeq(stringB);

		final MSeq<Character> ma = a.copy();
		final MSeq<Character> mb = b.copy();

		final int[] intPoints = points.stream()
			.mapToInt(Integer::intValue)
			.toArray();

		MultiPointCrossover.crossover(ma, mb, intPoints);
		Assert.assertEquals(toString(ma), expectedA);
		Assert.assertEquals(toString(mb), expectedB);
	}

	private String toString(final MSeq<Character> seq) {
		return seq.stream()
			.map(Objects::toString)
			.collect(Collectors.joining(""));
	}

	@DataProvider(name = "crossoverParameters")
	public Object[][] getParameters() {
		return new Object[][] {{
			"0123456789", "ABCDEFGHIJ",
			ISeq.empty(),
			"0123456789", "ABCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0),
			"ABCDEFGHIJ", "0123456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(1),
			"0BCDEFGHIJ", "A123456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(2),
			"01CDEFGHIJ", "AB23456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(3),
			"012DEFGHIJ", "ABC3456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(8),
			"01234567IJ", "ABCDEFGH89"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(9),
			"012345678J", "ABCDEFGHI9"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(10),
			"0123456789", "ABCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1),
			"A123456789", "0BCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(3, 5),
			"012DE56789", "ABC34FGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2),
			"A1CDEFGHIJ", "0B23456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2, 3),
			"A1C3456789", "0B2DEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2, 3, 4),
			"A1C3EFGHIJ", "0B2D456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2, 3, 4, 5),
			"A1C3E56789", "0B2D4FGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2, 3, 4, 5, 6),
			"A1C3E5GHIJ", "0B2D4F6789"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2, 3, 4, 5, 6, 7),
			"A1C3E5G789", "0B2D4F6HIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2, 3, 4, 5, 6, 7, 8),
			"A1C3E5G7IJ", "0B2D4F6H89"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
			"A1C3E5G7I9", "0B2D4F6H8J"
		},{
			"0123456789", "ABCDEFGHIJ",
			ISeq.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
			"A1C3E5G7I9", "0B2D4F6H8J"
		},

		{
			"012345678", "ABCDEFGHI",
			ISeq.empty(),
			"012345678", "ABCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0),
			"ABCDEFGHI", "012345678"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(1),
			"0BCDEFGHI", "A12345678"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(2),
			"01CDEFGHI", "AB2345678"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(3),
			"012DEFGHI", "ABC345678"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(8),
			"01234567I", "ABCDEFGH8"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(9),
			"012345678", "ABCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1),
			"A12345678", "0BCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(3, 5),
			"012DE5678", "ABC34FGHI"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1, 2),
			"A1CDEFGHI", "0B2345678"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1, 2, 3),
			"A1C345678", "0B2DEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1, 2, 3, 4),
			"A1C3EFGHI", "0B2D45678"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1, 2, 3, 4, 5),
			"A1C3E5678", "0B2D4FGHI"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1, 2, 3, 4, 5, 6),
			"A1C3E5GHI", "0B2D4F678"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1, 2, 3, 4, 5, 6, 7),
			"A1C3E5G78", "0B2D4F6HI"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1, 2, 3, 4, 5, 6, 7, 8),
			"A1C3E5G7I", "0B2D4F6H8"
		},{
			"012345678", "ABCDEFGHI",
			ISeq.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
			"A1C3E5G7I", "0B2D4F6H8"
		},

		{
			"0123456789", "ABCDEF",
			ISeq.empty(),
			"0123456789", "ABCDEF",
		},{
			"0123456789", "ABCDEF",
			ISeq.of(0),
			"ABCDEF6789", "012345"
		},{
			"0123456789", "ABCDEF",
			ISeq.of(1),
			"0BCDEF6789", "A12345"
		},{
			"0123456789", "ABCDEF",
			ISeq.of(2),
			"01CDEF6789", "AB2345"
		},{
			"0123456789", "ABCDEF",
			ISeq.of(3),
			"012DEF6789", "ABC345"
		},{
			"0123456789", "ABCDEF",
			ISeq.of(5),
			"01234F6789", "ABCDE5"
		},{
			"0123456789", "ABCDEF",
			ISeq.of(6),
			"0123456789", "ABCDEF"
		},{
			"0123456789", "ABCDEF",
			ISeq.of(1, 3),
			"0BC3456789", "A12DEF"
		}
		};
	}

	@Test
	public void singlePointCrossoverConsistency() {
		final ISeq<Character> a = CharSeq.toISeq("1234567890");
		final ISeq<Character> b = CharSeq.toISeq("ABCDEFGHIJ");

		for (int i = 0; i < a.length() + 1; ++i) {
			final MSeq<Character> ma1 = a.copy();
			final MSeq<Character> mb1 = b.copy();
			final MSeq<Character> ma2 = a.copy();
			final MSeq<Character> mb2 = b.copy();

			MultiPointCrossover.crossover(ma1, mb1, new int[]{i});
			SinglePointCrossover.crossover(ma2, mb2, i);

			Assert.assertEquals(ma1, ma2);
			Assert.assertEquals(mb1, mb2);
		}
	}

	@Test(dataProvider = "numberOfCrossoverPoints")
	public void reverseCrossover(final Integer npoints) {
		for (int i = 1; i < 500; ++i) {
			final CharSeq chars = CharSeq.of("a-zA-Z");
			final ISeq<Character> a = new CharacterChromosome(chars, i).toSeq()
				.map(CharacterGene::getAllele);
			final ISeq<Character> b = new CharacterChromosome(chars, i).toSeq()
				.map(CharacterGene::getAllele);

			final MSeq<Character> ma1 = a.copy();
			final MSeq<Character> mb1 = b.copy();
			final int[] points = base.subset(
				a.length() + 1,
				min(npoints, a.length() + 1),
				new Random(1234)
			);

			MultiPointCrossover.crossover(ma1, mb1, points);
			MultiPointCrossover.crossover(ma1, mb1, points);

			Assert.assertEquals(ma1, a);
			Assert.assertEquals(mb1, b);
		}
	}

	@DataProvider(name = "numberOfCrossoverPoints")
	public Iterator<Object[]> getNumberOfCrossoverPoints() {
		return MSeq.<Object[]>ofLength(11).fill(new Supplier<Object[]>() {
			private int point = 0;
			@Override public Object[] get() {
				return new Object[]{++point};
			}

		}).iterator();
	}

	@Test
	public void crossoverAll1() {
		final CharSeq chars = CharSeq.of("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

		final MultiPointCrossover<CharacterGene, Double> crossover =
				new MultiPointCrossover<>(2000);
		final int[] points = new int[g1.length()];
		for (int i = 0; i < points.length; ++i) {
			points[i] = i;
		}

		final MSeq<CharacterGene> ms1 = g1.copy();
		final MSeq<CharacterGene> ms2 = g2.copy();

		crossover.crossover(ms1, ms2);
	}

	public static void main(final String[] args) {
		final ISeq<Character> a = CharSeq.toISeq("12345678");
		final ISeq<Character> b = CharSeq.toISeq("ABCDEFGH");

		final int[][] indexes = new int[][] {
			{0, 4},
			{3, 6},
			{0, 8}
		};

		for (int i = 0; i < indexes.length; ++i) {
			final MSeq<Character> ma = a.copy();
			final MSeq<Character> mb = b.copy();

			MultiPointCrossover.crossover(ma, mb, indexes[i]);

			final String l1 = String.format( "%6s: %s  %s", MSeq.of(indexes[i]), a, ma);
			final String l2 = String.format( "        %s  %s",    b, mb);

			System.out.println(l1);
			System.out.println(l2);
			System.out.println();
		}
	}

}
