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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 */
@SuppressWarnings("module")
module io.jenetics.base {
	exports io.jenetics;
	exports io.jenetics.engine;
	exports io.jenetics.stat;
	exports io.jenetics.util;

	exports io.jenetics.internal.engine to io.jenetics.ext;
	exports io.jenetics.internal.math to io.jenetics.ext;
	exports io.jenetics.internal.util to io.jenetics.ext, io.jenetics.prog;
}
