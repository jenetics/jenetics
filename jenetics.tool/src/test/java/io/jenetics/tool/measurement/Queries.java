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
package io.jenetics.tool.measurement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.jenetics.facilejdbc.Query;

public final class Queries {
	private Queries() {}

	private static final Pattern END_OF_STMT = Pattern.compile(
		"\\s*;\\s*(?=([^']*'[^']*')*[^']*$)"
	);

	public static List<Query> read(final InputStream in)
		throws IOException
	{
		final String script = toSQLText(in);

		return END_OF_STMT.splitAsStream(script)
			.map(Query::of)
			.collect(Collectors.toList());
	}

	private static String toSQLText(final InputStream in) throws IOException {
		try(Reader r = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(r))
		{
			final StringBuilder builder = new StringBuilder();

			String line;
			while ((line = br.readLine()) != null) {
				final String trimmed = line.strip();

				if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
					builder.append(trimmed);
					builder.append("\n");
				}
			}

			return builder.toString();
		}
	}

}
