package ch.unibe.scg.cc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;

import org.junit.Test;

import ch.unibe.scg.cc.Protos.Clone;
import ch.unibe.scg.cc.Protos.Snippet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.protobuf.ByteString;

/** Test {@link CloneExpander} */
public final class CloneExpanderTest {
	/** Test {@link CloneExpander#expandClones} */
	@Test
	public void testExpandClones() {
		// Construct something like h1fun2@3 h2fun2@5 h3fun3@1 h4fun3@7
		Snippet thisFunction = Snippet.newBuilder().setFunction(ByteString.copyFromUtf8("fun1")).build();
		ImmutableList<Clone> toExpand = ImmutableList.of(
				Clone.newBuilder()
					.setThisSnippet(Snippet.newBuilder(thisFunction)
							.setHash(ByteString.copyFromUtf8("h1"))
							.setPosition(2))
					.setThatSnippet(Snippet.newBuilder()
							.setFunction(ByteString.copyFromUtf8("fun2"))
							.setHash(ByteString.copyFromUtf8("h1"))
							.setPosition(2))
					.build(),

					Clone.newBuilder()
					.setThisSnippet(Snippet.newBuilder(thisFunction)
							.setHash(ByteString.copyFromUtf8("h2"))
							.setPosition(5))
					.setThatSnippet(Snippet.newBuilder()
							.setFunction(ByteString.copyFromUtf8("fun2"))
							.setHash(ByteString.copyFromUtf8("h2"))
							.setPosition(5))
					.build(),

					Clone.newBuilder()
					.setThisSnippet(Snippet.newBuilder(thisFunction)
							.setHash(ByteString.copyFromUtf8("h3"))
							.setPosition(1))
					.setThatSnippet(Snippet.newBuilder()
							.setFunction(ByteString.copyFromUtf8("fun3"))
							.setHash(ByteString.copyFromUtf8("h3"))
							.setPosition(1))
					.build(),

					Clone.newBuilder()
					.setThisSnippet(Snippet.newBuilder(thisFunction)
							.setHash(ByteString.copyFromUtf8("h4"))
							.setPosition(7))
					.setThatSnippet(Snippet.newBuilder()
							.setFunction(ByteString.copyFromUtf8("fun3"))
							.setHash(ByteString.copyFromUtf8("h1"))
							.setPosition(7))
					.build());

		// Construct maps for one popular snippet h7 that occurs in two places: fun2@6, fun1@2
		final Snippet fun2Loc = Snippet.newBuilder()
				.setHash(ByteString.copyFromUtf8("h7"))
				.setPosition(6)
				.setFunction(ByteString.copyFromUtf8("fun2")).build();
		final Snippet fun1Loc = Snippet.newBuilder()
				.setHash(ByteString.copyFromUtf8("h7"))
				.setPosition(1)
				.setFunction(ByteString.copyFromUtf8("fun1")).build();
		final ImmutableListMultimap<ByteBuffer, Snippet> snippet2Popular = ImmutableListMultimap.of(
				fun1Loc.getHash().asReadOnlyByteBuffer(), fun1Loc,
				fun2Loc.getHash().asReadOnlyByteBuffer(), fun2Loc);
		final ImmutableListMultimap<ByteBuffer, Snippet> function2Popular = ImmutableListMultimap.of(
				fun1Loc.getFunction().asReadOnlyByteBuffer(), fun1Loc,
				fun2Loc.getFunction().asReadOnlyByteBuffer(), fun2Loc);

		CloneExpander expander = new CloneExpander(new PopularSnippetMaps(null) {
			final private static long serialVersionUID = 1L;

			@Override public ImmutableMultimap<ByteBuffer, Snippet> getFunction2PopularSnippets() {
				return function2Popular;
			}

			@Override public ImmutableMultimap<ByteBuffer, Snippet> getSnippet2PopularSnippets() {
				return snippet2Popular;
			}
		});
		assertThat(expander.expandClones(toExpand).toString(), is(
				"[thisSnippet {\n  function: \"fun1\"\n  position: 1\n  length: 9\n}\n" +
				"thatSnippet {\n  function: \"fun2\"\n  position: 2\n  length: 8\n}\n, " +
				"thisSnippet {\n  function: \"fun1\"\n  position: 1\n  length: 11\n}\n" +
				"thatSnippet {\n  function: \"fun3\"\n  position: 1\n  length: 11\n}\n]"));
		}
}
