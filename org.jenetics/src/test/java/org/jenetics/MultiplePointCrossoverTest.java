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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.CharSeq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Seq;
import org.jenetics.util.arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class MultiplePointCrossoverTest {

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

	@Test
	public void crossoverAll1() {
		final CharSeq chars = CharSeq.valueOf("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

		final MultiplePointCrossover<CharacterGene> crossover = new MultiplePointCrossover<>(2);
		final int[] points = new int[g1.length()];
		for (int i = 0; i < points.length; ++i) {
			points[i] = i;
		}

		final MSeq<CharacterGene> ms1 = g1.copy();
		final MSeq<CharacterGene> ms2 = g2.copy();

		crossover.crossover(ms1, ms2, points);

		for (int i = 1; i < points.length; i += 2) {
			final int start = points[i - 1];
			final int end = points[i];

			Seq<CharacterGene> actual = ms2.subSeq(start, end);
			Seq<CharacterGene> expected = g1.subSeq(start, end);

			Assert.assertEquals(actual, expected);
		}
	}

	@Test
	public void crossoverAll2() {
		final CharSeq chars = CharSeq.valueOf("a-zA-Z");
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

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

	static void print(final Seq<CharacterGene> genes, final int[] points) {

	}

}






















