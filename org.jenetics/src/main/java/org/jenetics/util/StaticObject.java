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

import static java.lang.String.format;

/**
 * This class is used to <i>tag</i> a class as <i>object</i> with static
 * methods only. Such classes are not supposed to be treated as <i>types</i>.
 * The protected constructor always throws an {@link AssertionError} and
 * prevents the <i>static object</i> from being instantiated.
 *
 * The following <i>pattern</i> is used for creating such static helper
 * classes&mdash;the only constructors is declared private to gain the wished
 * compile-time safety:
 * [code]
 * public final class utils extends StaticObject {
 *     private utils() {}
 *
 *     ...
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date: 2013-06-14 $</em>
 */
public abstract class StaticObject {

	/**
	 * Calling the constructor of an {@code StaticObject} will always throw an
	 * {@link AssertionError}.
	 *
	 * @throws AssertionError always.
	 */
	protected StaticObject() {
		String message = "Object instantiation is not allowed";

		final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if (trace.length >= 3) {
			message = format(
				"Instantiation of '%s' is not allowed.",
				trace[2].getClassName()
			);
		}

		throw new AssertionError(message);
	}

}
