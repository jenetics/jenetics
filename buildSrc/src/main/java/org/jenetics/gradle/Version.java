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
package org.jenetics.gradle;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.quote;

/**
 * Represent a library version.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 2.0
 */
public final class Version implements Comparable<Version> {

	private final int _major;
	private final int _minor;
	private final int _micro;
	private final boolean _snapshot;

	public Version(
		final int major,
		final int minor,
		final int micro,
		final boolean snapshot
	) {
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
		_snapshot = snapshot;
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

	public boolean isSnapshot() {
		return _snapshot;
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

	public String majorVersionString() {
		return Integer.toString(_major);
	}

	public String minorVersionString() {
		return format("%d.%d", _major, _minor);
	}

	public String microVersionString() {
		return format("%d.%d.%d", _major, _minor, _micro);
	}

	@Override
	public int hashCode() {
		int hash = getClass().hashCode();
		hash = 31*hash + _major;
		hash = 31*hash + _minor;
		hash = 31*hash + _micro;
		hash = 31*hash + Boolean.valueOf(_snapshot).hashCode();
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
				_micro == version._micro &&
				_snapshot == version._snapshot;
	}

	@Override
	public String toString() {
		return format("%d.%d.%d", _major, _minor, _micro) +
				(_snapshot ? "-SNAPSHOT" : "");
	}

	public static Version parse(final String versionString) {
		requireNonNull(versionString, "Version string must not be null.");
		final String[] parts = versionString.split(quote("."));

		int major = 1;
		int minor = 0;
		int micro = 0;
		boolean snapshot = false;

		try {
			if (parts.length == 3) {
				major = Integer.parseInt(parts[0]);
				minor = Integer.parseInt(parts[1]);
				final String[] last = parts[2].split("-");
				if (last.length == 2) {
					micro = Integer.parseInt(last[0]);
					snapshot = "SNAPSHOT".equals(last[1]);
				} else {
					micro = Integer.parseInt(parts[2]);
				}
			} else {
				throw new IllegalArgumentException(format(
					"'%s' is not a valid version string.", versionString
				));
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(format(
				"'%s' is not a valid version string.", versionString
			));
		}

		return new Version(major, minor, micro, snapshot);
	}

}
