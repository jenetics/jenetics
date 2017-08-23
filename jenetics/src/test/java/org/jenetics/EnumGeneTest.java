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

import static org.jenetics.util.factories.Int;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class EnumGeneTest extends GeneTester<EnumGene<Integer>> {

	private final Factory<EnumGene<Integer>>
	_factory = new Factory<EnumGene<Integer>>() {
		private ISeq<Integer> _alleles = MSeq.<Integer>ofLength(100).fill(Int()).toISeq();

		@Override
		public EnumGene<Integer> newInstance() {
			return EnumGene.of(_alleles);
		}

	};

	@Override
	protected Factory<EnumGene<Integer>> factory() {
		return _factory;
	}

	@Test
	public void valueOf() {
		final int length = 100;
		final ISeq<Integer> alleles = MSeq.<Integer>ofLength(length).fill(Int()).toISeq();

		Assert.assertEquals(alleles.length(), length);
		for (int i = 0; i < alleles.length(); ++i) {
			Assert.assertEquals(alleles.get(i), Integer.valueOf(i));
		}

		for (int i = 0; i < alleles.length(); ++i) {
			Assert.assertEquals(new EnumGene<>(i, alleles).getAllele(), Integer.valueOf(i));
			Assert.assertSame(new EnumGene<>(i, alleles).getValidAlleles(), alleles);
		}
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void valueOfIndexOutOfBounds1() {
		final int length = 100;
		final ISeq<Integer> alleles = MSeq.<Integer>ofLength(length).fill(Int()).toISeq();

		new EnumGene<>(length + 1, alleles);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void valueOfIndexOutOfBounds2() {
		final int length = 100;
		final ISeq<Integer> alleles = MSeq.<Integer>ofLength(length).fill(Int()).toISeq();

		new EnumGene<>(-1, alleles);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void valueOfZeroLength() {
		final int length = 0;
		final ISeq<Integer> alleles = MSeq.<Integer>ofLength(length).fill(Int()).toISeq();

		EnumGene.of(alleles);
	}

}
