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

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class EnumGeneTest extends GeneTester<EnumGene<Integer>> {

	private final Factory<EnumGene<Integer>>
	_factory = new Factory<EnumGene<Integer>>() {
		private ISeq<Integer> _alleles = new Array<Integer>(100).fill(Int()).toISeq();

		@Override
		public EnumGene<Integer> newInstance() {
			return EnumGene.valueOf(_alleles);
		}

	};

	@Override
	protected Factory<EnumGene<Integer>> getFactory() {
		return _factory;
	}

	@Test
	public void valueOf() {
		final int length = 100;
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();

		Assert.assertEquals(alleles.length(), length);
		for (int i = 0; i < alleles.length(); ++i) {
			Assert.assertEquals(alleles.get(i), new Integer(i));
		}

		for (int i = 0; i < alleles.length(); ++i) {
			Assert.assertEquals(EnumGene.valueOf(alleles, i).getAllele(), new Integer(i));
			Assert.assertSame(EnumGene.valueOf(alleles, i).getValidAlleles(), alleles);
		}
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void valueOfIndexOutOfBounds1() {
		final int length = 100;
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();

		EnumGene.valueOf(alleles, length + 1);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void valueOfIndexOutOfBounds2() {
		final int length = 100;
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();

		EnumGene.valueOf(alleles, -1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void valueOfZeroLength() {
		final int length = 0;
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();

		EnumGene.valueOf(alleles);
	}

}




