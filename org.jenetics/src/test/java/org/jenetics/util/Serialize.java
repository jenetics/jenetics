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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
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

	public static Serialize xml = new Serialize(IO.xml);

	public static Serialize object = new Serialize(IO.object);

	public void test(final Object object) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		_io.write(object, out);

		final byte[] data = out.toByteArray();
		final ByteArrayInputStream in = new ByteArrayInputStream(data);
		final Object copy = _io.read(Object.class, in);

		Assert.assertEquals(copy, object);
	}

}





