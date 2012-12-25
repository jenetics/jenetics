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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-12-14 $</em>
 */
public class StaticObjectTest {

	@Test
	public void instantiation()
		throws InstantiationException, IllegalAccessException
	{
		try {
			new StaticObject() {};
			Assert.assertFalse(true);
		} catch (AssertionError e) {
			final String expected = String.format(
				"Instantiation of '%s' is not allowed.",
				"org.jenetics.util.StaticObjectTest$1"
			);

			Assert.assertEquals(e.getMessage(), expected);
		}

		try {
			new SomeStaticObject();
			Assert.assertFalse(true);
		} catch (AssertionError e) {
			final String expected = String.format(
				"Instantiation of '%s' is not allowed.",
				"org.jenetics.util.SomeStaticObject"
			);

			Assert.assertEquals(e.getMessage(), expected);
		}

		try {
			SomeStaticObject.class.newInstance();
			Assert.assertFalse(true);
		} catch (AssertionError e) {
			final String expected = String.format(
				"Instantiation of '%s' is not allowed.",
				"org.jenetics.util.SomeStaticObject"
			);

			Assert.assertEquals(e.getMessage(), expected);
		}
	}

}

final class SomeStaticObject extends StaticObject {}






