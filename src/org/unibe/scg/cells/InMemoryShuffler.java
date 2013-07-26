package org.unibe.scg.cells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

/** For running in memory, acts as both as sink and source */
public class InMemoryShuffler<T> implements CellSink<T>, CellSource<T> {
	final private List<Cell<T>> store = new ArrayList<>();

	InMemoryShuffler() {} // Don't subclass

	/** Return an instance of a shuffler */
	public static <T> InMemoryShuffler<T> getInstance() {
		return new InMemoryShuffler<>();
	}

	@Override
	public void close() {
		Collections.sort(store);
	}

	@Override
	public void write(Cell<T> cell) {
		store.add(cell);
	}

	@Override
	public Iterator<Iterable<Cell<T>>> iterator() {
		if (store.isEmpty()) {
			return Iterators.emptyIterator();
		}

		ImmutableList.Builder<Iterable<Cell<T>>> ret = ImmutableList.builder();

		ImmutableList.Builder<Cell<T>> partition = ImmutableList.builder();
		Cell<T> last = null;

		for (Cell<T> c : store) {
			if ((last != null) && (!c.getRowKey().equals(last.getRowKey()))) {
				ret.add(partition.build());
				partition = ImmutableList.builder();
				last = null;
			}

			if (!c.equals(last)) {
				partition.add(c);
			}

			last = c;
		}

		ret.add(partition.build());

		return ret.build().iterator();
	}
}
