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
package io.jenetics.incubator.grammar;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import io.jenetics.incubator.grammar.Cfg.Rule;

// Some experiments
public class FairSymbolIndex implements SymbolIndex {

	private static final class Counts {
		private final int[] _counts;

		private int _count = 0;

		private Counts(final int size) {
			_counts = new int[size];
		}

		int update(final int index) {
			final int max = Math.max(_count/_counts.length*3, 3);

			if (_counts[index] <= max) {
				++_count;
				++_counts[index];

				return index;
			} else {
				//System.out.println(max + ":" + _counts[index] + ":" + index);
				return update((index + 1)%_counts.length);
			}
		}
	}

	private final SymbolIndex _index;
	private final Map<Rule, Counts> _counts = new HashMap<>();

	public FairSymbolIndex(final SymbolIndex index) {
		_index = requireNonNull(index);
	}

	@Override
	public int next(final Rule rule) {
		return _counts
			.computeIfAbsent(rule, key -> new Counts(rule.alternatives().size()))
			.update(_index.next(rule));
	}
}
