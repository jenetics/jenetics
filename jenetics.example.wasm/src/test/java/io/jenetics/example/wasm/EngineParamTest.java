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
package io.jenetics.example.wasm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.testng.annotations.Test;

public class EngineParamTest {

	@Test
	public void defaults() {
		final EngineParam param = EngineParam.DEFAULT;
		assertThat(param.populationSize()).isEqualTo(50);
		assertThat(param.tournamentSize()).isEqualTo(3);
		assertThat(param.mutationRate()).isEqualTo(0.025F);
		assertThat(param.mutationMultitude()).isEqualTo(0.15F);
		assertThat(param.polygonLength()).isEqualTo(4);
		assertThat(param.polygonCount()).isEqualTo(250);
		assertThat(param.referenceWidth()).isEqualTo(60);
		assertThat(param.referenceHeight()).isEqualTo(60);

		param.validate();
	}

	@Test
	public void invalidValues() {
		assertThatThrownBy(() -> new EngineParam(2, 3, 0.025F, 0.15F, 4, 250, 60, 60).validate())
			.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> new EngineParam(50, 1, 0.025F, 0.15F, 4, 250, 60, 60).validate())
			.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> new EngineParam(50, 3, -0.1F, 0.15F, 4, 250, 60, 60).validate())
			.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> new EngineParam(50, 3, 0.025F, 1.5F, 4, 250, 60, 60).validate())
			.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> new EngineParam(50, 3, 0.025F, 0.15F, 2, 250, 60, 60).validate())
			.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> new EngineParam(50, 3, 0.025F, 0.15F, 4, 1, 60, 60).validate())
			.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> new EngineParam(50, 3, 0.025F, 0.15F, 4, 250, 1, 60).validate())
			.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> new EngineParam(50, 3, 0.025F, 0.15F, 4, 250, 60, 1).validate())
			.isInstanceOf(IllegalArgumentException.class);
	}
}
