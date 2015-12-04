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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.testng.Assert;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class Serialize {

	private final IO _io;

	Serialize(final IO io) {
		_io = io;
	}

	public static final Serialize object = new Serialize(IO.object);

	public static final Serialize xml = new Serialize(IO.jaxb);

	public void test(final Object object) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		_io.write(object, out);

		final byte[] data = out.toByteArray();
		final ByteArrayInputStream in = new ByteArrayInputStream(data);
		final Object copy = _io.read(in);

		Assert.assertEquals(copy, object);
	}

}
