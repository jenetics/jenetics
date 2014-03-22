package org.jenetics.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ConcurrentTest {

	@Test
	public void serialScope() {
		final long start = System.currentTimeMillis();

		final long sleep = 100;
		final int loops = 10;
		try (Scoped<Concurrent> e = Concurrent.serial()) {
			for (int i = 0; i < loops; ++i) {
				e.get().execute(new Sleep(String.format("sleep(%d)", i), sleep));
			}
		}

		final long end = System.currentTimeMillis();
		final long duration = end - start;
		final long expected = sleep*loops;
		Assert.assertTrue(
			duration >= expected,
			String.format("%d < %d", duration, expected)
		);
		Assert.assertTrue(
			duration < 1.5*expected,
			String.format("%d >= %d", duration, expected)
		);
		System.out.println("Duration: " + (double)(end - start)/1000.0);
	}

	@Test
	public void scope() {
		final long start = System.currentTimeMillis();

		final long sleep = 100;
		final int loops = 20;
		final int parallelism = 5;
		final ExecutorService executor = Executors.newFixedThreadPool(parallelism);

		try {
			try (Scoped<Concurrent> e = Concurrent.scope(executor)) {
				for (int i = 0; i < loops; ++i) {
					e.get().execute(new Sleep(String.format("sleep(%d)", i), sleep));
				}
			}

			final long end = System.currentTimeMillis();
			final long duration = end - start;
			final long expected = sleep*loops/parallelism;

			Assert.assertTrue(
				duration >= expected,
				String.format("%d < %d", duration, expected)
			);
			Assert.assertTrue(
				duration < 1.5*expected,
				String.format("%d >= %d", duration, expected)
			);
			System.out.println("Duration: " + (double)(end - start)/1000.0);
		} finally {
			executor.shutdownNow();
		}
	}

	private static final class Sleep implements Runnable {
		private final String _name;
		private final long _duration;

		Sleep(final String name, final long duration) {
			_name = name;
			_duration = duration;
		}

		@Override
		public void run() {
			System.out.println("Starting " + _name);
			try {
				Thread.sleep(_duration);
			} catch (InterruptedException e) {
				System.out.println("Sleep interrupted.");
				Thread.currentThread().interrupt();
			}
			System.out.println("    ...finished " + _name);
		}

	}

}
