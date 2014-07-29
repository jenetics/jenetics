package org.jenetics.internal.engine;

import java.time.Duration;

public interface Result<T> {

	public Duration getExecutionDuration();
	
	public T getResult();
	
}
