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
 * This object is used to <i>tag</i> a class as <i>object</i> with static
 * methods only. The protected constructor always throws an {@link AssertionError}
 * and prevents the <i>static object</i> from being instantiated.
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
