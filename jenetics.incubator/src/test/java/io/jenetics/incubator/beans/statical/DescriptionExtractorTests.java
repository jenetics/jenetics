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
package io.jenetics.incubator.beans.statical;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class DescriptionExtractorTests {

	public static final class Box {
		List<Integer> list;

		public String[] getStrings() {
			return strings;
		}

		public void setStrings(String[] strings) {
			this.strings = strings;
		}

		String[] strings;

		public Box(List<Integer> list) {
			this.list = list;
		}

		public List<Integer> getList() {
			return list;
		}

		public void setList(List<Integer> list) {
			this.list = list;
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

		final List<Integer> list = new ArrayList<Integer>() {};
		list.add(23);

		final var desc = Descriptions.walk(Box.class)
			/*
			.flatMap(d ->
					Stream.concat(
						Stream.of(d),
						DescriptionExtractor.extract(d.type())
					)
				)

			 */
			.toList();


		desc.forEach(System.out::println);
	}

}
