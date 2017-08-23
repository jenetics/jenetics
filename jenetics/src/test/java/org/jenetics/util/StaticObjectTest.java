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

import java.lang.reflect.InvocationTargetException;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class StaticObjectTest {

	@Test
	public void constructorInstantiation() throws Exception {
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
	}

	@Test
	public void reflectiveInstantiation() throws Exception {
		try {
			SomeStaticObject.class.getDeclaredConstructor().newInstance();
			Assert.assertFalse(true);
		} catch (InvocationTargetException e) {
			final String expected = String.format(
				"Instantiation of '%s' is not allowed.",
				"org.jenetics.util.SomeStaticObject"
			);

			Assert.assertEquals(e.getTargetException().getMessage(), expected);
		}
	}

}

final class SomeStaticObject {
	SomeStaticObject() {require.noInstance();}
}
