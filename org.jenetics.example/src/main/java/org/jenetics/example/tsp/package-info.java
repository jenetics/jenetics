/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlSchema(
	namespace = "http://www.topografix.com/GPX/1/1",
	elementFormDefault = XmlNsForm.QUALIFIED,
	xmlns = {
		@XmlNs(
			namespaceURI = "http://www.topografix.com/GPX/1/1",
			prefix = ""
		)
	}
)
package org.jenetics.example.tsp;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
