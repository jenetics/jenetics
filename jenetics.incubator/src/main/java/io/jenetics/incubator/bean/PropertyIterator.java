package io.jenetics.incubator.bean;

import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Preorder property iterator.
 */
final class PropertyIterator implements Iterator<Property> {

    private final Property.Reader reader;

    private final Deque<Iterator<Property>> deque = new ArrayDeque<>();

	PropertyIterator(
		final String basePath,
		final Object root,
        final Property.Reader reader
    ) {
        this.reader = requireNonNull(reader);

        deque.push(reader.read(basePath, root).iterator());
    }

    @Override
    public boolean hasNext() {
        final Iterator<Property> peek = deque.peek();
        return peek != null && peek.hasNext();
    }

    @Override
    public Property next() {
        final Iterator<Property> it = deque.peek();
        if (it == null) {
            throw new NoSuchElementException("No next element.");
        }

        final Property node = it.next();
        if (!it.hasNext()) {
            deque.pop();
        }

        final Iterator<Property> children = reader
			.read(node.path(), node.value())
			.iterator();

		if (children.hasNext()) {
            deque.push(children);
        }

        return node;
    }

}
