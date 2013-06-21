package org.jenetics;

import java.util.Iterator;
import java.util.List;

import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

final class BitGeneSeq implements ISeq<BitGene> {

	private final byte[] _genes;
	private final int _length;

	BitGeneSeq(final byte[] genes, final int length) {
		_genes = genes;
		_length = length;
	}

	@Override
	public BitGene get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <B> Iterator<B> iterator(
			Function<? super BitGene, ? extends B> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public <R> void foreach(Function<? super BitGene, ? extends R> function) {
		// TODO Auto-generated method stub

	}

	@Override
	public <R> void forEach(Function<? super BitGene, ? extends R> function) {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public boolean forall(Function<? super BitGene, Boolean> predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean forAll(Function<? super BitGene, Boolean> predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int indexOf(Object element) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexOf(Object element, int start) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexOf(Object element, int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexWhere(Function<? super BitGene, Boolean> predicate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexWhere(Function<? super BitGene, Boolean> predicate,
			int start) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexWhere(Function<? super BitGene, Boolean> predicate,
			int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object element) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object element, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object element, int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexWhere(Function<? super BitGene, Boolean> predicate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexWhere(Function<? super BitGene, Boolean> predicate,
			int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexWhere(Function<? super BitGene, Boolean> predicate,
			int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<BitGene> asList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BitGene[] toArray(BitGene[] array) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(String prefix, String separator, String suffix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(String separator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<BitGene> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISeq<BitGene> subSeq(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISeq<BitGene> subSeq(int start) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <B> ISeq<B> map(Function<? super BitGene, ? extends B> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public <A> ISeq<A> upcast(ISeq<? extends A> seq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MSeq<BitGene> copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
