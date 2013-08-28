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
package org.jenetics.gradle;

import static java.lang.String.format;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
public final class Version implements Comparable<Version> {

	private final int _major;
	private final int _minor;
	private final int _micro;

	public Version(final int major, final int minor, final int micro) {
		// Check the version numbers.
		if ( major < 0 || minor < 0 || micro < 0 ) {
			throw new IllegalArgumentException(format(
				"Invalid range of the version numbers (%d, %d, %d)",
				major, minor, micro
			));
		}

		_major = major;
		_minor = minor;
		_micro = micro;
	}

	public Version(final int major, final int minor) {
		this(major, minor, 0);
	}

	public Version(final int major) {
		this(major, 0, 0);
	}

	public int getMajor() {
		return _major;
	}

	public int getMinor() {
		return _minor;
	}

	public int getMicro() {
		return _micro;
	}

	@Override
	public int compareTo(final Version version) {
		int comp = 0;

		if (_major > version._major) {
			comp = 1;
		} else if (_major < version._major) {
			comp = -1;
		}
		if (comp == 0) {
			if (_minor > version._minor) {
				comp = 1;
			} else if (_minor < version._minor) {
				comp = -1;
			}
		}
		if (comp == 0) {
			if (_micro > version._micro) {
				comp = 1;
			} else if (_micro < version._micro) {
				comp = -1;
			}
		}

		return comp;
	}

	@Override
	public int hashCode() {
		int hash = getClass().hashCode();
		hash = 31*hash + _major;
		hash = 31*hash + _minor;
		hash = 31*hash + _micro;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Version)) {
			return false;
		}

		final Version version = (Version)obj;
		return _major == version._major &&
				_minor == version._minor &&
				_micro == version._micro;
	}

	@Override
	public String toString() {
		return format("%d.%d.%d", _major, _minor, _micro);
	}

}
















