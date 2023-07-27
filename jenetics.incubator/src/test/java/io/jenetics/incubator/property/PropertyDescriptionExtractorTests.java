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
package io.jenetics.incubator.property;

import org.testng.annotations.Test;

import io.jenetics.jpx.GPX;

import java.util.List;

public class PropertyDescriptionExtractorTests {

	public static final class Box {
		List<Integer> root;

		public Box(List<Integer> root) {
			this.root = root;
		}

		public Object root() {
			return root;
		}

		public void setRoot(List<Integer> root) {
			this.root = root;
		}
	}

	static class Data {
		int _1;
		int _2;
		int _3;

		int[] _x;
	}

	@Test
	public void extractIntArray() {
		final var data = new int[0];

		final var desc = PropertyDescriptionExtractor.extract(data.getClass())
			.toList();

		System.out.println(desc);
	}

}
