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

import static java.io.File.createTempFile;
import static java.lang.String.format;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.move;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.jaxb.marshal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Represents an function testing measurement environment.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
@XmlJavaTypeAdapter(TrialMeter.Model.Adapter.class)
public final class TrialMeter<T> {

	private final String _name;
	private final String _description;
	private final Env _env;

	private final Params<T> _params;
	private final DataSet _dataSet;

	private TrialMeter(
		final String name,
		final String description,
		final Env env,
		final Params<T> params,
		final DataSet dataSet
	) {
		_name = requireNonNull(name);
		_description = description;
		_env = requireNonNull(env);
		_params = requireNonNull(params);
		_dataSet = requireNonNull(dataSet);
	}

	public String getName() {
		return _name;
	}

	/**
	 * Return the optional description string.
	 *
	 * @return the optional description string
	 */
	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	/**
	 * The trial meter environment information.
	 *
	 * @return the trial meter environment information
	 */
	public Env getEnv() {
		return _env;
	}

	/**
	 * Return the testing parameters.
	 *
	 * @return the testing parameters
	 */
	public Params<T> getParams() {
		return _params;
	}

	/**
	 * Return the current trail {@link DataSet}.
	 *
	 * @return the current trail data set
	 */
	public DataSet getDataSet() {
		return _dataSet;
	}

	/**
	 * Return the test data with the given name
	 *
	 * @param name the data name
	 * @return the test {@link Data} with the given name
	 * @throws NullPointerException if the given {@code name} is {@code null}
	 */
	public Data getData(final String name) {
		return _dataSet.get(name);
	}

	/**
	 * Return the number of test data results.
	 *
	 * @return the number of test data results.
	 */
	public int dataSize() {
		return _dataSet.dataSize();
	}

	/**
	 * Calculates the test values for all parameters. The length of the
	 * resulting {@code double[]} array must be {@link #dataSize()}.
	 *
	 * @param function the test function
	 */
	public void sample(final Function<T, double[]> function) {
		_params.values()
			.subSeq(_dataSet.nextParamIndex())
			.forEach(p -> _dataSet.add(function.apply(p)));
	}

	@Override
	public String toString() {
		return format(
			"TrialMeter[sample=%d, param=%d]",
			dataSize(), _dataSet.nextParamIndex()
		);
	}

	/**
	 * Writes the current {@code TrialMeter} object (the calculated samples +
	 * the parameters) to the given output stream.
	 *
	 * @param out the output stream where to write the trial meter
	 * @throws UncheckedIOException if the marshalling fails
	 */
	public void write(final OutputStream out) {
		try {
			final Marshaller marshaller = jaxb.context().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(marshal(this), out);
		} catch (Exception e) {
			throw new UncheckedIOException(new IOException(e));
		}
	}

	/**
	 * Writes the current {@code TrialMeter} object (the calculated samples +
	 * the parameters) to the given path.
	 *
	 * @param path the output path
	 * @throws UncheckedIOException if the marshalling fails
	 */
	public void write(final Path path) {
		try {
			final File tempFile = createTempFile("__trial_meter__", ".xml");
			try {
				try (OutputStream out = new FileOutputStream(tempFile)) {
					write(out);
				}

				move(tempFile.toPath(), path, REPLACE_EXISTING);
			} finally {
				deleteIfExists(tempFile.toPath());
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Return a new trial measure environment.
	 *
	 * @param name the trial meter name
	 * @param description the trial meter description, maybe {@code null}
	 * @param params the parameters which are tested by this trial meter
	 * @param dataSetNames the names of the calculated data sets
	 * @param <T> the parameter type
	 * @return a new trial measure environment
	 */
	public static <T> TrialMeter<T> of(
		final String name,
		final String description,
		final Params<T> params,
		final String... dataSetNames
	) {
		return new TrialMeter<T>(
			name,
			description,
			Env.of(),
			params,
			DataSet.of(params.size(), dataSetNames)
		);
	}

	/**
	 * Return a new trial measure environment.
	 *
	 * @param name the trial meter name
	 * @param description the trial meter description, maybe {@code null}
	 * @param env the environment information
	 * @param params the parameters which are tested by this trial meter
	 * @param dataSetNames the names of the calculated data sets
	 * @param <T> the parameter type
	 * @return a new trial measure environment
	 */
	public static <T> TrialMeter<T> of(
		final String name,
		final String description,
		final Env env,
		final Params<T> params,
		final String... dataSetNames
	) {
		return new TrialMeter<T>(
			name,
			description,
			env,
			params,
			DataSet.of(params.size(), dataSetNames)
		);
	}

	/**
	 * Read existing {@code TrialMeter} (intermediate) results from the given
	 * input stream.
	 *
	 * @param in the {@link InputStream} to read from
	 * @param <T> the parameter type
	 * @throws UncheckedIOException if reading the {@code TrialMeter} fails
	 * @return the {@code TrialMeter} object read from the input stream
	 */
	@SuppressWarnings("unchecked")
	public static <T> TrialMeter<T> read(final InputStream in) {
		try {
			final Unmarshaller unmarshaller = jaxb.context().createUnmarshaller();
			return (TrialMeter<T>)Model.ADAPTER
				.unmarshal((Model)unmarshaller.unmarshal(in));
		} catch (Exception e) {
			throw new UncheckedIOException(new IOException(e));
		}
	}

	/**
	 * Read existing {@code TrialMeter} (intermediate) results from the given
	 * path.
	 *
	 * @param path the path the {@code TrialMeter} is read
	 * @param <T> the parameter type
	 * @throws UncheckedIOException if reading the {@code TrialMeter} fails
	 * @return the {@code TrialMeter} object read from the input stream
	 */
	public static <T> TrialMeter<T> read(final Path path) {
		try (InputStream in = new FileInputStream(path.toFile())) {
			return read(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "measurement")
	@XmlType(name = "org.jenetics.tool.trial.TrialMeter")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute
		public String name;

		@XmlAttribute
		public String description;

		@XmlElement(name = "environment", required = true, nillable = false)
		public Env env;

		@XmlElement(name = "params", required = true, nillable = false)
		public Params params;

		@XmlElement(name = "data-set", required = true, nillable = false)
		public DataSet dataSet;

		public static final class Adapter
			extends XmlAdapter<Model, TrialMeter>
		{
			@Override
			public Model marshal(final TrialMeter data) {
				final Model model = new Model();
				model.name = data._name;
				model.description = data._description;
				model.env = data._env;
				model.params = data.getParams();
				model.dataSet = data._dataSet;
				return model;
			}

			@Override
			public TrialMeter unmarshal(final Model model) {
				return new TrialMeter(
					model.name,
					model.description,
					model.env,
					model.params,
					model.dataSet
				);
			}
		}

		static final Adapter ADAPTER = new Adapter();
	}

}
