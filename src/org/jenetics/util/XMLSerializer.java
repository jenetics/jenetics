/* 
 * XMLSerializer.java, 23.09.2008
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.util;

import java.io.InputStream;
import java.io.OutputStream;

import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: XMLSerializer.java,v 1.1 2008-09-23 18:01:51 fwilhelm Exp $
 */
public class XMLSerializer {

	private XMLSerializer() {
	}
	
	public static <T extends XMLSerializable> void write(final T object, final OutputStream out) 
		throws XMLStreamException 
	{
		Validator.notNull(out, "Output stream");
		Validator.notNull(object, "Object");
		
		final XMLObjectWriter writer = XMLObjectWriter.newInstance(out);
		writer.setIndentation("\t");
		writer.write(object);
		writer.close();
	}
	
	public static Object read(final InputStream in) throws XMLStreamException {
		Validator.notNull(in, "Input stream");
		
		final XMLObjectReader reader = XMLObjectReader.newInstance(in);
		return reader.read();
	}
	
}



