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
package io.jenetics.tool.trial;

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.xml.stream.Writer.elem;
import static io.jenetics.xml.stream.Writer.text;

import java.io.Serializable;

import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;

/**
 * Represents the collected runtime information.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 3.4
 */
public final class Env implements Serializable  {

	private static final long serialVersionUID = 1L;

	private final String _osName;
	private final String _osVersion;
	private final String _osArch;
	private final String _javaVersion;
	private final String _javaRuntimeName;
	private final String _javaRuntimeVersion;
	private final String _javaVMName;
	private final String _javaVMVersion;

	private Env(
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
	public String getOSArch() {
		return _osArch;
	}

	/**
	 * The OS name.
	 *
	 * @return the OS name
	 */
	public String getOSName() {
		return _osName;
	}

	/**
	 * The OS version.
	 *
	 * @return the OS version
	 */
	public String getOSVersion() {
		return _osVersion;
	}

	/**
	 * The Java runtime name.
	 *
	 * @return the Java runtime name
	 */
	public String getJavaRuntimeName() {
		return _javaRuntimeName;
	}

	/**
	 * The Java runtime version.
	 *
	 * @return the Java runtime version
	 */
	public String getJavaRuntimeVersion() {
		return _javaRuntimeVersion;
	}

	/**
	 * The Java version.
	 *
	 * @return the Java version
	 */
	public String getJavaVersion() {
		return _javaVersion;
	}

	/**
	 * The Java VM name.
	 *
	 * @return the Java VM name
	 */
	public String getJavaVMName() {
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
		return
			hash(_osName,
			hash(_osVersion,
			hash(_osArch,
			hash(_javaVersion,
			hash(_javaRuntimeName,
			hash(_javaRuntimeVersion,
			hash(_javaVMName,
			hash(_javaVMVersion))))))));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Env other &&
			_osName.equals(other._osName) &&
			_osVersion.equals(other._osVersion) &&
			_osArch.equals(other._osArch) &&
			_javaVersion.equals(other._javaVersion) &&
			_javaRuntimeName.equals(other._javaRuntimeName) &&
			_javaRuntimeVersion.equals(other._javaRuntimeVersion) &&
			_javaVMName.equals(other._javaVMName) &&
			_javaVMVersion.equals(other._javaVMVersion);
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
	public static Env of(
		final String osName,
		final String osVersion,
		final String osArch,
		final String javaVersion,
		final String javaRuntimeName,
		final String javaRuntimeVersion,
		final String javaVMName,
		final String javaVMVersion
	) {
		return new Env(
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
	public static Env of() {
		return of(
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

	/* *************************************************************************
	 *  XML reader/writer
	 * ************************************************************************/

	public static final Writer<Env> WRITER = elem("environment",
		elem("os-name", text().map(Env::getOSName)),
		elem("os-version", text().map(Env::getOSVersion)),
		elem("os-architecture", text().map(Env::getOSArch)),
		elem("java-version", text().map(Env::getJavaVersion)),
		elem("java-runtime-name", text().map(Env::getJavaRuntimeName)),
		elem("java-runtime-version", text().map(Env::getJavaRuntimeVersion)),
		elem("java-vm-name", text().map(Env::getJavaVMName)),
		elem("java-vm-version", text().map(Env::getJavaVMVersion))
	);

	public static final Reader<Env> READER = Reader.elem(
		(Object[] v) -> Env.of(
			(String)v[0],
			(String)v[1],
			(String)v[2],
			(String)v[3],
			(String)v[4],
			(String)v[5],
			(String)v[6],
			(String)v[7]
		),
		"environment",
		Reader.elem("os-name", Reader.text()),
		Reader.elem("os-version", Reader.text()),
		Reader.elem("os-architecture", Reader.text()),
		Reader.elem("java-version", Reader.text()),
		Reader.elem("java-runtime-name", Reader.text()),
		Reader.elem("java-runtime-version", Reader.text()),
		Reader.elem("java-vm-name", Reader.text()),
		Reader.elem("java-vm-version", Reader.text())
	);

}
