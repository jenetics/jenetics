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

/**
 * TODO: Fix tests
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class XOR32ShiftRandomCompatibilityTest {
	private final static String TEST_RESOURCE =
		"/org/jenetics/util/XOR32ShiftRandom.dat/%s-%s-%s-%s";


//	private static TestData testData(
//		final Integer seed,
//		final Integer a,
//		final Integer b,
//		final Integer c
//	) {
//		final String resource = String.format(
//			TEST_RESOURCE, seed, a, b, c
//		);
//		return new TestData(resource);
//	}
//
//	@DataProvider(name = "data")
//	public Object[][] data() {
//		return TestData.list("/org/jenetics/util/LCG64ShiftRandom")
//			.map(data -> new Object[]{data})
//			.toArray(Object[][]::new);
//	}
//
//	@Test(dataProvider = "parameters")
//	public void random(
//		final Integer seed,
//		final Integer a,
//		final Integer b,
//		final Integer c
//	) {
//		final Random random = new XOR32ShiftRandom(seed, new Param(a, b, c));
//
//		for (final String[] value : testData(seed, a, b, c)) {
//			final int expected = Integer.parseInt(value[0]);
//			Assert.assertEquals(random.nextInt(), expected);
//		}
//	}
//
//	@Test(dataProvider = "parameters")
//	public void threadSafeRandom(
//		final Integer seed,
//		final Integer a,
//		final Integer b,
//		final Integer c
//	) {
//		final Random random = new XOR32ShiftRandom.ThreadSafe(seed, new Param(a, b, c));
//
//		for (final String[] value : testData(seed, a, b, c)) {
//			final int expected = Integer.parseInt(value[0]);
//			Assert.assertEquals(random.nextInt(), expected);
//		}
//	}
//
//	@DataProvider(name = "parameters")
//	public Object[][] parameters() {
//		return new Object[][]{
//			{0, 12, 21, 5},
//			{0, 25, 9, 10},
//			{0, 13, 17, 11},
//			{0, 13, 6, 5},
//			{0, 21, 5, 3},
//			{0, 7, 13, 25},
//			{0, 23, 8, 7},
//			{234, 12, 21, 5},
//			{234, 25, 9, 10},
//			{234, 13, 17, 11},
//			{234, 13, 6, 5},
//			{234, 21, 5, 3},
//			{234, 7, 13, 25},
//			{234, 23, 8, 7},
//			{1245281, 12, 21, 5},
//			{1245281, 25, 9, 10},
//			{1245281, 13, 17, 11},
//			{1245281, 13, 6, 5},
//			{1245281, 21, 5, 3},
//			{1245281, 7, 13, 25},
//			{1245281, 23, 8, 7},
//			{2345249, 12, 21, 5},
//			{2345249, 25, 9, 10},
//			{2345249, 13, 17, 11},
//			{2345249, 13, 6, 5},
//			{2345249, 21, 5, 3},
//			{2345249, 7, 13, 25},
//			{2345249, 23, 8, 7}
//		};
//	}
//
//	public static void main(final String[] arg) throws Exception {
//		final XOR32ShiftRandomCompatibilityTest test =
//			new XOR32ShiftRandomCompatibilityTest();
//
//		for (Object[] params : test.parameters()) {
//			final long seed = (Integer)params[0];
//			final Param param = new Param(
//				(Integer)params[1], (Integer)params[2], (Integer)params[3]
//			);
//			final Random random = new XOR32ShiftRandom(seed, param);
//
//			final File file = new File(
//				"/home/fwilhelm/Workspace/Development/Projects/Jenetics/org.jenetics/src/test/resources",
//				String.format(
//					XOR32ShiftRandomCompatibilityTest.TEST_RESOURCE,
//					params[0], params[1], params[2], params[3]
//				)
//			);
//			try (FileWriter writer = new FileWriter(file)) {
//				for (int i = 0; i < 1000; ++i) {
//					writer.write(Integer.toString(random.nextInt()));
//					writer.write("\n");
//				}
//			}
//		}
//	}
}
