package io.jenetics.incubator.csv;

import static java.util.Objects.requireNonNull;

import io.jenetics.ext.util.CsvSupport.Quote;
import io.jenetics.ext.util.CsvSupport.Separator;

public final class Splitter {

	private final Quote quote = Quote.DEFAULT;
	private final Separator separator = Separator.DEFAULT;

	final class Quoted implements CharProcessor{
		@Override
		public void process(char ch) {
		}
	}

	private final Quoted quoted = new Quoted();

	final CharIterator iterator;

	CharProcessor processor;

	Splitter(final CharSequence chars) {
		this.iterator = new CharIterator(chars);
	}

	public void process() {
		while (iterator.next() != -1) {
			//System.out.print((char)iterator.current);
			if (iterator.current == quote.value()) {
				processor = quoted;
			}
		}

		System.out.println();
	}

	public static void main(String[] args) {
		final Splitter splitter = new Splitter("some,bar,to");
		splitter.process();
	}

}

final class CharIterator {
	private final CharSequence chars;
	private final int n;

	int previous;
	int current;
	int next;
	int index;

	CharIterator(final CharSequence chars) {
		this.chars = requireNonNull(chars);
		this.n = chars.length();
		next = chars.isEmpty() ? -1 : chars.charAt(0);
		index = -1;
	}

	int next() {
		++index;
		previous = current;
		current = next;
		next =  index + 1 < n ? chars.charAt(index + 1) : -1;

		return current;
	}

}
