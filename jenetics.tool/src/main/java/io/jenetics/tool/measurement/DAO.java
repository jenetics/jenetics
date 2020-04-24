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

		private static final RowParser<Measurement> PARSER = (row, conn) ->
			new Measurement(
				row.getString("name"),
				Instant.ofEpochMilli(row.getDate("created_at").getTime()),
				ParameterRow.select(row.getLong("id"), conn),
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

		private static final Query SELECT = Query.of(
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
			"WHERE name = :name"
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
		 * Selects all measurements with a given name.
		 *
		 * @param name the name of the measurement
		 * @param conn the DB connection
		 * @return all measurements with a given name
		 * @throws SQLException if the selection fails
		 */
		static List<Measurement> selectByName(
			final String name,
			final Connection conn
		)
			throws SQLException
		{
			return SELECT
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

		private static final RowParser<Parameter> PARSER = (row, conn) ->
			toObject(row.getString("value"), Parameter.class);

		private static final Query INSERT = Query.of(
			"INSERT INTO parameter(measurement_id, value) " +
			"VALUES(:measurement_id, :value)"
		);

		private static final Query SELECT = Query.of(
			"SELECT value " +
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

		static List<Parameter> select(final long measurementId, final Connection conn)
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
	 * Selects all measurements with a given name.
	 *
	 * @param name the name of the measurement
	 * @param conn the DB connection
	 * @return all measurements with a given name
	 * @throws SQLException if the selection fails
	 */
	public static List<Measurement> selectByName(final String name, final Connection conn)
		throws SQLException
	{
		return MeasurementRow.selectByName(name, conn);
	}

}
