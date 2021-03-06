package ch.unibe.scg.cells.hadoop;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.unibe.scg.cells.Cell;
import ch.unibe.scg.cells.CellSource;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.protobuf.ByteString;

/** Testing {@link HBaseCellSink} */
@SuppressWarnings("javadoc")
public final class HBaseCellSinkTest {
	private Table<Void> testTable;
	private static final ByteString FAMILY = ByteString.copyFromUtf8("d");

	final private TableAdmin admin = Guice.createInjector(new UnibeModule()).getInstance(
			TableAdmin.class);

	@Before
	public void createTable() throws IOException {
		testTable = admin.createTemporaryTable(FAMILY);
	}

	@After
	public void deleteTable() throws IOException {
		testTable.close();
	}

	/** Testing {@link HBaseCellSink#write(Cell)}. */
	@Test
	public void writeTest() throws IOException {
		final Injector i = Guice.createInjector(new UnibeModule(),
				new HBaseStorage(), new HBaseTableModule<>(testTable.getTableName(), FAMILY));
		ByteString key = ByteString.copyFromUtf8("123");
		ByteString col = ByteString.copyFromUtf8("abc");
		Cell<Void> cell = Cell.<Void> make(key, col, ByteString.EMPTY);

		try (HBaseCellLookupTable<Void> lookup = i.getInstance(HBaseCellLookupTable.class)) {
			Iterable<Cell<Void>> rowBeforeWrite = lookup.readRow(key);
			assertTrue(rowBeforeWrite.toString(), Iterables.isEmpty(rowBeforeWrite));

			try (HBaseCellSink<Void> cellSink = i.getInstance(HBaseCellSink.class)) {
				cellSink.write(cell);
			}

			Iterable<Cell<Void>> rowAfterWrite = lookup.readRow(key);
			assertFalse(rowAfterWrite.toString(), Iterables.isEmpty(rowAfterWrite));
		}

		try (CellSource<Void> src = i.getInstance(HBaseCellSource.class)) {
			Iterable<Cell<Void>> row = Iterables.getOnlyElement(src);
			Cell<Void> actual = Iterables.getOnlyElement(row);
			assertThat(actual, is(cell));
		}
	}
}
