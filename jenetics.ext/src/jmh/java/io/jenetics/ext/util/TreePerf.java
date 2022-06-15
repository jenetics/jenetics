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
package io.jenetics.ext.util;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class TreePerf {

	@State(Scope.Benchmark)
	public static class Trees {
		Tree<Integer, ?> tree = newTree(4, new Random(123));
		Tree<Integer, ?> flatTree = FlatTreeNode.ofTree(tree);
	}

	private static TreeNode<Integer> newTree(final int levels, final RandomGenerator random) {
		final TreeNode<Integer> root = TreeNode.of(0);
		fill(root, levels, random);
		return root;
	}

	public static void main(String[] args) {
		System.out.println(new Trees().flatTree.size());
	}

	public static void fill(
		final TreeNode<Integer> node,
		final int level,
		final RandomGenerator random
	) {
		for (int i = 0, n = random.nextInt(3) + 1; i < n; ++i) {
			final TreeNode<Integer> child = TreeNode.of();
			child.value(random.nextInt());

			if (level > 0) {
				fill(child, level - 1, random);
			}

			node.attach(child);
		}
	}

	@Benchmark
	public int size(final Trees trees) {
		return trees.tree.size();
	}

	@Benchmark
	public int flatSize(final Trees trees) {
		return trees.flatTree.size();
	}

	@Benchmark
	public long count(final Trees trees) {
		return trees.tree.breadthFirstStream().count();
	}

	@Benchmark
	public long flatCount(final Trees trees) {
		return trees.flatTree.breadthFirstStream().count();
	}

	@Benchmark
	public int reduce(final Trees trees) {
		return trees.tree.reduce(new Integer[]{0}, TreePerf::sum);
	}

	@Benchmark
	public int flatReduce(final Trees trees) {
		return trees.flatTree.reduce(new Integer[]{0}, TreePerf::sum);
	}

	static int sum(final Integer zero, final Integer[] values) {
		int value = zero;
		for (var i : values) {
			value += i;
		}
		return value;
	}


	/* 7.1
Benchmark            Mode  Cnt     Score    Error  Units
TreePerf.count       avgt   15  1398.861 ± 24.482  ns/op
TreePerf.flatCount   avgt   15    27.249 ±  0.454  ns/op
TreePerf.flatReduce  avgt   15  1174.845 ± 40.802  ns/op
TreePerf.flatSize    avgt   15     3.361 ±  0.093  ns/op
TreePerf.reduce      avgt   15  1237.930 ± 18.379  ns/op
TreePerf.size        avgt   15   424.451 ±  5.099  ns/op
	 */

	/*
Benchmark           Mode  Cnt     Score    Error  Units
TreePerf.count      avgt    4  1914.299 ± 38.673  ns/op
TreePerf.flatCount  avgt    4  2227.737 ± 73.127  ns/op
TreePerf.flatSize   avgt    4   347.181 ± 21.484  ns/op
TreePerf.size       avgt    4   405.097 ±  4.489  ns/op
	 */

}
