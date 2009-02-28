package org.jenetics;

import javolution.xml.stream.XMLStreamException;

import org.testng.annotations.Test;

public class NumberStatisticsTest {

	@SuppressWarnings("unchecked")
	@Test
	public void serialize() throws XMLStreamException {
		final Statistics statistics = new Statistics(null, null, 0, 0, 0);
		final NumberStatistics numberStatistics = new NumberStatistics(statistics, 3.234, 42.234);
		SerializeUtils.testSerialization(numberStatistics);
	}
	
}
