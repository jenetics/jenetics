package org.jenetics;

import javolution.xml.stream.XMLStreamException;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class NumberStatisticsTest {

	@SuppressWarnings("unchecked")
	@Test
	public void serialize() throws XMLStreamException {
		final Statistics statistics = new Statistics(234234, null, null, 0, 0, 0);
		final NumberStatistics numberStatistics = new NumberStatistics(
				statistics, 3.234, 42.234, 23
			);
		SerializeUtils.testSerialization(numberStatistics);
	}
	
}
