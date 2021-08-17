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

import static io.jenetics.facilejdbc.Dctor.field;
import static io.jenetics.facilejdbc.Param.value;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;

import io.jenetics.facilejdbc.Batch;
import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;

/**
 * DAO for measurement insert/select operations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class DAO {
	private DAO() {
	}

	/**
	 * Row/accessor for the {@link Measurement} class.
	 */
	private static final class MeasurementRow {

		private static final Dctor<Measurement> DCTOR = Dctor.of(
			field("name", Measurement::name),
			field("created_at", m -> new Date(m.createdAt().toEpochMilli())),
			field("sample_count", Measurement::sampleCount),
			field("os_name", m -> m.environment().osName()),
			field("os_version", m -> m.environment().osVersion()),
			field("os_architecture", m -> m.environment().osArch()),
			field("java_version", m -> m.environment().javaVersion()),
			field("java_runtime_name", m -> m.environment().javaRuntimeName()),
			field("java_runtime_version", m -> m.environment().javaRuntimeVersion()),
			field("java_vm_name", m -> m.environment().javaVMName()),
			field("java_vm_version", m -> m.environment().getJavaVMVersion())
		);

		private static final RowParser<MeasurementRow> PARSER = (row, conn) ->
			new MeasurementRow(
				row.getLong("id"),
				new Measurement(
					row.getString("name"),
					Instant.ofEpochMilli(row.getDate("created_at").getTime()),
					ParameterRow.select(row.getLong("id"), conn).stream()
						.map(ParameterRow::parameter)
						.collect(Collectors.toList()),
					row.getInt("sample_count"),
					Environment.of(
						row.getString("os_name"),
						row.getString("os_version"),
						row.getString("os_architecture"),
						row.getString("java_version"),
						row.getString("java_runtime_name"),
						row.getString("java_runtime_version"),
						row.getString("java_vm_name"),
						row.getString("java_vm_version")
					)
				)
			);

		private static final Query INSERT = Query.of(
			"INSERT INTO measurement(" +
				"name," +
				"created_at," +
				"sample_count," +
				"os_name," +
				"os_version," +
				"os_architecture," +
				"java_version," +
				"java_runtime_name," +
				"java_runtime_version," +
				"java_vm_name," +
				"java_vm_version" +
			") " +
				"VALUES(" +
				":name," +
				":created_at," +
				":sample_count," +
				":os_name," +
				":os_version," +
				":os_architecture," +
				":java_version," +
				":java_runtime_name," +
				":java_runtime_version," +
				":java_vm_name," +
				":java_vm_version" +
				")"
		);

		private static final Query SELECT_ALL = Query.of(
			"SELECT " +
				"id," +
				"name," +
				"created_at," +
				"sample_count," +
				"os_name," +
				"os_version," +
				"os_architecture," +
				"java_version," +
				"java_runtime_name," +
				"java_runtime_version," +
				"java_vm_name," +
				"java_vm_version " +
			"FROM measurement " +
			"ORDER BY created_at"
		);

		private static final Query SELECT_BY_NAME = Query.of(
			"SELECT " +
				"id," +
				"name," +
				"created_at," +
				"sample_count," +
				"os_name," +
				"os_version," +
				"os_architecture," +
				"java_version," +
				"java_runtime_name," +
				"java_runtime_version," +
				"java_vm_name," +
				"java_vm_version " +
			"FROM measurement " +
			"WHERE name = :name " +
			"ORDER BY created_at"
		);

		private final long _id;
		private final Measurement _measurement;

		MeasurementRow(final long id, final Measurement measurement) {
			_id = id;
			_measurement = measurement;
		}

		long id() {
			return _id;
		}

		Measurement measurement() {
			return _measurement;
		}

		/**
		 * Insert a given measurement object into the DB and returns the
		 * assigned primary key.
		 *
		 * @param measurement the measurement object to insert
		 * @param conn the DB connection
		 * @return the primary key of the inserted measurement
		 * @throws SQLException if the insertion fails
		 */
		static long insert(final Measurement measurement, final Connection conn)
			throws SQLException
		{
			final long id = INSERT
				.on(measurement, DCTOR)
				.executeInsert(conn)
				.orElseThrow();

			final List<Parameter> parameters = measurement.parameters();

			final Batch batch = Batch.of(
				IntStream.range(0, parameters.size())
					.mapToObj(i -> new ParameterRow(i, id, parameters.get(i)))
					.collect(Collectors.toList()),
				ParameterRow.DCTOR
			);

			ParameterRow.INSERT.execute(batch, conn);

			return id;
		}

		/**
		 * Selects all measurements.
		 *
		 * @param conn the DB connection
		 * @return all measurements with a given name
		 * @throws SQLException if the selection fails
		 */
		static List<MeasurementRow> selectAll(final Connection conn)
			throws SQLException
		{
			return SELECT_ALL.as(PARSER.list(), conn);
		}

		/**
		 * Selects all measurements with a given name.
		 *
		 * @param name the name of the measurement
		 * @param conn the DB connection
		 * @return all measurements with a given name
		 * @throws SQLException if the selection fails
		 */
		static List<MeasurementRow> selectByName(
			final String name,
			final Connection conn
		)
			throws SQLException
		{
			return SELECT_BY_NAME
				.on(value("name", name))
				.as(PARSER.list(), conn);
		}

	}

	/**
	 * Row/accessor for the {@link Parameter} class.
	 */
	private static final class ParameterRow {

		private static final Dctor<ParameterRow> DCTOR = Dctor.of(
			field("measurement_id", ParameterRow::measurementId),
			field("value", p -> toJson(p.parameter()))
		);

		private static final RowParser<ParameterRow> PARSER = (row, conn) ->
			new ParameterRow(
				row.getLong("id"),
				row.getLong("measurement_id"),
				toObject(row.getString("value"), Parameter.class)
			);

		private static final Query INSERT = Query.of(
			"INSERT INTO parameter(measurement_id, value) " +
			"VALUES(:measurement_id, :value)"
		);

		private static final Query SELECT = Query.of(
			"SELECT id, measurement_id, value " +
			"FROM parameter " +
			"WHERE measurement_id = :measurement_id"
		);

		private final long _id;
		private final long _measurementId;
		private final Parameter _parameter;

		ParameterRow(
			final long id,
			final long measurementId,
			final Parameter parameter
		) {
			_id = id;
			_measurementId = measurementId;
			_parameter = parameter;
		}

		long id() {
			return _id;
		}

		long measurementId() {
			return _measurementId;
		}

		Parameter parameter() {
			return _parameter;
		}

		static List<ParameterRow> select(
			final long measurementId,
			final Connection conn
		)
			throws SQLException
		{
			return SELECT
				.on(value("measurement_id", measurementId))
				.as(PARSER.list(), conn);
		}
	}

	private static final class SampleRow {

		private static final Dctor<SampleRow> DCTOR = Dctor.of(
			field("parameter_id", SampleRow::parameterId),
			field("value", s -> toJson(s.sample()))
		);

		private static final RowParser<ParameterSample> PARSER = (row, conn) ->
			new ParameterSample(
				toObject(row.getString("parameter_value"), Parameter.class),
				toObject(row.getString("sample_value"), Sample.class)
			);

		private static final Query INSERT = Query.of(
			"INSERT INTO sample(parameter_id, value) " +
			"VALUES(:parameter_id, :value)"
		);

		private static final Query SELECT = Query.of(
			"SELECT parameter.value AS parameter_value, sample.value AS sample_value " +
			"FROM sample " +
			"INNER JOIN parameter ON parameter.id = parameter_id " +
			"WHERE parameter.measurement_id = :measurement_id "
		);

		private final long _id;
		private final long _parameterId;
		private final Sample _sample;

		SampleRow(
			final long id,
			final long parameterId,
			final Sample sample
		) {
			_id = id;
			_parameterId = parameterId;
			_sample = sample;
		}

		long id() {
			return _id;
		}

		long parameterId() {
			return _parameterId;
		}

		Sample sample() {
			return _sample;
		}

		static long insert(
			final long parameterId,
			final Sample sample,
			final Connection conn
		)
			throws SQLException
		{
			return INSERT
				.on(new SampleRow(0, parameterId, sample), DCTOR)
				.executeInsert(conn)
				.orElseThrow();
		}

		static List<ParameterSample> select(
			final long measurementId,
			final Connection conn
		)
			throws SQLException
		{
			return SELECT
				.on(value("measurement_id", measurementId))
				.as(PARSER.list(), conn);
		}

	}

	private static String toJson(final Object object) {
		return new Gson().toJson(object);
	}

	private static <T> T toObject(final String json, final Class<T> type) {
		return new Gson().fromJson(json, type);
	}

	/**
	 * Insert a given measurement object into the DB and returns the
	 * assigned primary key.
	 *
	 * @param measurement the measurement object to insert
	 * @param conn the DB connection
	 * @return the primary key of the inserted measurement
	 * @throws SQLException if the insertion fails
	 */
	public static long insert(final Measurement measurement, final Connection conn)
		throws SQLException
	{
		return MeasurementRow.insert(measurement, conn);
	}

	/**
	 * Selects all measurements.
	 *
	 * @param conn the DB connection
	 * @return all measurements with a given name
	 * @throws SQLException if the selection fails
	 */
	public static List<Stored<Measurement>> selectAll(final Connection conn)
		throws SQLException
	{
		return MeasurementRow.selectAll(conn).stream()
			.map(row -> Stored.of(row.id(), row.measurement()))
			.collect(Collectors.toList());
	}

	/**
	 * Selects all measurements with a given name.
	 *
	 * @param name the name of the measurement
	 * @param conn the DB connection
	 * @return all measurements with a given name
	 * @throws SQLException if the selection fails
	 */
	public static List<Stored<Measurement>> selectByName(
		final String name,
		final Connection conn
	)
		throws SQLException
	{
		return MeasurementRow.selectByName(name, conn).stream()
			.map(row -> Stored.of(row.id(), row.measurement()))
			.collect(Collectors.toList());
	}

	public static List<Stored<Parameter>> selectParameters(
		final long measurementId,
		final Connection conn
	)
		throws SQLException
	{
		return ParameterRow.select(measurementId, conn).stream()
			.map(row -> Stored.of(row.id(), row.parameter()))
			.collect(Collectors.toList());
	}

	public static long insertSample(
		final long parameterId,
		final Sample sample,
		final Connection conn
	)
		throws SQLException
	{
		return SampleRow.insert(parameterId, sample, conn);
	}

	public static List<ParameterSample> selectParameterSamples(
		final long measurementId,
		final Connection conn
	)
		throws SQLException
	{
		return SampleRow.select(measurementId, conn);
	}

}
