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
package io.jenetics.prog.regression;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SampleBufferTest {

	@Test
	public void add() {
		final SampleBuffer<Double> buffer = new SampleBuffer<>(33);
		for (int i = 0; i < 10; ++i) {
			buffer.add(Sample.ofDouble(i, 2*i));
		}

		Assert.assertEquals(buffer.samples(), List.of());
	}

	@Test
	public void publish() {
		final SampleBuffer<Double> buffer = new SampleBuffer<>(33);
		for (int i = 0; i < 10; ++i) {
			buffer.add(Sample.ofDouble(i, 2*i));
		}
		buffer.publish();

		Assert.assertEquals(
			buffer.samples().stream()
				.map(p -> p.argAt(0).intValue())
				.collect(Collectors.toList()),
			List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
		);
	}

}
