CREATE TABLE measurement(
	id BIGINT IDENTITY PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	created_at TIMESTAMP NOT NULL,
	sample_count INT NOT NULL,

	-- Test environment.
	os_name VARCHAR(255) NOT NULL,
	os_version VARCHAR(255) NOT NULL,
	os_architecture VARCHAR(255) NOT NULL,
	java_version VARCHAR(255) NOT NULL,
	java_runtime_name VARCHAR(255) NOT NULL,
	java_runtime_version VARCHAR(255) NOT NULL,
	java_vm_name VARCHAR(255) NOT NULL,
	java_vm_version VARCHAR(255) NOT NULL
);

CREATE TABLE parameter(
	id BIGINT IDENTITY PRIMARY KEY,
	measurement_id BIGINT NOT NULL REFERENCES measurement(id),
	value VARCHAR(4096) NOT NULL
);

CREATE TABLE sample(
	id BIGINT IDENTITY PRIMARY KEY,
	parameter_id BIGINT NOT NULL REFERENCES parameter(id),
	value VARCHAR(4096) NOT NULL
);
