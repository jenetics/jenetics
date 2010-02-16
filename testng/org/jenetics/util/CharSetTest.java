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
package org.jenetics.util;

import java.util.Iterator;
import java.util.regex.PatternSyntaxException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class CharSetTest {

	@Test
	public void expand1() {
		String value = CharSet.expand('a', 'z');
		Assert.assertEquals(value.length(), 26);
		Assert.assertEquals(value, "abcdefghijklmnopqrstuvwxyz");
		
		value = CharSet.expand('A', 'Z');
		Assert.assertEquals(value.length(), 26);
		Assert.assertEquals(value, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		
		value = CharSet.expand('0', '9');
		Assert.assertEquals(value.length(), 10);
		Assert.assertEquals(value, "0123456789");
	}
	
	@Test
	public void expand2() {
		String value = CharSet.expand("a-z");
		Assert.assertEquals(value.length(), 26);
		Assert.assertEquals(value, "abcdefghijklmnopqrstuvwxyz");
		
		value = CharSet.expand("a-z\\-");
		Assert.assertEquals(value.length(), 27);
		Assert.assertEquals(value, "abcdefghijklmnopqrstuvwxyz-");
		
		value = CharSet.expand("a-z\\\\xx");
		Assert.assertEquals(value.length(), 29);
		Assert.assertEquals(value, "abcdefghijklmnopqrstuvwxyz\\xx");
		
		value = CharSet.expand("A-Z");
		Assert.assertEquals(value.length(), 26);
		Assert.assertEquals(value, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		
		value = CharSet.expand("0-9");
		Assert.assertEquals(value.length(), 10);
		Assert.assertEquals(value, "0123456789");
		
		value = CharSet.expand("0-9yxcvba-z");
		Assert.assertEquals(value.length(), 41);
		Assert.assertEquals(value, "0123456789yxcvbabcdefghijklmnopqrstuvwxyz");
		
		value = CharSet.expand("0-9a-zA-Z");
		Assert.assertEquals(value.length(), 10 + 26 + 26);
		Assert.assertEquals(value, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}
	
	@Test(expectedExceptions = PatternSyntaxException.class)
	public void expand3() {
		CharSet.expand("a-z-");
	}
	
	@Test(expectedExceptions = PatternSyntaxException.class)
	public void expand4() {
		CharSet.expand("-az");
	}

	@Test
	public void contains() {
		final CharSet set = new CharSet(CharSet.expand("a-z"));
		Assert.assertTrue(set.contains('t'));
		Assert.assertTrue(set.contains('a'));
		Assert.assertTrue(set.contains('z'));
		Assert.assertFalse(set.contains('T'));
		Assert.assertFalse(set.contains('1'));
		Assert.assertFalse(set.contains('Z'));
	}
	
	@Test
	public void iterate() {
		final CharSet set = new CharSet(CharSet.expand("a-z"));
		final String values = CharSet.expand("a-z");
		final Iterator<Character> it = set.iterator();
		for (int i = 0; i < values.length(); ++i) {
			Assert.assertTrue(it.hasNext());
			Assert.assertEquals(it.next(), new Character(values.charAt(i)));
		}
		Assert.assertFalse(it.hasNext());
	}
	
	@Test
	public void subSequence() {
		final CharSet set = new CharSet(CharSet.expand("a-z"));
		final CharSet sub = set.subSequence(3, 10);
		Assert.assertEquals(sub.length(), 7);
		Assert.assertEquals(sub.toString(), "defghij");
	}
	
}





