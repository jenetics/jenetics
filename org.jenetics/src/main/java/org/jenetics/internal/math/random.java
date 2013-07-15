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
package org.jenetics.internal.math;

import org.jenetics.util.StaticObject;

/**
 * Some random helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
public final class random extends StaticObject {
	private random() {}

	/*
	 * Conversion methods used by the 'Random' engine from the JDK.
	 */

	public static float toFloat(final int a) {
		return (a >>> 8)/((float)(1 << 24));
	}

	public static float toFloat(final long a) {
		return (int)(a >>> 40)/((float)(1 << 24));
	}

	public static double toDouble(final long a) {
		return (((a >>> 38) << 27) + (((int)a) >>> 5))/(double)(1L << 53);
	}

	public static double toDouble(final int a, final int b) {
		return (((long)(a >>> 6) << 27) + (b >>> 5))/(double)(1L << 53);
	}

	/*
	 * Conversion methods used by the Apache Commons BitStreamGenerator.
	 */

	public static float toFloat2(final int a) {
		return (a >>> 9)*0x1.0p-23f;
	}

	public static float toFloat2(final long a) {
		return (int)(a >>> 41)*0x1.0p-23f;
	}

	public static double toDouble2(final long a) {
		return (a & 0xFFFFFFFFFFFFFL)*0x1.0p-52d;
	}

	public static double toDouble2(final int a, final int b) {
		return (((long)(a >>> 6) << 26) | (b >>> 6))*0x1.0p-52d;
	}
}


