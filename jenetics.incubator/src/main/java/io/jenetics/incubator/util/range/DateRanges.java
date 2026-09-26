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
package io.jenetics.incubator.util.range;

import java.time.LocalDate;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public final class DateRanges {
	private DateRanges() {
	}

	/**
	 * Creates a new continuous date range with the given {@code start} and
	 * {@code end} date.
	 *
	 * @param start the start date (inclusively)
	 * @param end the end date (exclusively)
	 * @return  a new continuous date range
	 */
	public static DenseRange<LocalDate> dense(LocalDate start, LocalDate end) {
		return Range.dense(Integral.LOCAL_DATE, start, end);
	}

	/**
	 * Return a new date range consisting of the given {@code date}.
	 *
	 * @param date the element the created date range consists of
	 * @return a new date range consisting of the given {@code date}
	 */
	static DenseRange<LocalDate> of(LocalDate date) {
		return Range.of(Integral.LOCAL_DATE, date);
	}

	/**
	 * Return a new date range consisting of the given {@code dates}.
	 *
	 * @param dates the elements the created date range consists of
	 * @return a new date range consisting of the given {@code dates}
	 */
	static Range<LocalDate> of(LocalDate... dates) {
		return Range.of(Integral.LOCAL_DATE, dates);
	}

	/**
	 * Create a new date range consisting of the given subranges.
	 *
	 * @param ranges the subranges of the created date range
	 * @return a new date range consisting of the given subranges
	 */
	@SafeVarargs
	static Range<LocalDate> of(Range<LocalDate>... ranges) {
		return Range.of(Integral.LOCAL_DATE, ranges);
	}

}
