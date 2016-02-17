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

import static org.jenetics.tool.optimizer.SelectorCodec.ofBoltzmannSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofExponentialRankSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofLinearRankSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofTournamentSelector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.BoltzmannSelector;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.ExponentialRankSelector;
import org.jenetics.Genotype;
import org.jenetics.LinearRankSelector;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.Selector;
import org.jenetics.StochasticUniversalSelector;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SelectorCodecTest {

	@Test(dataProvider = "selectors")
	public void selectorEncoding(final ISeq<Selector<DoubleGene, Double>> selectors) {
		final Codec<Selector<DoubleGene, Double>, DoubleGene> codec =
			SelectorCodec.of(ISeq.empty(), selectors);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 1);

		final DoubleChromosome ch = (DoubleChromosome)gt.getChromosome();
		Assert.assertEquals(ch.getMin(), 0.0);
		Assert.assertEquals(ch.getMax(), (double)selectors.length());

		final Selector<DoubleGene, Double> selector = codec.decoder().apply(gt);
		System.out.println(selector);
	}

	@Test(dataProvider = "selectors")
	public void selectorDecoding(final ISeq<Selector<DoubleGene, Double>> selectors) {
		final Codec<Selector<DoubleGene, Double>, DoubleGene> codec =
			SelectorCodec.of(ISeq.empty(), selectors);

		for (int i = 0; i < selectors.length(); ++i) {
			final Genotype<DoubleGene> gt =
				Genotype.of(DoubleChromosome.of(DoubleGene.of(i, 0, selectors.length())));
			Assert.assertEquals(gt.getGene().doubleValue(), (double)i);

			final Selector<DoubleGene, Double> selector = codec.decoder().apply(gt);
			Assert.assertEquals(selector, selectors.get(i));
		}
	}

	@SuppressWarnings("rawtypes")
	@DataProvider(name = "selectors")
	public Object[][] selectors() {
		return new Object[][] {
			{ISeq.of(new RouletteWheelSelector())},
			{ISeq.of(
				new RouletteWheelSelector(),
				new TruncationSelector()
			)},
			{ISeq.of(
				new RouletteWheelSelector(),
				new TruncationSelector(),
				new StochasticUniversalSelector()
			)}
		};
	}

	@Test
	public void selectorSelecting() {
		final ISeq<Selector<DoubleGene, Double>> selectors = ISeq.of(
			new RouletteWheelSelector<>(),
			new TruncationSelector<>(),
			new StochasticUniversalSelector<>()
		);

		final Codec<Selector<DoubleGene, Double>, DoubleGene> codec =
			SelectorCodec.of(ISeq.empty(), selectors);

		final Set<Selector<DoubleGene, Double>> selected = new HashSet<>();
		for (int i = 0; i < 100; ++i) {
			final Genotype<DoubleGene> gt = codec.encoding().newInstance();
			final Selector<DoubleGene, Double> selector = codec.decoder().apply(gt);
			selected.add(selector);
		}

		Assert.assertEquals(selected.size(), selectors.size());
		for (Selector<DoubleGene, Double> selector :  selectors) {
			Assert.assertTrue(selected.contains(selector));
		}
	}

	@Test
	public void selectorCodec() {
		final Codec<Selector<DoubleGene, Double>, DoubleGene> codec =
			SelectorCodec
				.of(new RouletteWheelSelector<DoubleGene, Double>())
				.and(new TruncationSelector<>())
				.and(new StochasticUniversalSelector<>())
				.and(ofBoltzmannSelector(DoubleRange.of(0, 3)))
				.and(ofExponentialRankSelector(DoubleRange.of(0, 1)))
				.and(ofLinearRankSelector(DoubleRange.of(0, 3)))
				.and(ofTournamentSelector(IntRange.of(2, 10)));

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 5);
	}

	@Test
	public void selectorCodecSelection() {
		final Codec<Selector<DoubleGene, Double>, DoubleGene> codec =
			SelectorCodec
				.of(new RouletteWheelSelector<DoubleGene, Double>())
				.and(new TruncationSelector<>())
				.and(new StochasticUniversalSelector<>())
				.and(ofBoltzmannSelector(DoubleRange.of(0, 3)))
				.and(ofExponentialRankSelector(DoubleRange.of(0, 1)))
				.and(ofLinearRankSelector(DoubleRange.of(0, 3)))
				.and(ofTournamentSelector(IntRange.of(2, 10)));

		final Set<Class<?>> classes = new HashSet<>();
		classes.add(RouletteWheelSelector.class);
		classes.add(TruncationSelector.class);
		classes.add(StochasticUniversalSelector.class);
		classes.add(BoltzmannSelector.class);
		classes.add(ExponentialRankSelector.class);
		classes.add(LinearRankSelector.class);
		classes.add(TournamentSelector.class);

		for (int i = 0; i < 100; ++i) {
			final Genotype<DoubleGene> gt = codec.encoding().newInstance();
			final Selector<DoubleGene, Double> selector = codec.decoder().apply(gt);

			classes.remove(selector.getClass());
		}

		Assert.assertEquals(classes, Collections.emptySet());
	}

}
