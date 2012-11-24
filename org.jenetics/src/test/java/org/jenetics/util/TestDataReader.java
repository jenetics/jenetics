/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class TestDataReader implements Closeable {

	private final BufferedReader _reader;

	public TestDataReader(final InputStream in) {
		_reader = new BufferedReader(new InputStreamReader(in));
	}

	public TestDataReader(final String resource) {
		this(TestDataReader.class.getResourceAsStream(resource));
	}

	public <R> void foreach(final Function<String[], R> f) {
		String[] data = null;
		try {
			while ((data = read()) != null) {
				f.apply(data);
			}
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public String[] read() throws IOException {
		String line = null;
		while ((line = _reader.readLine()) != null &&
				(line.trim().startsWith("#") ||
				line.trim().isEmpty()))
		{
		}

		return line != null ? line.split(",") : null;
	}

	@Override
	public void close() throws IOException {
		if (_reader != null) {
			_reader.close();
		}
	}

}
