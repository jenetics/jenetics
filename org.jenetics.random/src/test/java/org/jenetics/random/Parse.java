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
package org.jenetics.random;

import static java.lang.String.format;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Parse {

	static final class Result {
		final String shift;
		final int score;
		final int passed;
		final int weak;
		final int failed;
		final String param;

		Result(
			final String shift,
			final int score,
			final int passed,
			final int weak,
			final int failed,
			final String param
		) {
			this.shift = shift;
			this.score = score;
			this.passed = passed;
			this.weak = weak;
			this.failed = failed;
			this.param = param;
		}

		@Override
		public String toString() {
			return format("%s; %s; %s; %s; %s", score, passed, weak, failed, param);
		}

		static Result of(final String line) {
			final String[] parts = line.split(";");
			return new Result(
				parts[0].trim(),
				Integer.parseInt(parts[1].trim()),
				Integer.parseInt(parts[2].trim()),
				Integer.parseInt(parts[3].trim()),
				Integer.parseInt(parts[4].trim()),
				parts[5]
			);
		}
	}

	public static void main(final String[] args) throws Exception {
		final List<String> lines = Files.readAllLines(Paths.get(
			"/home/fwilhelm/Downloads/XOR64ShiftRandom.results"
		));

		final Map<String, List<Result>> groups = lines.stream()
			.map(Result::of)
			.filter(r -> r.score >= 100)
			.collect(Collectors.groupingBy(r -> r.shift));

		for (String shift : groups.keySet()) {
			System.out.println(shift);
			final List<Result> results = groups.get(shift);
			results.sort((a, b) -> a.param.compareTo(b.param));

			System.out.println(results);
		}

		System.out.println("---------------------");
		final List<Result> results = intersect(groups.values());
		results.forEach(System.out::println);
	}

	static List<Result> intersect(final Collection<List<Result>> groups) {
		List<Result> result = groups.iterator().next();
		for (List<Result> r : groups) {
			result = intersect(result, r);
			//System.out.println(result);
		}

		return result;
	}

	private static List<Result> intersect(final List<Result> r1, final List<Result> r2) {
		return r1.stream()
			.filter(r -> contains(r2, r))
			.collect(Collectors.toList());
	}

	private static boolean contains(final List<Result> results, final Result result) {
		return results.stream()
			.map(r -> r.param)
			.collect(Collectors.toList())
			.contains(result.param);
	}

}
