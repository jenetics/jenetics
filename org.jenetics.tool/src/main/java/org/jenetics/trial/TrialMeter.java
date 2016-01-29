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
package org.jenetics.trial;

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
import java.util.Random;
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

import org.jenetics.util.ISeq;

/**
 * Represents an function testing measurement environment.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
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

	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	public Env getEnv() {
		return _env;
	}

	public Params<T> getParams() {
		return _params;
	}

	public DataSet getDataSet() {
		return _dataSet;
	}

	public int dataSize() {
		return _dataSet.dataSize();
	}

	public void sample(final Function<T, double[]> function) {
		_params.get()
			.subSeq(_dataSet.nextParamIndex())
			.forEach(p -> _dataSet.add(function.apply(p)));
	}

	@Override
	public String toString() {
		return format(
			"TrialMeter[samples=%d, params=%d]",
			dataSize(), _dataSet.nextParamIndex()
		);
	}

	public void write(final OutputStream out) {
		try {
			final Marshaller marshaller = jaxb.context().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(marshal(this), out);
		} catch (Exception e) {
			throw new UncheckedIOException(new IOException(e));
		}
	}

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

	public static <T> TrialMeter<T> read(final Path path) {
		try (InputStream in = new FileInputStream(path.toFile())) {
			return read(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static void main(final String[] args) throws Exception {
		final TrialMeter<String> trialMeter = TrialMeter.of(
			"Some name", "Some description",
			Params.of("Strings", ISeq.of("p1", "p2", "p3", "p4", "p5")),
			"fitness", "generation"
		);

		final Random random = new Random();

		for (int i = 0; i < 10; ++i) {
			trialMeter.sample(p -> {
				return new double[] {
					random.nextDouble(), random.nextDouble()
				};
			});
		}

		trialMeter.write(System.out);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "measurement")
	@XmlType(name = "org.jenetics.tool.TrialMeter")
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
