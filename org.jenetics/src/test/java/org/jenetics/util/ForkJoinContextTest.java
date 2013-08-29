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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class ForkJoinContextTest {

	static {
		ForkJoinContext.setForkJoinPool(new ForkJoinPool(3));
	}

	@SuppressWarnings("unused")
	private static class Task implements Runnable {
		private int _id;

		public Task(final int id) {
			_id = id;
		}

		@Override
		public void run() {
			try (Concurrency c = Concurrency.start()) {
				for (int i = 0; i < 10; ++i) {

					final int id = i;
					c.execute(new Runnable() { @Override public void run() {
						try {
							//System.out.println(String.format("%d - %d", _id, id));
							Thread.sleep(5);
						} catch (Exception e) {
							assert(false) : e.getMessage();
						}
					}});
				}
			}
		}
	}

	@Test
	public void potentialDeadLock() {
		try (Concurrency c = Concurrency.start()) {
			for (int i = 0; i < 10; ++i) {
				c.execute(new Task(i));
			}
		}
	}

	@Test(dataProvider = "levels")
	public void execute(final Integer level) {
		try {
			Concurrency.setContext(ForkJoinContext.class);
			Assert.assertEquals(_execute(level), level + 1);
		} finally {
			Concurrency.setDefaultContext();
		}
	}

	private long _execute(final Integer level) {
		final AtomicLong counter = new AtomicLong(1);

		try (Concurrency c = Concurrency.start()) {
			c.execute(new Runnable() {
				@Override public void run() {
					if (level == 0) {
						//System.out.println("READY");
					} else {
						counter.addAndGet(_execute(level - 1));
					}
				}
			});
			c.execute(new Runnable() {
				@Override
				public void run() {
					try {
						exec();
						Thread.sleep(2);
					} catch (InterruptedException e) {
						assert(false) : e.toString();
					}
				}
			});
		}

		return counter.longValue();
	}

	private static void exec() {
		try (Concurrency c = Concurrency.start()) {
			for (int i = 0; i < 5; ++i) {
				c.execute(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							assert(false) : e.toString();
						}
					}
				});
				}
		}
	}

	@DataProvider(name = "levels")
	public Object[][] levels() {
		return new Object[][] {
			{ 1 }, { 2 }, { 5 }, { 7 }, { 10 }, { 15 }, { 21 }, { 50 }, { 100 }
		};
	}


}




