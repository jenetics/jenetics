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
package org.jenetix.util;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__! &mdash; <em>$Date: 2014-07-14 $</em>
 * @since !__version__!
 */
public class Main {

	public static void main(final String[] args) throws Exception {
		final String file = "/home/fwilhelm/Downloads/word/all.txt";

		try (FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr))
		{
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (isValid(line)) {
					System.out.println(line);
				}
			}
		}

	}

	private static boolean isValid(final String word) {
		boolean valid = true;
		for (int i = 0; i < word.length() && valid; ++i) {
			final char c = Character.toLowerCase(word.charAt(i));
			valid = c == 'a' ||
				c == 'b' ||
				c == 'c' ||
				c == 'd' ||
				c == 'e' ||
				c == 'f';
		}

		return valid;
	}

}
