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
package io.jenetics.ext.moea;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class VecsTest {

	@Test
	public void simpleIntVecEqualsVerifier() {
		EqualsVerifier.forClass(SimpleIntVec.class).verify();
	}

	@Test
	public void simpleLongVecEqualsVerifier() {
		EqualsVerifier.forClass(SimpleLongVec.class).verify();
	}

	@Test
	public void simpleDoubleVecEqualsVerifier() {
		EqualsVerifier.forClass(SimpleDoubleVec.class).verify();
	}

	@Test
	public void generalIntVecEqualsVerifier() {
		EqualsVerifier.forClass(GeneralIntVec.class)
			.withPrefabValues(Object.class, new int[]{1, 3, 2}, new int[]{1})
			.withOnlyTheseFields("_data")
			.verify();
	}

	@Test
	public void generalLongVecEqualsVerifier() {
		EqualsVerifier.forClass(GeneralLongVec.class)
			.withPrefabValues(Object.class, new long[]{1, 3, 2}, new long[]{1})
			.withOnlyTheseFields("_data")
			.verify();
	}

	@Test
	public void generalDoubleVecEqualsVerifier() {
		EqualsVerifier.forClass(GeneralDoubleVec.class)
			.withPrefabValues(Object.class, new double[]{1, 3, 2}, new double[]{1})
			.withOnlyTheseFields("_data")
			.verify();
	}

	@Test
	public void generalObjectVecEqualsVerifier() {
		EqualsVerifier.forClass(GeneralObjectVec.class)
			.withPrefabValues(Object.class, new String[]{"1", "3", "2"}, new String[]{"1"})
			.withOnlyTheseFields("_data")
			.verify();
	}

	@Test
	public void toFlags() {
		final Random random = new Random();

		final boolean[] flags = new boolean[100];
		final Optimize[] opts = new Optimize[flags.length];
		for (int i = 0; i < flags.length; ++i) {
			flags[i]  = random.nextBoolean();
			if (flags[i]) {
				opts[i] = Optimize.MAXIMUM;
			} else {
				opts[i] = Optimize.MINIMUM;
			}
		}

		Assert.assertEquals(Vecs.toFlags(Arrays.asList(opts)), flags);
	}

}
