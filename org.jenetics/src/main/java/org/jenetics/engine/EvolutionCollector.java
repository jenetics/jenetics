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
package org.jenetics.engine;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public class EvolutionCollector {


	private static final class Result<C extends Comparable<? super C>> {

	}

//	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
//	Collector<EvolutionResult<G, C>, ?, C> best() {
//
//
//		return Collector.of(
//			(Supplier<List<C>>)ArrayList::new,
//			(list, result) -> list.add(result.getPopulation()),
//			(left, right) -> { left.addAll(right); return left; },
//			list -> list.stream().collect(null)
//		);
//	}

}
