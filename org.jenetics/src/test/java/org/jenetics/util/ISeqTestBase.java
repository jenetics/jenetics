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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public abstract class ISeqTestBase extends SeqTestBase {

	@Override
	protected abstract ISeq<Integer> newSeq(final int length);

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void asList() {
		final ISeq<Integer> seq = newSeq(1000);
		final List<Integer> list = seq.asList();

		list.set(3, 3);
	}

	@Test(dataProvider = "sequences")
	public void copy(final ISeq<Integer> seq) {
		final MSeq<Integer> copy = seq.copy();
		Assert.assertEquals(copy, seq);

		final Integer[] mcopy = copy.toArray(new Integer[0]);
		for (int i = 0; i < mcopy.length; ++i) {
			Assert.assertEquals(mcopy[i], seq.get(i));
		}

		final long seed = math.random.seed();
		final Random random = new Random(seed);
		for (int i = 0; i < copy.length(); ++i) {
			copy.set(i, random.nextInt());
		}

		for (int i = 0; i < mcopy.length; ++i) {
			Assert.assertEquals(mcopy[i], seq.get(i));
		}

		random.setSeed(seed);
		for (int i = 0; i < copy.length(); ++i) {
			Assert.assertEquals(copy.get(i).intValue(), random.nextInt());
		}
	}
}
