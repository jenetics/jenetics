package io.jenetics.incubator.bean;

import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import io.jenetics.incubator.bean.Property.Path;

public class BreathFirstPropertyIterator  implements Iterator<Property> {

	private final Property.Reader reader;

	private final Queue<Iterator<Property>> queue = new ArrayDeque<>();

	BreathFirstPropertyIterator(
		final Path basePath,
		final Object root,
		final Property.Reader reader
	) {
		this.reader = requireNonNull(reader);

		queue.add(reader.read(basePath, root).iterator());
	}

	@Override
	public boolean hasNext() {
		final Iterator<Property> peek = queue.peek();
		return peek != null && peek.hasNext();
	}

	@Override
	public Property next() {
		final Iterator<Property> it = queue.peek();
		if (it == null) {
			throw new NoSuchElementException("No next element.");
		}

		final Property node = it.next();
		if (!it.hasNext()) {
			queue.poll();
		}

		final Iterator<Property> children = reader
			.read(node.path(), node.value())
			.iterator();

		if (children.hasNext()) {
			queue.add(children);
		}

		return node;
	}

}
