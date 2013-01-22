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
package org.jenetics.util;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-01-22 $</em>
 */
public class MSeqListAdapterTest {

	private final Random _random = RandomRegistry.getRandom();

	private final Factory<Double> _factory = new Factory<Double>() {
		@Override
		public Double newInstance() {
			return _random.nextDouble();
		}
	};

	@Test
	public void equals() {
		final MSeq<Double> seq = new Array<>(1000);
		final List<Double> list = new MSeqListAdapter<>(seq);

		Assert.assertNotSame(seq, list);
		assertSameElements(seq, list);

		seq.fill(_factory);
		seq.forall(object.NonNull);

		assertSameElements(seq, list);
	}

	@Test
	public void hash() {
		final MSeq<Double> seq = new Array<>(1000);
		final List<Double> list = new MSeqListAdapter<>(seq);

		Assert.assertNotSame(seq, list);
		Assert.assertEquals(seq.hashCode(), list.hashCode());

		seq.fill(_factory);
		Assert.assertEquals(seq.hashCode(), list.hashCode());
	}

	private static <T> void assertSameElements(final Iterable<T> a, final Iterable<T> b) {
		final Iterator<T> it1 = a.iterator();
		final Iterator<T> it2 = b.iterator();

		while (it1.hasNext() && it2.hasNext()) {
			assert(object.eq(it1.next(), it2.next()));
		}
		assert(!(it1.hasNext() || it2.hasNext()));
	}

}








