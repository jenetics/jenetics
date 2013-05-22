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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public final class DieHarder {

	private static final class Randomizer implements Runnable {
		private final Random _random;
		private final DataOutputStream _out;

		public Randomizer(final Random random, final DataOutputStream out) {
			_random = random;
			_out = out;
		}

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					for (int i = 0; i < 1000; ++i) {
						_out.writeInt(_random.nextInt());
					}
				}
			} catch (IOException ignore) {
			}
		}

	}

	public static void main(final String[] args) throws Exception {
		if ( args.length < 1) {
			System.out.println("Usage: java org.jenetics.util.DieHarder <random-class-name>");
			return;
		}

		final String randomName = args[0];
		Random random = null;
		try {
			random = (Random)Class.forName(randomName).newInstance();
			printt("Testing: %s", randomName);
		} catch (Exception e) {
			System.out.println("Can't create random class " + randomName);
			return;
		}

		final List<String> dieharderArgs = new ArrayList<>();
		dieharderArgs.add("dieharder");
		for (int i  = 1; i < args.length; ++i) {
			dieharderArgs.add(args[i]);
		}
		dieharderArgs.add("-g");
		dieharderArgs.add("200");

		final long start = System.currentTimeMillis();
		final ProcessBuilder builder = new ProcessBuilder(dieharderArgs);
		//final ProcessBuilder builder = new ProcessBuilder("dieharder", "-a", "-g", "200");
		//final ProcessBuilder builder = new ProcessBuilder("dieharder", "-d", "1", "-g", "200");
		final Process dieharder = builder.start();

		final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
			dieharder.getOutputStream()
		));
		final Thread randomizer = new Thread(new Randomizer(random, out));
		randomizer.start();

		final BufferedReader stdout = new BufferedReader (
			new InputStreamReader(dieharder.getInputStream())
		);
		String line = null;
		while ((line = stdout.readLine()) != null) {
			System.out.println(line);
		}

		dieharder.waitFor();
		randomizer.interrupt();
		final long sec = (System.currentTimeMillis() - start)/1000;

		printt("Runtime: %d:%02d:%02d", sec/3600, (sec%3600)/60, (sec%60));

	}

	private static void printt(final String title, final Object... args) {
		System.out.println("#=============================================================================#");
		System.out.println(String.format(
			"# %-76s#", String.format(title, args)
		));
		System.out.println("#=============================================================================#");
	}

}





