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
package org.jenetics.tool.optimizer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.Alterer;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Codec;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class AltererCodecTest {

	@Test
	public void altererCodec() {
		AltererCodec<DoubleGene, Double> codec =
			AltererCodec.<DoubleGene, Double>ofSwapMutator()
				.and(AltererCodec.ofMeanAlterer())
				.and(AltererCodec.ofMutator());

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 4);
	}

	@Test(dataProvider = "alterers")
	public void altererCodecs(final ISeq<Codec<Alterer<DoubleGene, Double>, DoubleGene>> alterers) {
		AltererCodec<DoubleGene, Double> codec =
			AltererCodec.of(alterers, ISeq.empty());

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), alterers.length() + 1);
	}

	@SuppressWarnings("rawtype")
	@DataProvider(name = "alterers")
	public Object[][] alterers() {
		return new Object[][] {
			{ISeq.of(AltererCodec.ofSinglePointCrossover())},
			{ISeq.of(
				AltererCodec.ofSinglePointCrossover(),
				AltererCodec.ofMutator()
			)},
			{ISeq.of(
				AltererCodec.ofSinglePointCrossover(),
				AltererCodec.ofMutator(),
				AltererCodec.ofSwapMutator()
			)}
		};
	}

	@Test
	public void altererCodecSelection() {
		AltererCodec<DoubleGene, Double> codec =
			AltererCodec.<DoubleGene, Double>ofSwapMutator()
				.and(AltererCodec.ofMeanAlterer())
				.and(AltererCodec.ofMutator());

		final int samples = 10000;
		final Map<Class<?>, AtomicInteger> histogram = new HashMap<>();
		for (int i = 0; i < samples; ++i) {
			final Genotype<DoubleGene> gt = codec.encoding().newInstance();
			final Alterer<DoubleGene, Double> alterer = codec.decoder().apply(gt);

			histogram
				.computeIfAbsent(alterer.getClass(), key -> new AtomicInteger())
				.incrementAndGet();
		}

		Assert.assertTrue(histogram.get(SwapMutator.class).intValue() > samples/9);
		Assert.assertTrue(histogram.get(Mutator.class).intValue() > samples/9);
		Assert.assertTrue(histogram.get(MeanAlterer.class).intValue() > samples/9);
	}

}
