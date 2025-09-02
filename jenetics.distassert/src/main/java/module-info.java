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

/**
 * This module allows performing statistical assertions on sample data.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.3
 */
module io.jenetics.distassert {
	exports io.jenetics.distassert.assertion;
	exports io.jenetics.distassert.distribution;
	exports io.jenetics.distassert.observation;

	requires org.apache.commons.math4.legacy;
	requires org.apache.commons.numbers.core;
	requires org.apache.commons.numbers.gamma;
	requires org.apache.commons.numbers.rootfinder;
	requires org.apache.commons.statistics.descriptive;
	requires org.apache.commons.statistics.distribution;
}
