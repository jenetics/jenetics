/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.lang.Math.min;

import java.util.Iterator;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.Array;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-03-06 $</em>
 */
public class MultiPointCrossoverTest {

	@Test(dataProvider = "parameters")
	public void crossover(
		final String stringA,
		final String stringB,
		final Array<Integer> points,
		final String expectedA,
		final String expectedB
	) {
		final ISeq<Character> a = CharSeq.toISeq(stringA);
		final ISeq<Character> b = CharSeq.toISeq(stringB);

		final MSeq<Character> ma = a.copy();
		final MSeq<Character> mb = b.copy();

		MultiPointCrossover.crossover(ma, mb, Array.unboxInt(points));
		Assert.assertEquals(ma, CharSeq.toISeq(expectedA));
		Assert.assertEquals(mb, CharSeq.toISeq(expectedB));
	}

	@DataProvider(name = "parameters")
	public Object[][] getParameters() {
		return new Object[][] {{
			"0123456789", "ABCDEFGHIJ",
			Array.empty(),
			"0123456789", "ABCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0),
			"ABCDEFGHIJ", "0123456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(1),
			"0BCDEFGHIJ", "A123456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(2),
			"01CDEFGHIJ", "AB23456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(3),
			"012DEFGHIJ", "ABC3456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(8),
			"01234567IJ", "ABCDEFGH89"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(9),
			"012345678J", "ABCDEFGHI9"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(10),
			"0123456789", "ABCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1),
			"A123456789", "0BCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(3, 5),
			"012DE56789", "ABC34FGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2),
			"A1CDEFGHIJ", "0B23456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2, 3),
			"A1C3456789", "0B2DEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2, 3, 4),
			"A1C3EFGHIJ", "0B2D456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2, 3, 4, 5),
			"A1C3E56789", "0B2D4FGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2, 3, 4, 5, 6),
			"A1C3E5GHIJ", "0B2D4F6789"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2, 3, 4, 5, 6, 7),
			"A1C3E5G789", "0B2D4F6HIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2, 3, 4, 5, 6, 7, 8),
			"A1C3E5G7IJ", "0B2D4F6H89"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
			"A1C3E5G7I9", "0B2D4F6H8J"
		},{
			"0123456789", "ABCDEFGHIJ",
			Array.box(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
			"A1C3E5G7I9", "0B2D4F6H8J"
		},

		{
			"012345678", "ABCDEFGHI",
			Array.empty(),
			"012345678", "ABCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0),
			"ABCDEFGHI", "012345678"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(1),
			"0BCDEFGHI", "A12345678"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(2),
			"01CDEFGHI", "AB2345678"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(3),
			"012DEFGHI", "ABC345678"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(8),
			"01234567I", "ABCDEFGH8"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(9),
			"012345678", "ABCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1),
			"A12345678", "0BCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(3, 5),
			"012DE5678", "ABC34FGHI"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1, 2),
			"A1CDEFGHI", "0B2345678"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1, 2, 3),
			"A1C345678", "0B2DEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1, 2, 3, 4),
			"A1C3EFGHI", "0B2D45678"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1, 2, 3, 4, 5),
			"A1C3E5678", "0B2D4FGHI"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1, 2, 3, 4, 5, 6),
			"A1C3E5GHI", "0B2D4F678"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1, 2, 3, 4, 5, 6, 7),
			"A1C3E5G78", "0B2D4F6HI"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1, 2, 3, 4, 5, 6, 7, 8),
			"A1C3E5G7I", "0B2D4F6H8"
		},{
			"012345678", "ABCDEFGHI",
			Array.box(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
			"A1C3E5G7I", "0B2D4F6H8"
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

			//System.out.println(i + ": " + ma1);
		}
	}

	@Test(dataProvider = "numberOfCrossoverPoints")
	public void reverseCrossover(final Integer npoints) {
		for (int i = 1; i < 500; ++i) {
			final CharSeq chars = CharSeq.valueOf("a-zA-Z");
			final ISeq<Character> a = new CharacterChromosome(chars, i)
											.toSeq().map(CharacterGene.Allele);
			final ISeq<Character> b = new CharacterChromosome(chars, i)
											.toSeq().map(CharacterGene.Allele);

			final MSeq<Character> ma1 = a.copy();
			final MSeq<Character> mb1 = b.copy();
			final int[] points = arrays.subset(
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
		return new Array<Object[]>(11).fill(new Factory<Object[]>() {
			private int point = 0;
			@Override public Object[] newInstance() {
				return new Object[]{++point};
			}

		}).iterator();
	}

	@Test
	public void crossoverAll1() {
		final CharSeq chars = CharSeq.valueOf("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

		final MultiPointCrossover<CharacterGene> crossover =
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

			final String l1 = String.format( "%6s: %s  %s", Array.box(indexes[i]), a, ma);
			final String l2 = String.format( "        %s  %s",    b, mb);

			System.out.println(l1);
			System.out.println(l2);
			System.out.println();
		}
	}

}






















