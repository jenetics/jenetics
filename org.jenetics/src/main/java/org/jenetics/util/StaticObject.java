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

/**
 * This method is used for preventing class with static methods only from
 * being instantiated. Use the following <i>pattern</i> when creating such
 * helper classes:
 * [code]
 * public final class utils {
 *     private utils() { object.nonInstanceable(); }
 *
 *     // Here comes the static helper methods.
 *     ...
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
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
			message = String.format(
				"Instantiation of '%s' is not allowed.",
				trace[2].getClassName()
			);
		}

		throw new AssertionError(message);
	}

}
