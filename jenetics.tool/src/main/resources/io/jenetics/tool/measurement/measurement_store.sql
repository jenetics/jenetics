CREATE TABLE measurement(
	id INTEGER PRIMARY KEY,
	name TEXT,
	created_at INTEGER
);

CREATE TABLE parameter(
	name TEXT,
	measurement_id INTEGER NOT NULL,

	FOREIGN KEY(measurement_id) REFERENCES measurement(id)
);

CREATE TABLE sample(

);