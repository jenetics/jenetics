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
package io.jenetics.tool.measurement;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Represents the collected runtime information.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Environment implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _osName;
	private final String _osVersion;
	private final String _osArch;
	private final String _javaVersion;
	private final String _javaRuntimeName;
	private final String _javaRuntimeVersion;
	private final String _javaVMName;
	private final String _javaVMVersion;

	private Environment(
		final String osName,
		final String osVersion,
		final String osArch,
		final String javaVersion,
		final String javaRuntimeName,
		final String javaRuntimeVersion,
		final String javaVMName,
		final String javaVMVersion
	) {
		_osName = requireNonNull(osName);
		_osVersion = requireNonNull(osVersion);
		_osArch = requireNonNull(osArch);
		_javaVersion = requireNonNull(javaVersion);
		_javaRuntimeName = requireNonNull(javaRuntimeName);
		_javaRuntimeVersion = requireNonNull(javaRuntimeVersion);
		_javaVMName = requireNonNull(javaVMName);
		_javaVMVersion = requireNonNull(javaVMVersion);
	}

	/**
	 * The OS architecture.
	 *
	 * @return the OS architecture
	 */
	public String osArch() {
		return _osArch;
	}

	/**
	 * The OS name.
	 *
	 * @return the OS name
	 */
	public String osName() {
		return _osName;
	}

	/**
	 * The OS version.
	 *
	 * @return the OS version
	 */
	public String osVersion() {
		return _osVersion;
	}

	/**
	 * The Java runtime name.
	 *
	 * @return the Java runtime name
	 */
	public String javaRuntimeName() {
		return _javaRuntimeName;
	}

	/**
	 * The Java runtime version.
	 *
	 * @return the Java runtime version
	 */
	public String javaRuntimeVersion() {
		return _javaRuntimeVersion;
	}

	/**
	 * The Java version.
	 *
	 * @return the Java version
	 */
	public String javaVersion() {
		return _javaVersion;
	}

	/**
	 * The Java VM name.
	 *
	 * @return the Java VM name
	 */
	public String javaVMName() {
		return _javaVMName;
	}

	/**
	 * Return the Java VM version.
	 *
	 * @return the Java VM version
	 */
	public String getJavaVMVersion() {
		return _javaVMVersion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			_osName,
			_osVersion,
			_osArch,
			_javaVersion,
			_javaRuntimeName,
			_javaRuntimeVersion,
			_javaVMName,
			_javaVMVersion
		);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Environment &&
			_osName.equals(((Environment)obj)._osName) &&
			_osVersion.equals(((Environment)obj)._osVersion) &&
			_osArch.equals(((Environment)obj)._osArch) &&
			_javaVersion.equals(((Environment)obj)._javaVersion) &&
			_javaRuntimeName.equals(((Environment)obj)._javaRuntimeName) &&
			_javaRuntimeVersion.equals(((Environment)obj)._javaRuntimeVersion) &&
			_javaVMName.equals(((Environment)obj)._javaVMName) &&
			_javaVMVersion.equals(((Environment)obj)._javaVMVersion);
	}

	@Override
	public String toString() {
		return
			"OS name:              " + _osName + "\n" +
			"OS version:           " + _osVersion + "\n" +
			"OS architecture:      " + _osArch + "\n" +
			"Java version:         " + _javaVersion + "\n" +
			"Java runtime name:    " + _javaRuntimeName + "\n" +
			"Java runtime version: " + _javaRuntimeName + "\n" +
			"VM name:              " + _javaVMName + "\n" +
			"VM version:           " + _javaVMVersion;
	}

	/**
	 * Return a new {@code Env} object with the given parameters.
	 *
	 * @param osName the OS name
	 * @param osVersion the OS version
	 * @param osArch the OS architecture
	 * @param javaVersion the Java version
	 * @param javaRuntimeName the Java runtime name
	 * @param javaRuntimeVersion the Java runtime version
	 * @param javaVMName the Java VM name
	 * @param javaVMVersion the Java VM version
	 * @throws  NullPointerException if one of the parameters is {@code null}
	 * @return a new {@code Env} object
	 */
	public static Environment of(
		final String osName,
		final String osVersion,
		final String osArch,
		final String javaVersion,
		final String javaRuntimeName,
		final String javaRuntimeVersion,
		final String javaVMName,
		final String javaVMVersion
	) {
		return new Environment(
			osName,
			osVersion,
			osArch,
			javaVersion,
			javaRuntimeName,
			javaRuntimeVersion,
			javaVMName,
			javaVMVersion
		);
	}

	/**
	 * Return the <i>current</i> environment values.
	 *
	 * @return the <i>current</i> environment values
	 */
	public static Environment of() {
		return Environment.of(
			System.getProperty("os.name"),
			System.getProperty("os.version"),
			System.getProperty("os.arch"),
			System.getProperty("java.version"),
			System.getProperty("java.runtime.name"),
			System.getProperty("java.runtime.version"),
			System.getProperty("java.vm.name"),
			System.getProperty("java.vm.version")
		);
	}
}
