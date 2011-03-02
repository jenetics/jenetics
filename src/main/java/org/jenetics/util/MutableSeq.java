package org.jenetics.util;

/**
 * The mutable view of a sequence.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface MutableSeq<T> extends Seq<T> {
	
	/**
	 * Set the {@code value} at the given {@code index}.
	 * 
	 * @param index the index of the new value.
	 * @param value the new value.
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range 
	 * 		  {@code (index < 0 || index >= size())}.
	 * @throws UnsupportedOperationException if this sequence is sealed 
	 * 		  ({@code isSealed() == true}).
	 */
	public void set(final int index, final T value);
	
	/**
	 * Return whether this sequence is sealed (immutable) or not.
	 * 
	 * @return {@code false} if this sequence can be changed, {@code true} 
	 *         otherwise.
	 */
	public boolean isSealed();
	
	/**
	 * Seal this mutable sequence and return an immutable view of this
	 * sequence.
	 * 
	 * @return an immutable view of this sequence.
	 */
	public ImmutableSeq<T> seal();
	
}