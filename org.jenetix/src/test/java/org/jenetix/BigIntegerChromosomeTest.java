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
package org.jenetix;

import java.math.BigInteger;

import org.testng.annotations.Test;

import org.jenetics.Chromosome;
import org.jenetics.NumericChromosomeTester;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
 */
@Test
public class BigIntegerChromosomeTest
	extends NumericChromosomeTester<BigInteger, BigIntegerGene>

{
	private final BigIntegerChromosome _factory = new BigIntegerChromosome(
		BigInteger.ZERO, BigInteger.valueOf(Long.MAX_VALUE), 500
	);

	@Override
	protected Factory<Chromosome<BigIntegerGene>> factory() {
		return _factory;
	}
}
