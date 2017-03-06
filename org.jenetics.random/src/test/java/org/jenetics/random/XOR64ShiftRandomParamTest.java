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
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.jenetics.random.utils.listOf;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jenetics.random.XOR64ShiftRandom.Param;
import org.jenetics.random.XOR64ShiftRandom.Shift;
import org.jenetics.random.internal.DieHarder;
import org.jenetics.random.internal.DieHarder.Assessment;
import org.jenetics.random.internal.DieHarder.Result;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class XOR64ShiftRandomParamTest {

	private static final List<Param> ALL_PARAMS = listOf(
		new Param( 1, 1,54),
		new Param( 1, 1,55),
		new Param( 1, 3,45),
		new Param( 1, 7, 9),
		new Param( 1, 7,44),
		new Param( 1, 7,46),
		new Param( 1, 9,50),
		new Param( 1,11,35),
		new Param( 1,11,50),
		new Param( 1,13,45),
		new Param( 1,15, 4),
		new Param( 1,15,63),
		new Param( 1,19, 6),
		new Param( 1,19,16),
		new Param( 1,23,14),
		new Param( 1,23,29),
		new Param( 1,29,34),
		new Param( 1,35, 5),
		new Param( 1,35,11),
		new Param( 1,35,34),
		new Param( 1,45,37),
		new Param( 1,51,13),
		new Param( 1,53, 3),
		new Param( 1,59,14),
		new Param( 2,13,23),
		new Param( 2,31,51),
		new Param( 2,31,53),
		new Param( 2,43,27),
		new Param( 2,47,49),
		new Param( 3, 1,11),
		new Param( 3, 5,21),
		new Param( 3,13,59),
		new Param( 3,21,31),
		new Param( 3,25,20),
		new Param( 3,25,31),
		new Param( 3,25,56),
		new Param( 3,29,40),
		new Param( 3,29,47),
		new Param( 3,29,49),
		new Param( 3,35,14),
		new Param( 3,37,17),
		new Param( 3,43, 4),
		new Param( 3,43, 6),
		new Param( 3,43,11),
		new Param( 3,51,16),
		new Param( 3,53, 7),
		new Param( 3,61,17),
		new Param( 3,61,26),
		new Param( 4, 7,19),
		new Param( 4, 9,13),
		new Param( 4,15,51),
		new Param( 4,15,53),
		new Param( 4,29,45),
		new Param( 4,29,49),
		new Param( 4,31,33),
		new Param( 4,35,15),
		new Param( 4,35,21),
		new Param( 4,37,11),
		new Param( 4,37,21),
		new Param( 4,41,19),
		new Param( 4,41,45),
		new Param( 4,43,21),
		new Param( 4,43,31),
		new Param( 4,53, 7),
		new Param( 5, 9,23),
		new Param( 5,11,54),
		new Param( 5,15,27),
		new Param( 5,17,11),
		new Param( 5,23,36),
		new Param( 5,33,29),
		new Param( 5,41,20),
		new Param( 5,45,16),
		new Param( 5,47,23),
		new Param( 5,53,20),
		new Param( 5,59,33),
		new Param( 5,59,35),
		new Param( 5,59,63),
		new Param( 6, 1,17),
		new Param( 6, 3,49),
		new Param( 6,17,47),
		new Param( 6,23,27),
		new Param( 6,27, 7),
		new Param( 6,43,21),
		new Param( 6,49,29),
		new Param( 6,55,17),
		new Param( 7, 5,41),
		new Param( 7, 5,47),
		new Param( 7, 5,55),
		new Param( 7, 7,20),
		new Param( 7, 9,38),
		new Param( 7,11,10),
		new Param( 7,11,35),
		new Param( 7,13,58),
		new Param( 7,19,17),
		new Param( 7,19,54),
		new Param( 7,23, 8),
		new Param( 7,25,58),
		new Param( 7,27,59),
		new Param( 7,33, 8),
		new Param( 7,41,40),
		new Param( 7,43,28),
		new Param( 7,51,24),
		new Param( 7,57,12),
		new Param( 8, 5,59),
		new Param( 8, 9,25),
		new Param( 8,13,25),
		new Param( 8,13,61),
		new Param( 8,15,21),
		new Param( 8,25,59),
		new Param( 8,29,19),
		new Param( 8,31,17),
		new Param( 8,37,21),
		new Param( 8,51,21),
		new Param( 9, 1,27),
		new Param( 9, 5,36),
		new Param( 9, 5,43),
		new Param( 9, 7,18),
		new Param( 9,19,18),
		new Param( 9,21,11),
		new Param( 9,21,20),
		new Param( 9,21,40),
		new Param( 9,23,57),
		new Param( 9,27,10),
		new Param( 9,29,12),
		new Param( 9,29,37),
		new Param( 9,37,31),
		new Param( 9,41,45),
		new Param(10, 7,33),
		new Param(10,27,59),
		new Param(10,53,13),
		new Param(11, 5,32),
		new Param(11, 5,34),
		new Param(11, 5,43),
		new Param(11, 5,45),
		new Param(11, 9,14),
		new Param(11, 9,34),
		new Param(11,13,40),
		new Param(11,15,37),
		new Param(11,23,42),
		new Param(11,23,56),
		new Param(11,25,48),
		new Param(11,27,26),
		new Param(11,29,14),
		new Param(11,31,18),
		new Param(11,53,23),
		new Param(12, 1,31),
		new Param(12, 3,13),
		new Param(12, 3,49),
		new Param(12, 7,13),
		new Param(12,11,47),
		new Param(12,25,27),
		new Param(12,39,49),
		new Param(12,43,19),
		new Param(13, 3,40),
		new Param(13, 3,53),
		new Param(13, 7,17),
		new Param(13, 9,15),
		new Param(13, 9,50),
		new Param(13,13,19),
		new Param(13,17,43),
		new Param(13,19,28),
		new Param(13,19,47),
		new Param(13,21,18),
		new Param(13,21,49),
		new Param(13,29,35),
		new Param(13,35,30),
		new Param(13,35,38),
		new Param(13,47,23),
		new Param(13,51,21),
		new Param(14,13,17),
		new Param(14,15,19),
		new Param(14,23,33),
		new Param(14,31,45),
		new Param(14,47,15),
		new Param(15, 1,19),
		new Param(15, 5,37),
		new Param(15,13,28),
		new Param(15,13,52),
		new Param(15,17,27),
		new Param(15,19,63),
		new Param(15,21,46),
		new Param(15,23,23),
		new Param(15,45,17),
		new Param(15,47,16),
		new Param(15,49,26),
		new Param(16, 5,17),
		new Param(16, 7,39),
		new Param(16,11,19),
		new Param(16,11,27),
		new Param(16,13,55),
		new Param(16,21,35),
		new Param(16,25,43),
		new Param(16,27,53),
		new Param(16,47,17),
		new Param(17,15,58),
		new Param(17,23,29),
		new Param(17,23,51),
		new Param(17,23,52),
		new Param(17,27,22),
		new Param(17,45,22),
		new Param(17,47,28),
		new Param(17,47,29),
		new Param(17,47,54),
		new Param(18, 1,25),
		new Param(18, 3,43),
		new Param(18,19,19),
		new Param(18,25,21),
		new Param(18,41,23),
		new Param(19, 7,36),
		new Param(19, 7,55),
		new Param(19,13,37),
		new Param(19,15,46),
		new Param(19,21,52),
		new Param(19,25,20),
		new Param(19,41,21),
		new Param(19,43,27),
		new Param(20, 1,31),
		new Param(20, 5,29),
		new Param(21, 1,27),
		new Param(21, 9,29),
		new Param(21,13,52),
		new Param(21,15,28),
		new Param(21,15,29),
		new Param(21,17,24),
		new Param(21,17,30),
		new Param(21,17,48),
		new Param(21,21,32),
		new Param(21,21,34),
		new Param(21,21,37),
		new Param(21,21,38),
		new Param(21,21,40),
		new Param(21,21,41),
		new Param(21,21,43),
		new Param(21,41,23),
		new Param(22, 3,39),
		new Param(23, 9,38),
		new Param(23, 9,48),
		new Param(23, 9,57),
		new Param(23,13,38),
		new Param(23,13,58),
		new Param(23,13,61),
		new Param(23,17,25),
		new Param(23,17,54),
		new Param(23,17,56),
		new Param(23,17,62),
		new Param(23,41,34),
		new Param(23,41,51),
		new Param(24, 9,35),
		new Param(24,11,29),
		new Param(24,25,25),
		new Param(24,31,35),
		new Param(25, 7,46),
		new Param(25, 7,49),
		new Param(25, 9,39),
		new Param(25,11,57),
		new Param(25,13,29),
		new Param(25,13,39),
		new Param(25,13,62),
		new Param(25,15,47),
		new Param(25,21,44),
		new Param(25,27,27),
		new Param(25,27,53),
		new Param(25,33,36),
		new Param(25,39,54),
		new Param(28, 9,55),
		new Param(28,11,53),
		new Param(29,27,37),
		new Param(31, 1,51),
		new Param(31,25,37),
		new Param(31,27,35),
		new Param(33,31,43),
		new Param(33,31,55),
		new Param(43,21,46),
		new Param(49,15,61),
		new Param(55, 9,56)
	);

	// ./jrun org.jenetics.random.XOR64ShiftRandomParamTest 2>> XOR64ShiftRandom.results
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
			final XOR64ShiftRandom random = new XOR64ShiftRandom(
				shift,
				param,
				XOR64ShiftRandom.seedBytes()
			);

			final List<Result> results = DieHarder
				.test(random, singletonList("-a"), System.out);

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
