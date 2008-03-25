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

import static org.jenetics.Checker.checkNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javolution.context.ObjectFactory;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CharacterGene.java,v 1.1 2008-03-25 18:31:55 fwilhelm Exp $
 */
public class CharacterGene 
	implements Gene<Character>, Comparable<CharacterGene>, Realtime, XMLSerializable 
{
	private static final long serialVersionUID = 5091130159700639888L;
	
	private static final Character[] VALID_CHARACTERS = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
		'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ö', 'Ä',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
		'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ' ', '!', '"', '$',
		'%', '&', '/', '(', ')', '=', '?', '`', '{', '[', ']', '}', '\\', '+', '~',
		'*', '#', '\'', ',', ';', '.', ':', '-', '_', '<', '>', '|', '@', '^'
	};
	private static final Map<Character,Integer> 
	CHARACTER_POSITION = new Hashtable<Character,Integer>();
	
	private static final Set<Character> CHARACTER_SET = new HashSet<Character>();
	static {
		Collections.addAll(CHARACTER_SET, VALID_CHARACTERS);
		for (int i = 0; i < VALID_CHARACTERS.length; ++i) {
			CHARACTER_POSITION.put(VALID_CHARACTERS[i], i);
		}
	}
	
	private Character _character;
	
	protected CharacterGene() {
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
	/**
	 * Return the current character allel.
	 * 
	 * @return The current character allel.
	 */
	public Character getCharacter() {
		return _character;
	}
	
	@Override
	public Character getAllele() {
		return _character;
	}

	@Override
	public CharacterGene copy() {
		return valueOf(_character);
	}
	
	/**
	 * @see java.lang.Character#compareTo(java.lang.Character)
	 * @param that The other gene to compare.
	 * @return the value 0 if the argument Character is equal to this Character; 
	 *         a value less than 0 if this Character is numerically less than 
	 *         the Character argument; and a value greater than 0 if this 
	 *         Character is numerically greater than the Character argument 
	 *         (unsigned comparison). Note that this is strictly a numerical 
	 *         comparison; it is not locale-dependent. 
	 */
	@Override
	public int compareTo(final CharacterGene that) {
		return getAllele().compareTo(that.getAllele());
	}

	/**
	 * Test, if the given character is valid.
	 * 
	 * @param c The character to test.
	 * @return true if the character is valid, false otherwise.
	 */
	public static boolean isValidCharacter(final Character c) {
		return CHARACTER_SET.contains(c);
	}
	
	/**
	 * Retunr a (unmodifiable) set of valid characters.
	 * 
	 * @return A set of valid characters.
	 */
	public static Set<Character> getValidCharacters() {
		return Collections.unmodifiableSet(CHARACTER_SET);
	}
	
	@Override
	public int hashCode() {
		return _character.hashCode();
	}
	
	@Override 
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CharacterGene)) {
			return false;
		}
		CharacterGene gene = (CharacterGene)obj;
		return getCharacter().equals(gene.getCharacter());
	}
	
	@Override
	public Text toText() {
		return Text.valueOf(_character);
	}
	
	private static final ObjectFactory<CharacterGene> 
	FACTORY = new ObjectFactory<CharacterGene>() {
		@Override
		protected CharacterGene create() {
			return new CharacterGene();
		}
	};
	
	/**
	 * Create a new CharacterGene with a randomly choosen character from the
	 * set of valid characters.
	 * 
	 * @see CharacterGene#getValidCharacters 
	 */
	public static CharacterGene valueOf() {
		final Random random = RandomRegistry.getRandom();
		int pos = random.nextInt(VALID_CHARACTERS.length);
		return valueOf(VALID_CHARACTERS[pos]);
	}
	
	/**
	 * Create a new CharacterGene from the give character.
	 * 
	 * @param character The allel.
	 * @throws NullPointerException if the <code>character</code> is null.
	 * @throws IllegalArgumentException if the <code>character</code> is not
	 * 		a valid character. 
	 * 		See {@link CharacterGene#isValidCharacter(Character)}
	 * 		and {@link CharacterGene#getValidCharacters()}.
	 */
	public static CharacterGene valueOf(final Character character) {
		checkNull(character, "Character");
		
		CharacterGene g = FACTORY.object();
		if (!CHARACTER_SET.contains(character)) {
			throw new IllegalArgumentException(
				"Character '" + character + "' is not valid. "
			);
		}
		g._character = character;
		
		return g;
	}
	
	static final XMLFormat<CharacterGene> XML = new XMLFormat<CharacterGene>(CharacterGene.class) {
		@Override
		public CharacterGene newInstance(final Class<CharacterGene> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final Character character = xml.getAttribute("value", 'a');
			return CharacterGene.valueOf(character);
		}
		@Override
		public void write(final CharacterGene gene, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("value", gene._character);
		}
		@Override
		public void read(final InputElement element, final CharacterGene gene) {
		}
	};
	
}






