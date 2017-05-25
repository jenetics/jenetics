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
package org.jenetics.tool.trial;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.6
 * @since 3.6
 */
public class Tuple2<A, B> {

	public final A _1;
	public final B _2;

	private Tuple2(final A p1, final B p2) {
		_1 = p1;
		_2 = p2;
	}

	@Override
	public String toString() {
		return _1 + ":" + _2;
	}

	public static <A, B> Tuple2<A, B> of(final A p1, final B p2) {
		return new Tuple2<>(p1, p2);
	}



}
