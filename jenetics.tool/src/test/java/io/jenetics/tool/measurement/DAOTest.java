package io.jenetics.tool.measurement;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.jenetics.facilejdbc.Transactional;

public class DAOTest {

	private static final Measurement MEASUREMENT = new Measurement(
		"measure",
		Instant.now(),
		List.of(
			Parameter.of("SELECT1", 1, 12.34, "BAR1"),
			Parameter.of("SELECT2", 2, 22.34, "BAR2"),
			Parameter.of("SELECT3", 3, 32.34, "BAR3"),
			Parameter.of("SELECT4", 4, 42.34, "BAR4"),
			Parameter.of("SELECT5", 5, 52.34, "BAR5")
		),
		100,
		Environment.of()
	);

	private final Transactional db = () -> DriverManager.getConnection(
		"jdbc:hsqldb:mem:testdb",
		"SA",
		""
	);

	@BeforeClass
	public void setup() throws IOException, SQLException {
		final var queries = Queries.read(getClass().getResourceAsStream(
			"/io/jenetics/tool/measurement/store-hsqldb.sql"
		));

		db.transaction().accept(conn -> {
			for (var query : queries) {
				query.execute(conn);
			}
		});
	}

	@Test
	public void insert() throws SQLException {
		final long id = db.transaction().apply(conn -> DAO.insert(MEASUREMENT, conn));
		System.out.println(id);
	}

	@Test(dependsOnMethods = "insert")
	public void select() throws SQLException {
		final var result = db.transaction().apply(conn -> DAO.selectByName("measure", conn));
		System.out.println(result.get(0).value().environment());
		System.out.println(result.get(0).value().parameters());
		System.out.println(MEASUREMENT.environment());
	}

}
