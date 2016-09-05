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
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.jenetics.random.utils.listOf;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jenetics.random.XOR32ShiftRandom.Param;
import org.jenetics.random.XOR32ShiftRandom.Shift;
import org.jenetics.random.internal.DieHarder;
import org.jenetics.random.internal.DieHarder.Assessment;
import org.jenetics.random.internal.DieHarder.Result;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class XOR32ShiftRandomParamTest {

	private final static List<Param> ALL_PARAMS = listOf(
		new Param( 1, 3,10),
		new Param( 1, 5,16),
		new Param( 1, 5,19),
		new Param( 1, 9,29),
		new Param( 1,11, 6),
		new Param( 1,11,16),
		new Param( 1,19, 3),
		new Param( 1,21,20),
		new Param( 1,27,27),
		new Param( 2, 5,15),
		new Param( 2, 5,21),
		new Param( 2, 7, 7),
		new Param( 2, 7, 9),
		new Param( 2, 7,25),
		new Param( 2, 9,15),
		new Param( 2,15,17),
		new Param( 2,15,25),
		new Param( 2,21, 9),
		new Param( 3, 1,14),
		new Param( 3, 3,26),
		new Param( 3, 3,28),
		new Param( 3, 3,29),
		new Param( 3, 5,20),
		new Param( 3, 5,22),
		new Param( 3, 5,25),
		new Param( 3, 7,29),
		new Param( 3,13, 7),
		new Param( 3,23,25),
		new Param( 3,25,24),
		new Param( 3,27,11),
		new Param( 4, 3,17),
		new Param( 4, 3,27),
		new Param( 4, 5,15),
		new Param( 5, 3,21),
		new Param( 5, 7,22),
		new Param( 5, 9,7 ),
		new Param( 5, 9,28),
		new Param( 5, 9,31),
		new Param( 5,13, 6),
		new Param( 5,15,17),
		new Param( 5,17,13),
		new Param( 5,21,12),
		new Param( 5,27, 8),
		new Param( 5,27,21),
		new Param( 5,27,25),
		new Param( 5,27,28),
		new Param( 6, 1,11),
		new Param( 6, 3,17),
		new Param( 6,17, 9),
		new Param( 6,21, 7),
		new Param( 6,21,13),
		new Param( 7, 1, 9),
		new Param( 7, 1,18),
		new Param( 7, 1,25),
		new Param( 7,13,25),
		new Param( 7,17,21),
		new Param( 7,25,12),
		new Param( 7,25,20),
		new Param( 8, 7,23),
		new Param( 8,9,23 ),
		new Param( 9, 5,1 ),
		new Param( 9, 5,25),
		new Param( 9,11,19),
		new Param( 9,21,16),
		new Param(10, 9,21),
		new Param(10, 9,25),
		new Param(11, 7,12),
		new Param(11, 7,16),
		new Param(11,17,13),
		new Param(11,21,13),
		new Param(12, 9,23),
		new Param(13, 3,17),
		new Param(13, 3,27),
		new Param(13, 5,19),
		new Param(13,17,15),
		new Param(14, 1,15),
		new Param(14,13,15),
		new Param(15, 1,29),
		new Param(17,15,20),
		new Param(17,15,23),
		new Param(17,15,26)
	);

	// ./jrun org.jenetics.random.XOR32ShiftRandomParamTest 2>> XOR32ShiftRandom.results
	public static void main(final String[] args) throws Exception {
		final int start = Stream.of(args).findFirst()
			.map(Integer::new)
			.orElse(0);

		for (Shift shift : Shift.values()) {
			System.err.println("#=============================================================================#");
			System.err.println("# Shift: " + shift);
			System.err.println("#=============================================================================#");

			ALL_PARAMS.subList(start, ALL_PARAMS.size())
				.parallelStream()
				.forEach(param -> test(shift, param));
		}
	}

	private static void test(final Shift shift, final Param param) {
		try {
			final XOR32ShiftRandom random = new XOR32ShiftRandom(param);
			final List<Result> results = DieHarder.test(random, asList("-a"), System.out);

			final Map<Assessment, Long> grouped = results.stream()
				.collect(groupingBy(r -> r.assessment, counting()));

			final long passed = grouped.getOrDefault(Assessment.PASSED, 0L);
			final long weak = grouped.getOrDefault(Assessment.WEAK, 0L);
			final long failed = grouped.getOrDefault(Assessment.FAILED, 0L);

			synchronized (System.err) {
				System.err.println(format(
					"%d; %d; %d; %d; %s",
					(passed - failed), passed, weak, failed, param
				));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
