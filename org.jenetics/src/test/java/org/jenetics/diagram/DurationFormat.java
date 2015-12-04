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
package org.jenetics.diagram;

import java.text.NumberFormat;
import java.time.Duration;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
final class DurationFormat {

	static String format(final Duration duration) {
		final long hours = duration.toHours();
		final long minutes = duration.toMinutes() - hours*60;
		final long seconds = duration.getSeconds() - hours*3600 - minutes*60;
		final long nanos = duration.getNano();

		final NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumIntegerDigits(0);
		nf.setMaximumIntegerDigits(0);
		nf.setMinimumFractionDigits(6);
		nf.setMaximumFractionDigits(6);

		return String.format(
			"%2d:%2d:%2d%s",
			hours, minutes, seconds, nf.format(nanos/1_000_000_000.0)
		).replace(' ', '0');
	}

}
