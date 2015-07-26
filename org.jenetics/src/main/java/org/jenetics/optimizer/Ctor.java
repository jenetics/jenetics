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
package org.jenetics.optimizer;

import java.util.function.DoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Ctor<T> {

	public Class<T> type();

	public ISeq<DoubleFunction<Object>> parameters();


	public default T cons(final double... args) {
		final Object[] parameters = IntStream.range(0, args.length)
			.mapToObj(i -> parameters().get(i).apply(args[i]))
			.toArray();

		final Class<?>[] parameterTypes = Stream.of(parameters)
			.map(Object::getClass)
			.toArray(Class[]::new);

		try {
			return type()
				.getConstructor(parameterTypes)
				.newInstance(parameters);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

}
