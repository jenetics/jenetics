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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.gradle

import java.util.Objects
import java.util.regex.Pattern

/**
 * Represent a library version.
 *
 * @author [Franz Wilhelmstötter](mailto:franz.wilhelmstoetter@gmail.com)
 * @since 1.4
 * @version 6.1
 */
data class Version(val major: Int, val minor: Int, val micro: Int, val snapshot: Boolean)
	: Comparable<Version>
{

	init {
		// Check the version numbers.
		require(!(major < 0 || minor < 0 || micro < 0)) {
			String.format(
				"Invalid range of the version numbers (%d, %d, %d)",
				major, minor, micro
			)
		}
	}

	override operator fun compareTo(other: Version): Int {
		var comp = 0
		if (major > other.major) {
			comp = 1
		} else if (major < other.major) {
			comp = -1
		}
		if (comp == 0) {
			if (minor > other.minor) {
				comp = 1
			} else if (minor < other.minor) {
				comp = -1
			}
		}
		if (comp == 0) {
			if (micro > other.micro) {
				comp = 1
			} else if (micro < other.micro) {
				comp = -1
			}
		}
		return comp
	}

	fun minorVersionString(): String {
		return String.format("%d.%d", major, minor)
	}

	override fun toString(): String {
		return String.format("%d.%d.%d", major, minor, micro) +
			if (snapshot) "-SNAPSHOT" else ""
	}

	companion object {
		fun parse(versionString: String): Version {
			val parts = versionString
				.split(Pattern.quote(".").toRegex())
				.dropLastWhile { it.isEmpty() }
				.toTypedArray()

			val major: Int
			val minor: Int
			val micro: Int
			val snapshot: Boolean

			try {
				if (parts.size == 3) {
					major = parts[0].toInt()
					minor = parts[1].toInt()
					val last = parts[2]
						.split("-".toRegex())
						.dropLastWhile { it.isEmpty() }
						.toTypedArray()

					if (last.size == 2) {
						micro = last[0].toInt()
						snapshot = "SNAPSHOT" == last[1]
					} else {
						micro = parts[2].toInt()
						snapshot = false
					}
				} else {
					throw IllegalArgumentException(
						String.format(
							"'%s' is not a valid version string.", versionString
						)
					)
				}
			} catch (e: NumberFormatException) {
				throw IllegalArgumentException(
					String.format(
						"'%s' is not a valid version string.", versionString
					)
				)
			}
			return Version(major, minor, micro, snapshot)
		}

		fun parse(version: Any): Version {
			return parse(version.toString())
		}

	}
}
