package org.jenetics;

import java.io.IOException;

import javolution.xml.stream.XMLStreamException;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class NumberStatisticsTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void xmlSerialize() throws XMLStreamException {
		final Statistics statistics = new Statistics(234234, null, null, 0, 0, 0);
		final NumberStatistics numberStatistics = new NumberStatistics(
				statistics, 3.234, 42.234, 23
			);
		
		SerializeUtils.testXMLSerialization(numberStatistics);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void objectSerialize() throws IOException {
		final Statistics statistics = new Statistics(234234, null, null, 0, 0, 0);
		final NumberStatistics numberStatistics = new NumberStatistics(
				statistics, 3.234, 42.234, 23
			);
		
		SerializeUtils.testSerialization(numberStatistics);
	}
	
}
