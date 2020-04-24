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

public final class DAO {
	private DAO() {
	}

	private static final class MeasurementRow {
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
	}

	private static final class ParameterRow {
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
	}

	private static final Query INSERT_MEASUREMENT = Query.of(
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

	private static final Dctor<Measurement> MEASUREMENT_DCTOR = Dctor.of(
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

	private static final Query INESRT_PARAMETER = Query.of(
		"INSERT INTO parameter(id, measurement_id, value) " +
		"VALUES(:id, :measurement_id, :value)"
	);

	private static final Dctor<ParameterRow> PARAMETER_DCTOR = Dctor.of(
		field("id", ParameterRow::id),
		field("measurement_id", ParameterRow::measurementId),
		field("value", p -> toJson(p.parameter()))
	);

	private static String toJson(final Object object) {
		return new Gson().toJson(object);
	}

	private static <T> T toObject(final String json, final Class<T> type) {
		return new Gson().fromJson(json, type);
	}

	public static long insert(final Measurement measurement, final Connection conn)
		throws SQLException
	{
		final long id = INSERT_MEASUREMENT
			.on(measurement, MEASUREMENT_DCTOR)
			.executeInsert(conn)
			.orElseThrow();

		final List<Parameter> parameters = measurement.parameters();

		final Batch batch = Batch.of(
			IntStream.range(0, parameters.size())
				.mapToObj(i -> new ParameterRow(i, id, parameters.get(i)))
				.collect(Collectors.toList()),
			PARAMETER_DCTOR
		);

		INESRT_PARAMETER.execute(batch, conn);

		return id;
	}

	private static final Query SELECT_MEASUREMENT = Query.of(
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

	private static final Query SELECT_PARAMETER = Query.of(
		"SELECT value FROM parameter WHERE measurement_id = :measurement_id"
	);

	private static final RowParser<Parameter> PARAMETER_PARSER = (row, conn) ->
		toObject(row.getString("value"), Parameter.class);

	private static final RowParser<Measurement> MEASUREMENT_PARSER = (row, conn) ->
		new Measurement(
			row.getString("name"),
			Instant.ofEpochMilli(row.getDate("created_at").getTime()),
			selectParameters(row.getLong("id"), conn),
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

	public static List<Measurement> select(final String name, final Connection conn)
		throws SQLException
	{
		return SELECT_MEASUREMENT
			.on(value("name", name))
			.as(MEASUREMENT_PARSER.list(), conn);
	}

	private static List<Parameter> selectParameters(final long measurementId, final Connection conn)
		throws SQLException
	{
		return SELECT_PARAMETER
			.on(value("measurement_id", measurementId))
			.as(PARAMETER_PARSER.list(), conn);
	}
}
