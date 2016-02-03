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
package org.jenetics.tool.trial;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Hash;

/**
 * Represents the collected runtime information.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
@XmlJavaTypeAdapter(Env.Model.Adapter.class)
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
		return Hash.of(getClass())
			.and(_osName)
			.and(_osVersion)
			.and(_osArch)
			.and(_javaVersion)
			.and(_javaRuntimeName)
			.and(_javaRuntimeVersion)
			.and(_javaVMName)
			.and(_javaVMVersion).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Env &&
			_osName.equals(((Env)obj)._osName) &&
			_osVersion.equals(((Env)obj)._osVersion) &&
			_osArch.equals(((Env)obj)._osArch) &&
			_javaVersion.equals(((Env)obj)._javaVersion) &&
			_javaRuntimeName.equals(((Env)obj)._javaRuntimeName) &&
			_javaRuntimeVersion.equals(((Env)obj)._javaRuntimeVersion) &&
			_javaVMName.equals(((Env)obj)._javaVMName) &&
			_javaVMVersion.equals(((Env)obj)._javaVMVersion);
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
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "environment")
	@XmlType(name = "org.jenetics.tool.trial.Env")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlElement(name = "os-name")
		public String osName;

		@XmlElement(name = "os-version")
		public String osVersion;

		@XmlElement(name = "os-architecture")
		public String osArch;

		@XmlElement(name = "java-version")
		public String javaVersion;

		@XmlElement(name = "java-runtime-name")
		public String javaRuntimeName;

		@XmlElement(name = "java-runtime-version")
		public String javaRuntimeVersion;

		@XmlElement(name = "java-vm-name")
		public String javaVMName;

		@XmlElement(name = "java-vm-version")
		public String javaVMVersion;

		public static final class Adapter extends XmlAdapter<Model, Env> {
			@Override
			public Model marshal(final Env env) {
				final Model model = new Model();
				model.osName = env.getOSName();
				model.osVersion = env.getOSVersion();
				model.osArch = env.getOSArch();
				model.javaVersion = env.getJavaVersion();
				model.javaRuntimeName = env.getJavaRuntimeName();
				model.javaRuntimeVersion = env.getJavaRuntimeVersion();
				model.javaVMName = env.getJavaVMName();
				model.javaVMVersion = env.getJavaVMVersion();
				return model;
			}

			@Override
			public Env unmarshal(final Model model) {
				return Env.of(
					model.osName,
					model.osVersion,
					model.osArch,
					model.javaVersion,
					model.javaRuntimeName,
					model.javaRuntimeVersion,
					model.javaVMName,
					model.javaVMVersion
				);
			}
		}
	}

}
