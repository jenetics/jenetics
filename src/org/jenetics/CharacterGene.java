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

import static org.jenetics.util.Validator.nonNull;

import java.util.Random;

import javolution.context.LocalContext;
import javolution.context.ObjectFactory;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.CharSet;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Validator;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CharacterGene.java,v 1.10 2010-01-28 19:34:14 fwilhelm Exp $
 */
public class CharacterGene 
	implements Gene<Character, CharacterGene>, Comparable<CharacterGene>, 
				Realtime, XMLSerializable 
{
	private static final long serialVersionUID = 5091130159700639888L;
	
	private static final LocalContext.Reference<CharSet>
		CHARACTERS = new LocalContext.Reference<CharSet>(new CharSet(
				CharSet.expand("0-9a-zA-Z") +  " !\"$%&/()=?`{[]}\\+~*#';.:,-_<>|@^'"
			));
	
	private Character _character;
	
	protected CharacterGene() {
	}
		
	@Override
	public boolean isValid() {
		return true;
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
	 *         comparison; it is not local-dependent. 
	 */
	@Override
	public int compareTo(final CharacterGene that) {
		return getAllele().compareTo(that.getAllele());
	}
	
	@Override
	public CharacterGene newInstance() {
		final Random random = RandomRegistry.getRandom();
		final CharSet charset = CHARACTERS.get();
		final int index = random.nextInt(charset.length());
		
		return valueOf(charset.charAt(index));
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
		return getAllele().equals(gene.getAllele());
	}
	
	@Override
	public String toString() {
		return _character.toString();
	}
	
	@Override
	public Text toText() {
		return Text.valueOf(_character);
	}
	
	
	/**
	 * Test, if the given character is valid.
	 * 
	 * @param c The character to test.
	 * @return true if the character is valid, false otherwise.
	 */
	public static boolean isValidCharacter(final Character c) {
		return CHARACTERS.get().contains(c);
	}
	
	/**
	 * Retunr a (unmodifiable) set of valid characters.
	 * 
	 * @return the {@link CharSet} of valid characters.
	 */
	public static CharSet getCharacters() {
		return CHARACTERS.get();
	}
	
	/**
	 * Set the characters to use.
	 * 
	 * @param characters to user.
	 * @throws NullPointerException if the given {@code characters} are 
	 *         {@code null}.
	 */
	public static void setCharacters(final CharSet characters) {
		Validator.nonNull(characters, "CharSet");
		CHARACTERS.set(characters);
	}
	
	/**
	 * Set the characters to user.
	 * 
	 * @param characters the characters to user.
	 * @throws NullPointerException if the given characters are null.
	 */
	public static void setCharacters(final CharSequence characters) {
		Validator.nonNull(characters, "Characters");
		CHARACTERS.set(new CharSet(characters));
	}

	
	private static final ObjectFactory<CharacterGene> 
	FACTORY = new ObjectFactory<CharacterGene>() {
		@Override
		protected CharacterGene create() {
			return new CharacterGene();
		}
	};
	
	/**
	 * Create a new CharacterGene with a randomly chosen character from the
	 * set of valid characters.
	 */
	public static CharacterGene valueOf() {
		final Random random = RandomRegistry.getRandom();
		int pos = random.nextInt(CHARACTERS.get().length());
		return valueOf(CHARACTERS.get().charAt(pos));
	}
	
	/**
	 * Create a new CharacterGene from the give character.
	 * 
	 * @param character The allele.
	 * @throws NullPointerException if the <code>character</code> is null.
	 * @throws IllegalArgumentException if the <code>character</code> is not
	 * 		a valid character. 
	 * 		See {@link CharacterGene#isValidCharacter(Character)}
	 * 		and {@link CharacterGene#getCharacters()}.
	 */
	public static CharacterGene valueOf(final Character character) {
		nonNull(character, "Character");
		
		CharacterGene g = FACTORY.object();
		if (!CHARACTERS.get().contains(character)) {
			throw new IllegalArgumentException(
				"Character '" + character + "' is not valid. "
			);
		}
		g._character = character;
		
		return g;
	}
	
	static final XMLFormat<CharacterGene> 
	XML = new XMLFormat<CharacterGene>(CharacterGene.class) 
	{
		private static final String VALUE = "value";
		
		@Override
		public CharacterGene newInstance(
			final Class<CharacterGene> cls, final InputElement xml
		) 
			throws XMLStreamException 
		{
			final Character character = xml.getAttribute(VALUE, 'a');
			return CharacterGene.valueOf(character);
		}
		@Override
		public void write(final CharacterGene gene, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(VALUE, gene._character.charValue());
		}
		@Override
		public void read(final InputElement element, final CharacterGene gene) {
		}
	};

	
}






