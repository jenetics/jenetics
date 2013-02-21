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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javolution.context.LocalContext;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.Array;
import org.jenetics.util.CharSeq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Seq;
import org.jenetics.util.arrays;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-02-22 $</em>
 */
public class MultiplePointCrossoverTest {

	@Test(dataProvider = "parameters")
	public void crossover(
		final String stringA,
		final String stringB,
		final Points points,
		final String expectedA,
		final String expectedB
			) {
		final ISeq<Character> a = CharSeq.toISeq(stringA);
		final ISeq<Character> b = CharSeq.toISeq(stringB);

		final MSeq<Character> ma = a.copy();
		final MSeq<Character> mb = b.copy();

		MultiplePointCrossover.crossover(ma, mb, points.points);
		Assert.assertEquals(ma, CharSeq.toISeq(expectedA));
		Assert.assertEquals(mb, CharSeq.toISeq(expectedB));
	}

	@DataProvider(name = "parameters")
	public Object[][] getParameters() {
		return new Object[][] {{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{}),
			"0123456789", "ABCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0}),
			"ABCDEFGHIJ", "0123456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{1}),
			"0BCDEFGHIJ", "A123456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{2}),
			"01CDEFGHIJ", "AB23456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{3}),
			"012DEFGHIJ", "ABC3456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{8}),
			"01234567IJ", "ABCDEFGH89"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{9}),
			"012345678J", "ABCDEFGHI9"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{10}),
			"0123456789", "ABCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1}),
			"A123456789", "0BCDEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{3, 5}),
			"012DE56789", "ABC34FGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2}),
			"A1CDEFGHIJ", "0B23456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2, 3}),
			"A1C3456789", "0B2DEFGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2, 3, 4}),
			"A1C3EFGHIJ", "0B2D456789"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2, 3, 4, 5}),
			"A1C3E56789", "0B2D4FGHIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6}),
			"A1C3E5GHIJ", "0B2D4F6789"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6, 7}),
			"A1C3E5G789", "0B2D4F6HIJ"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}),
			"A1C3E5G7IJ", "0B2D4F6H89"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}),
			"A1C3E5G7I9", "0B2D4F6H8J"
		},{
			"0123456789", "ABCDEFGHIJ",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}),
			"A1C3E5G7I9", "0B2D4F6H8J"
		},

		{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{}),
			"012345678", "ABCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0}),
			"ABCDEFGHI", "012345678"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{1}),
			"0BCDEFGHI", "A12345678"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{2}),
			"01CDEFGHI", "AB2345678"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{3}),
			"012DEFGHI", "ABC345678"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{8}),
			"01234567I", "ABCDEFGH8"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{9}),
			"012345678", "ABCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1}),
			"A12345678", "0BCDEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{3, 5}),
			"012DE5678", "ABC34FGHI"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1, 2}),
			"A1CDEFGHI", "0B2345678"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1, 2, 3}),
			"A1C345678", "0B2DEFGHI"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1, 2, 3, 4}),
			"A1C3EFGHI", "0B2D45678"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1, 2, 3, 4, 5}),
			"A1C3E5678", "0B2D4FGHI"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6}),
			"A1C3E5GHI", "0B2D4F678"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6, 7}),
			"A1C3E5G78", "0B2D4F6HI"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}),
			"A1C3E5G7I", "0B2D4F6H8"
		},{
			"012345678", "ABCDEFGHI",
			new Points(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}),
			"A1C3E5G7I", "0B2D4F6H8"
		}
		};
	}

	private static final class Points {
		final int[] points;
		Points(final int[] points) {
			this.points = points;
		}

		@Override
		public String toString() {
			return Arrays.toString(points);
		}
	}

	/*
	@Test
	public void crossover() {
		final MSeq<Integer64Gene> s1 = new Array<>(1);
	}

	@Test
	public void crossover1() {
		final CharSeq chars = CharSeq.valueOf("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

		final SinglePointCrossover<CharacterGene> spco = new SinglePointCrossover<>();
		final MultiplePointCrossover<CharacterGene> nco = new MultiplePointCrossover<>(1);

		for (int i = 0; i < g1.length(); ++i) {
			final MSeq<CharacterGene> m11 = g1.copy();
			final MSeq<CharacterGene> m12 = g2.copy();
			spco.crossover(m11, m12, i);

			final MSeq<CharacterGene> m21 = g1.copy();
			final MSeq<CharacterGene> m22 = g2.copy();
			nco.crossover(m21, m22, new int[]{i});

			Assert.assertEquals(m21, m11);
			Assert.assertEquals(m22, m12);
		}
	}

	//@Test(dataProvider = "parameters")
	public void crossover(final Integer nchromosomes, final Integer npoints) {
		final long seed = math.random.seed();

		final MultiplePointCrossover<CharacterGene> co = new MultiplePointCrossover<>(npoints);

		final CharSeq chars = CharSeq.valueOf("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, nchromosomes).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, nchromosomes).toSeq();

		final MSeq<CharacterGene> m1 = g1.copy();
		final MSeq<CharacterGene> m2 = g2.copy();

		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random(seed));
			co.crossover(m1, m2);
		} finally {
			LocalContext.exit();
		}

		RandomRegistry.setRandom(new Random(seed));
		final int[] points = arrays.subset(nchromosomes, npoints, new Random(seed));

		for (int i = 1; i < points.length; i += 2) {
			final int start = points[i - 1];
			final int end = points[i];

			Seq<CharacterGene> actual = m2.subSeq(start, end);
			Seq<CharacterGene> expected = g1.subSeq(start, end);

			System.out.println("" + nchromosomes + ":" + npoints + " - " + Arrays.toString(points));
			System.out.println(g1);
			System.out.println(g2);
			System.out.println("-------");
			System.out.println(m1);
			System.out.println(m2);
			System.err.println();

			Assert.assertEquals(actual, expected);
		}

	}

	@DataProvider(name = "parameters")
	public Object[][] getParameters() {
		return new Object[][] {
			{1, 1},
			{2, 1},
			{3, 1},
			{3, 2},
			{3, 3},
			{4, 1},
			{4, 2},
			{4, 3},
			{6, 6}
		};
	}
*/
	@Test
	public void crossoverAll1() {
		final CharSeq chars = CharSeq.valueOf("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

		final MultiplePointCrossover<CharacterGene> crossover =
				new MultiplePointCrossover<>(2000);
		final int[] points = new int[g1.length()];
		for (int i = 0; i < points.length; ++i) {
			points[i] = i;
		}

		final MSeq<CharacterGene> ms1 = g1.copy();
		final MSeq<CharacterGene> ms2 = g2.copy();

		crossover.crossover(ms1, ms2);
	}

	/*
	@Test
	public void crossoverAll2() {
		final CharSeq chars = CharSeq.valueOf("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 21).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 21).toSeq();

		final MultiplePointCrossover<CharacterGene> crossover =
				new MultiplePointCrossover<>(Integer.MAX_VALUE);

		final MSeq<CharacterGene> ms1 = g1.copy();
		final MSeq<CharacterGene> ms2 = g2.copy();

		crossover.crossover(ms1, ms2);

		for (int i = 1; i < g1.length(); i += 2) {
			final int start = i - 1;
			final int end = i;

			Seq<CharacterGene> actual = ms2.subSeq(start, end);
			Seq<CharacterGene> expected = g1.subSeq(start, end);

			Assert.assertEquals(actual, expected);
		}
	}

	@Test(dataProvider = "points")
	public void crossover(final Points points) {
		final CharSeq chars = CharSeq.valueOf("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

		final MultiplePointCrossover<CharacterGene> crossover = new MultiplePointCrossover<>(2);

		final MSeq<CharacterGene> ms1 = g1.copy();
		final MSeq<CharacterGene> ms2 = g2.copy();

		//System.out.println(points);

		//System.out.println(ms1);
		//System.out.println(ms2);

		crossover.crossover(ms1, ms2, points.points);
		//System.out.println("--------------------------------");

		//System.out.println(ms1);
		//System.out.println(ms2);

		//ssSystem.out.println();

		for (int i = 1; i < points.points.length; i += 2) {
			final int start = points.points[i - 1];
			final int end = points.points[i];

			Seq<CharacterGene> actual = ms2.subSeq(start, end);
			Seq<CharacterGene> expected = g1.subSeq(start, end);

			Assert.assertEquals(actual, expected);
		}

	}

	@DataProvider(name = "points")
	public Iterator<Object[]> getPoints() {
		final int n = 20;
		final Random random = new Random(12334);

		final List<Object[]> elements = new ArrayList<>();
		for (int i = 0; i < 3; ++i) {
			for (int k = 1; k < n; k += 3) {
				elements.add(new Object[]{new Points(arrays.subset(n, k, random))});
			}
		}
		elements.add(new Object[]{new Points(arrays.subset(n, n, random))});

		return elements.iterator();
	}

	private static final class Points {
		final int[] points;
		Points(final int[] points) {
			this.points = points;
		}

		@Override
		public String toString() {
			return Arrays.toString(points);
		}
	}
	*/

}






















