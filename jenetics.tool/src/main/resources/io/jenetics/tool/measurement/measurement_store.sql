CREATE TABLE measurement(
	id BIGINT PRIMARY KEY,
	name TEXT,
	created_at TIMESTAMP
);

CREATE TABLE parameter(
	name TEXT,
	measurement_id BIGINT NOT NULL,

	FOREIGN KEY(measurement_id) REFERENCES measurement(id)
);

CREATE TABLE sample(

);