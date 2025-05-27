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
package io.jenetics.distassert.observation;

import static java.lang.Double.NaN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.testng.annotations.Test;

import io.jenetics.distassert.Interval;
import io.jenetics.distassert.observation.Histogram.Partition;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class HistogramPartitionTest {

	@Test
	public void create() {
		assertThatNoException().isThrownBy(() ->
			new Partition(new Interval(0, 10), 1, 2, 3, 4, 5, 6, 7, 8, 9)
		);
	}

	@Test
	public void outOfIntervalSeparator() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
			new Partition(new Interval(1, 2), 1, 2, 3, 4, 5, 6, 7, 8, 9)
		);
	}

	@Test
	public void notSortedSeparators() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
			new Partition(new Interval(0, 100), 1, 2, 3, 4, 5, 11, 7, 8, 9)
		);
	}

	@Test
	public void notANumberSeparator() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
			new Partition(new Interval(0, 100), 1, 2, 3, 4, 5, NaN, 7, 8, 9)
		);
	}

	@Test
	public void size() {
		assertThat(new Partition(new Interval(0, 10)).size())
			.isEqualTo(1);
		assertThat(new Partition(new Interval(0, 10), 5).size())
			.isEqualTo(2);
		assertThat(new Partition(new Interval(0, 10), 3, 5).size())
			.isEqualTo(3);
		assertThat(new Partition(new Interval(0, 10), 3, 5, 8).size())
			.isEqualTo(4);
	}

	@Test
	public void get() {
		var partition = new Partition(new Interval(0, 10), 1, 2, 3, 4, 5, 6, 7, 8, 9);
		assertThat(partition.size()).isEqualTo(10);

		for (int i = 0; i < partition.size(); ++i) {
			assertThat(partition.get(i)).isEqualTo(new Interval(i, i + 1));
		}
	}

	@Test
	public void iterator() {
		var partition = new Partition(new Interval(0, 10), 1, 2, 3, 4, 5, 6, 7, 8, 9);
		assertThat(partition).hasSize(10);

		int i = 0;
		for (var interval : partition) {
			assertThat(interval).isEqualTo(new Interval(i, i + 1));
			++i;
		}
	}

	@Test
	public void stream() {
		var partition = new Partition(new Interval(0, 10), 1, 2, 3, 4, 5, 6, 7, 8, 9);
		var intervals = partition.stream().toList();
		assertThat(intervals).hasSize(10);
	}

	@Test
	public void indexOf() {
		var partition = new Partition(new Interval(0, 10), 1, 2, 3, 4, 5, 6, 7, 8, 9);
		assertThat(partition.indexOf(-1)).isEqualTo(-1);
		assertThat(partition.indexOf(10)).isEqualTo(partition.size());

		for (int i = 0; i < partition.size(); ++i) {
			assertThat(partition.indexOf(i)).isEqualTo(i);
		}
	}

	@Test
	public void indexOfOneInterval() {
		var partition = new Partition(new Interval(0, 10));
		assertThat(partition.indexOf(-1)).isEqualTo(-1);
		assertThat(partition.indexOf(10)).isEqualTo(partition.size());
		assertThat(partition.indexOf(5)).isEqualTo(0);
	}

	@Test
	public void indexOfTwoInterval() {
		var partition = new Partition(new Interval(0, 10), 5);
		assertThat(partition.indexOf(-1)).isEqualTo(-1);
		assertThat(partition.indexOf(10)).isEqualTo(partition.size());
		assertThat(partition.indexOf(4)).isEqualTo(0);
		assertThat(partition.indexOf(6)).isEqualTo(1);
	}

	@Test
	public void ofEqualIntervals() {
		var partition = Partition.of(new Interval(0, 10), 10);
		assertThat(partition).hasSize(10);

		for (var interval : partition) {
			assertThat(interval.max() - interval.min())
				.isEqualTo(1);
		}
	}

}
