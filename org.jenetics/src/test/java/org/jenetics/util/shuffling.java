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
package org.jenetics.util;

import java.util.Random;

import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public final class shuffling {
	private shuffling() {require.noInstance();}

	public static <T> T[] shuffle(final T[] array) {
		return shuffle(array, RandomRegistry.getRandom());
	}

	public static <T> T[] shuffle(final T[] array, final Random random) {
		for (int j = array.length - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}
		return array;
	}

	public static <T> MSeq<T> shuffle(final MSeq<T> array) {
		return shuffle(array, RandomRegistry.getRandom());
	}

	public static <T> MSeq<T> shuffle(final MSeq<T> array, final Random random) {
		for (int j = array.length() - 1; j > 0; --j) {
			array.swap(j, random.nextInt(j + 1));
		}

		return array;
	}

	public static <T> void swap(final T[] array, final int i, final int j) {
		final T old = array[i];
		array[i] = array[j];
		array[j] = old;
	}

}
