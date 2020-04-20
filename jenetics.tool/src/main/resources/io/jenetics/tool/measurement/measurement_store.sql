CREATE TABLE measurement(
	id BIGINT PRIMARY KEY,
	name TEXT,
	created_at TIMESTAMP
);

CREATE TABLE parameter(
    id BIGINT NOT NULL,
	measurement_id BIGINT NOT NULL REFERENCES measurement(id),
	values TEXT NOT NULL,

	PRIMARY KEY(id, measurement_id)
);

CREATE TABLE sample(
	id BIGINT NOT NULL,
	parameter_id BIGINT NOT NULL REFERENCES parameter(id),
	measurement_id BIGINT NOT NULL REFERENCES parameter(measurement_id) ,
	values TEXT NOT NULL,

	PRIMARY KEY(id, parameter_id, measurement_id)
);
