package ch.unibe.scg.cc;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.inject.Inject;

import ch.unibe.scg.cc.Annotations.PopularSnippets;
import ch.unibe.scg.cc.Annotations.PopularSnippetsThreshold;
import ch.unibe.scg.cc.Protos.Clone;
import ch.unibe.scg.cc.Protos.Snippet;
import ch.unibe.scg.cells.Mapper;
import ch.unibe.scg.cells.OneShotIterable;
import ch.unibe.scg.cells.Sink;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.BaseEncoding;

/** Filter phase of the pipeline */
class Function2RoughCloner implements Mapper<Snippet, Clone> {
	final static private long serialVersionUID = 1L;
	final static private Logger logger = Logger.getLogger(Function2RoughCloner.class.getName());

	final private Sink<Snippet> popularSnippets;
	final private int popularSnippetThreshold;

	@Inject
	Function2RoughCloner(@PopularSnippets Sink<Snippet> popularSnippets,
			@PopularSnippetsThreshold int popularSnippetThreshold) {
		this.popularSnippets = popularSnippets;
		this.popularSnippetThreshold = popularSnippetThreshold;
	}

	@Override
	public void map(Snippet first, OneShotIterable<Snippet> rowIterable, Sink<Clone> function2RoughClones)
			throws IOException, InterruptedException {
		// rowIterable is not guaranteed to be iterable more than once, so copy.
		Collection<Snippet> row = ImmutableList.copyOf(rowIterable);
		rowIterable = null; // Don't touch!

		logger.finer("Make rough clone from snippet "
				+ BaseEncoding.base16().encode(Iterables.get(row, 0).getHash().toByteArray()).substring(0, 6));

		if (row.size() <= 1) {
			return; // prevent processing non-recurring hashes
		}

		// special handling of popular snippets
		if (row.size() >= popularSnippetThreshold) {
			for (Snippet loc : row) {
				popularSnippets.write(loc);
			}
			return;
		}

		for (Snippet thisSnip : row) {
			for (Snippet thatSnip : row) {
				// save only half of the functions as row-key
				// full table gets reconstructed in MakeSnippet2FineClones
				// This *must* be the same as in CloneExpander.
				if (thisSnip.getFunction().asReadOnlyByteBuffer()
							.compareTo(thatSnip.getFunction().asReadOnlyByteBuffer()) < 0) {
					function2RoughClones.write(
						Clone.newBuilder().setThisSnippet(thisSnip).setThatSnippet(thatSnip).build());
				}
			}
		}
	}

	@Override
	public void close() throws IOException {
		if (popularSnippets != null) {
			popularSnippets.close();
		}
	}
}
