/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
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
 *     
 */
package org.jenetics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

import org.testng.Assert;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: SerializeUtils.java,v 1.1 2008-09-22 21:39:47 fwilhelm Exp $
 */
class SerializeUtils {

	public static void testSerialization(final Object object) 
		throws XMLStreamException, IOException 
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final XMLObjectWriter writer = XMLObjectWriter.newInstance(out);
		writer.setIndentation("\t");
		writer.write(object);
		writer.close();
		out.close();
		
		final byte[] data = out.toByteArray();
		System.out.println(new String(data));
		
		final ByteArrayInputStream in = new ByteArrayInputStream(data);
		final XMLObjectReader reader = XMLObjectReader.newInstance(in);
		final Object p = reader.read();
		
		Assert.assertEquals(p, object);
	}
	
}





