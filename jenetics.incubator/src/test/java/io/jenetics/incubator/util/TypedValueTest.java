package io.jenetics.incubator.util;

public class TypedValueTest {

	record MilliSecond(Long value) implements TypedValue<Long, MilliSecond> {
	}

	record Meter(Double value) implements TypedValue<Double, Meter> {
	}

	record Distance(Meter value) implements TypedValue<Meter, Distance> {
	}

}
