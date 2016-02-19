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
package org.jenetics.tool.trial;

import java.util.Random;

import org.testng.annotations.Test;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public class TrialMeterTest {

	@Test
	public void measure() {
		final TrialMeter<String> trialMeter = TrialMeter.of(
			"Some name", "Some description",
			Params.of("Strings", ISeq.of("p1", "p2", "p3", "p4", "p5")),
			"fitness", "generation"
		);

		final Random random = new Random();

		for (int i = 0; i < 10; ++i) {
			trialMeter.sample(p -> {
				return new double[] {
					random.nextDouble(), random.nextDouble()
				};
			});
		}

		trialMeter.write(System.out);
	}

}
