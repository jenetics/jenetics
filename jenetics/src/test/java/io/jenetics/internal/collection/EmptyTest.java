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
package io.jenetics.internal.collection;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EmptyTest {

	@Test
	public void seqSubSeq() {
		final Seq<String> empty = Seq.empty();
		Assert.assertEquals(empty.subSeq(0), empty);
		Assert.assertEquals(empty.subSeq(0, 0), empty);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void seqSubSeqOutOfBounds1() {
		Seq.empty().subSeq(-1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void seqSubSeqOutOfBounds2() {
		Seq.empty().subSeq(1);
	}

	@Test
	public void mseqSubSeq() {
		final MSeq<String> empty = MSeq.empty();
		Assert.assertEquals(empty.subSeq(0), empty);
		Assert.assertEquals(empty.subSeq(0, 0), empty);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void mseqSubSeqOutOfBounds1() {
		MSeq.empty().subSeq(-1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void mseqSubSeqOutOfBounds2() {
		MSeq.empty().subSeq(1);
	}

	@Test
	public void iseqSubSeq() {
		final ISeq<String> empty = ISeq.empty();
		Assert.assertEquals(empty.subSeq(0), empty);
		Assert.assertEquals(empty.subSeq(0, 0), empty);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void iseqSubSeqOutOfBounds1() {
		ISeq.empty().subSeq(-1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void iseqSubSeqOutOfBounds2() {
		ISeq.empty().subSeq(1);
	}

}
