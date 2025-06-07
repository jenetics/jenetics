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
package io.jenetics;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public abstract class NumericGeneTester<
	N extends Number & Comparable<N>,
	G extends NumericGene<N,G>
>
	extends GeneTester<G>
{

	@Test
	public void newInstanceFromNumber() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = factory().newInstance();
			final G gene2 = gene1.newInstance(gene1.allele());

			assertThat(gene2).isEqualTo(gene1);
		}
	}

	@Test
	public void minMax() {
		for (int i = 0; i < 1000; ++i) {
			final G gene = factory().newInstance();

			assertThat(gene.allele()).isGreaterThanOrEqualTo(gene.min());
			assertThat(gene.allele()).isLessThan(gene.max());
		}
	}

	@Test
	public void compareTo() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = factory().newInstance();
			final G gene2 = factory().newInstance();

			if (gene1.allele().compareTo(gene2.allele()) > 0) {
				assertThat(gene1).isGreaterThan(gene2);
			} else if (gene1.allele().compareTo(gene2.allele()) < 0) {
				assertThat(gene1).isLessThan(gene2);
			} else {
				assertThat(gene1).isEqualTo(gene2);
			}
		}
	}

}
