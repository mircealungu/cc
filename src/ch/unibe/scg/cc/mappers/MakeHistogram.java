package ch.unibe.scg.cc.mappers;

import java.io.IOException;
import java.util.NavigableMap;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import ch.unibe.scg.cc.WrappedRuntimeException;

import com.google.common.base.Optional;

/** Compute distribution of commonness of snippets. */
public class MakeHistogram implements Runnable {
	static final String OUT_DIR = "/tmp/histogram";
	final MapReduceLauncher launcher;
	final Provider<Scan> scanProvider;

	@Inject
	MakeHistogram(MapReduceLauncher launcher, Provider<Scan> scanProvider) {
		this.launcher = launcher;
		this.scanProvider = scanProvider;
	}

	/**
	 * INPUT:<br>
	 *
	 * <pre>
	 * FAC1 --> { [F1|2] , [F2|3] , [F3|8] }
	 * FAC2 --> { [F1|3] , [F3|9] }
	 * </pre>
	 *
	 * OUTPUT:<br>
	 *
	 * <pre>
	 * 3 --> 1
	 * 2 --> 1
	 * </pre>
	 */
	static class MakeHistogramMapper extends GuiceTableMapper<IntWritable, LongWritable> {
		/** receives rows from htable snippet2function */
		@SuppressWarnings("unchecked")
		@Override
		public void map(ImmutableBytesWritable uselessKey, Result value,
				@SuppressWarnings("rawtypes") org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException,
				InterruptedException {
			NavigableMap<byte[], byte[]> familyMap = value.getFamilyMap(Constants.FAMILY);
			context.write(new IntWritable(familyMap.size()), new LongWritable(1L));
		}
	}

	static class MakeHistogramReducer extends GuiceReducer<IntWritable, LongWritable, IntWritable, LongWritable> {
		@Override
		public void reduce(IntWritable columnCount, Iterable<LongWritable> values, Context context) throws IOException,
				InterruptedException {
			long sum = 0;
			for (LongWritable val : values) {
				sum += val.get();
			}
			context.write(columnCount, new LongWritable(sum));
		}
	}

	@Override
	public void run() {
		try {
			FileSystem.get(new Configuration()).delete(new Path(OUT_DIR), true);

			Scan scan = scanProvider.get();
			scan.addFamily(Constants.FAMILY);

			Configuration config = new Configuration();
			config.set(MRJobConfig.MAP_LOG_LEVEL, "DEBUG");
			config.set(MRJobConfig.NUM_REDUCES, "1");
			// TODO test that
			config.set(MRJobConfig.REDUCE_MERGE_INMEM_THRESHOLD, "0");
			config.set(MRJobConfig.REDUCE_MEMTOMEM_ENABLED, "true");
			config.set(MRJobConfig.IO_SORT_MB, "512");
			config.set(MRJobConfig.IO_SORT_FACTOR, "100");
			config.set(MRJobConfig.JOB_UBERTASK_ENABLE, "true");
			// set to 1 if unsure TODO: check max mem allocation if only 1 jvm
			config.set(MRJobConfig.JVM_NUMTASKS_TORUN, "-1");
			config.set(MRJobConfig.TASK_TIMEOUT, "86400000");
			config.set(MRJobConfig.MAP_MEMORY_MB, "1536");
			config.set(MRJobConfig.MAP_JAVA_OPTS, "-Xmx1024M");
			config.set(MRJobConfig.REDUCE_MEMORY_MB, "3072");
			config.set(MRJobConfig.REDUCE_JAVA_OPTS, "-Xmx2560M");
			config.set(FileOutputFormat.OUTDIR, OUT_DIR);
			config.setClass(MRJobConfig.OUTPUT_FORMAT_CLASS_ATTR, TextOutputFormat.class, OutputFormat.class);
			config.setClass(MRJobConfig.COMBINE_CLASS_ATTR, MakeHistogramReducer.class, Reducer.class);
			config.set(Constants.GUICE_CUSTOM_MODULES_ANNOTATION_STRING, HBaseModule.class.getName());

			launcher.launchMapReduceJob(MakeHistogram.class.getName() + " Job", config,
					Optional.of("snippet2function"), Optional.<String> absent(), Optional.of(scan),
					MakeHistogramMapper.class.getName(), Optional.of(MakeHistogramReducer.class.getName()),
					IntWritable.class, LongWritable.class);
		} catch (IOException | ClassNotFoundException e) {
			throw new WrappedRuntimeException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return; // Exit.
		}
	}
}
