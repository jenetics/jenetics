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

import org.testng.annotations.Test;

import io.jenetics.ext.moea.Vecs.DoubleVec;
import io.jenetics.ext.moea.Vecs.IntVec;
import io.jenetics.ext.moea.Vecs.LongVec;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class VecsTest {

	@Test
	public void intVecEqualsVerifier() {
		EqualsVerifier.forClass(IntVec.class).verify();
	}

	@Test
	public void longVecEqualsVerifier() {
		EqualsVerifier.forClass(LongVec.class).verify();
	}

	@Test
	public void doubleVecEqualsVerifier() {
		EqualsVerifier.forClass(DoubleVec.class).verify();
	}

}
