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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics;

import static org.jenetics.util.ObjectUtils.eq;
import static org.jenetics.util.ObjectUtils.hashCodeOf;
import static org.jenetics.util.Validator.nonNull;

import java.util.Random;

import javolution.context.ObjectFactory;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.CharSet;
import org.jenetics.util.Converter;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class CharacterGene 
	implements 
		Gene<Character, CharacterGene>, 
		Comparable<CharacterGene>, 
		Realtime, 
		XMLSerializable 
{
	private static final long serialVersionUID = 1L;
	
	public static final CharSet DEFAULT_CHARACTERS = new CharSet(
				CharSet.expand("0-9a-zA-Z") +  
				" !\"$%&/()=?`{[]}\\+~*#';.:,-_<>|@^'"
			);
	
	private CharSet _validCharacters;
	private Character _character;
		
	CharacterGene() {
	}
		
	@Override
	public boolean isValid() {
		return _validCharacters.contains(_character);
	}
	
	@Override
	public Character getAllele() {
		return _character;
	}

	/**
	 * Test, if the given character is valid.
	 * 
	 * @param c The character to test.
	 * @return true if the character is valid, false otherwise.
	 */
	public boolean isValidCharacter(final Character c) {
		return _validCharacters.contains(c);
	}
	
	/**
	 * Retunr a (unmodifiable) set of valid characters.
	 * 
	 * @return the {@link CharSet} of valid characters.
	 */
	public CharSet getValidCharacters() {
		return _validCharacters;
	}
	
	@Override
	public CharacterGene copy() {
		return valueOf(_character, _validCharacters);
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
	
	/**
	 * Return the {@link Factory} view of this gene.
	 * 
	 * @return the {@link Factory} view of this gene.
	 */
	Factory<CharacterGene> asFactory() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(_character).value();
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
		return eq(getAllele(), gene.getAllele());
	}
	
	@Override
	public String toString() {
		return _character.toString();
	}
	
	@Override
	public Text toText() {
		return Text.valueOf(_character);
	}
	
	
	/* *************************************************************************
	 *  Property access methods.
	 * ************************************************************************/
	
	/**
	 * Converter for accessing the allele from a given gene.
	 */
	public static final Converter<CharacterGene, Character> Allele =
		new Converter<CharacterGene, Character>() {
				@Override public Character convert(final CharacterGene value) {
					return value._character;
				}
			};
			
	/**
	 * Converter for accessing the valid characters from a given gene.
	 */
	public static final Converter<CharacterGene, CharSet> ValidCharacters =
		new Converter<CharacterGene, CharSet>() {
				@Override public CharSet convert(final CharacterGene value) {
					return value._validCharacters;
				}
			};
			
	
	/* *************************************************************************
	 *  Factory methods
	 * ************************************************************************/
	
	@Override
	public CharacterGene newInstance() {
		return valueOf(_validCharacters);
	}
	
	/**
	 * Create a new character gene from the given character. If the character
	 * is not within the {@link #getValidCharacters()}, an invalid gene will be
	 * created.
	 * 
	 * @param character the character value of the created gene.
	 * @return a new character gene.
	 * @throws NullPointerException if the given {@code character} is 
	 *         {@code null}.
	 */
	public CharacterGene newInstance(final Character character) {
		return valueOf(character, _validCharacters);
	}
	
	
	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/
	
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
	 * 
	 * @param validCharacters the valid characters for this gene.
	 * @return a new valid, <em>random</em> gene,
	 * @throws NullPointerException if the {@code validCharacters} are 
	 *         {@code null}.
	 */
	public static CharacterGene valueOf(final CharSet validCharacters) {
		final Random random = RandomRegistry.getRandom();
		int pos = random.nextInt(validCharacters.length());
		return valueOf(validCharacters.charAt(pos), validCharacters);
	}
		
	/**
	 * Create a new character gene from the given character. If the character
	 * is not within the {@link #DEFAULT_CHARACTERS}, an invalid gene will be
	 * created.
	 * 
	 * @param character the character value of the created gene.
	 * @return a new character gene.
	 * @throws NullPointerException if the given {@code character} is 
	 *         {@code null}.
	 */
	public static CharacterGene valueOf(final Character character) {
		return valueOf(character, DEFAULT_CHARACTERS);
	}
	
	public static CharacterGene valueOf() {
		final Random random = RandomRegistry.getRandom();
		final int index = random.nextInt(DEFAULT_CHARACTERS.length());
		return valueOf(DEFAULT_CHARACTERS.charAt(index));
	}
	
	/**
	 * Create a new CharacterGene from the give character.
	 * 
	 * @param character The allele.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the {@code validCharacters} are empty.
	 */
	public static CharacterGene valueOf(
		final Character character, 
		final CharSet validCharacters
	) {
		nonNull(character, "Character");
		nonNull(validCharacters, "Valid characters");
		
		CharacterGene g = FACTORY.object();
		g._character = character;
		g._validCharacters = validCharacters;
		
		return g;
	}
	
	
	/* *************************************************************************
	 *  Factory methods
	 * ************************************************************************/
	
	static final XMLFormat<CharacterGene> 
	XML = new XMLFormat<CharacterGene>(CharacterGene.class) 
	{
		private static final String VALID_CHARS = "valid-characters";
		
		@Override
		public CharacterGene newInstance(
			final Class<CharacterGene> cls, final InputElement xml
		) 
			throws XMLStreamException 
		{
			final String validCharacters = xml.getAttribute(
					VALID_CHARS, 
					DEFAULT_CHARACTERS.toString()
				);
			final String character = xml.getText().toString();
			
			return CharacterGene.valueOf(character.charAt(0), new CharSet(validCharacters));
		}
		@Override
		public void write(final CharacterGene gene, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(VALID_CHARS, gene.getValidCharacters().toString());
			xml.addText(gene._character.toString());
		}
		@Override
		public void read(final InputElement element, final CharacterGene gene) {
		}
	};

	
}






