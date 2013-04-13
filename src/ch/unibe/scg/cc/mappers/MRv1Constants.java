package ch.unibe.scg.cc.mappers;

/** Values were extracted from JobConf class. */
public interface MRv1Constants {
	public static final String ACLS_ENABLED = "mapred.acls.enabled";
	public static final String CHILD_ENV = "mapred.child.env";
	public static final String CHILD_JAVA_OPTS = "mapred.child.java.opts";
	public static final String CHILD_ULIMIT = "mapred.child.ulimit";
	public static final String COMBINER_CLASS = "mapred.combiner.class";
	public static final String COMPRESS_MAP_OUTPUT = "mapred.compress.map.output";
	public static final String INPUT_FORMAT_CLASS = "mapred.input.format.class";
	public static final String JAR = "mapred.jar";
	public static final String JOB_MAP_MEMORY_MB = "mapred.job.map.memory.mb";
	public static final String JOB_NAME = "mapred.job.name";
	public static final String JOB_PRIORITY = "mapred.job.priority";
	public static final String JOB_QUEUE_NAME = "mapred.job.queue.name";
	public static final String JOB_REDUCE_MEMORY_MB = "mapred.job.reduce.memory.mb";
	public static final String JOB_REUSE_JVM_NUM_TASKS = "mapred.job.reuse.jvm.num.tasks";
	public static final String LOCAL_DIR = "mapred.local.dir";
	public static final String MAP_CHILD_ENV = "mapred.map.child.env";
	public static final String MAP_CHILD_JAVA_OPTS = "mapred.map.child.java.opts";
	public static final String MAP_CHILD_LOG_LEVEL = "mapred.map.child.log.level";
	public static final String MAP_CHILD_ULIMIT = "mapred.map.child.ulimit";
	public static final String MAP_MAX_ATTEMPTS = "mapred.map.max.attempts";
	public static final String MAP_OUTPUT_COMPRESSION_CODEC = "mapred.map.output.compression.codec";
	public static final String MAP_RUNNER_CLASS = "mapred.map.runner.class";
	public static final String MAP_TASK_DEBUG_SCRIPT = "mapred.map.task.debug.script";
	public static final String MAP_TASKS = "mapred.map.tasks";
	public static final String MAP_TASKS_SPECULATIVE_EXECUTION = "mapred.map.tasks.speculative.execution";
	public static final String MAPOUTPUT_KEY_CLASS = "mapred.mapoutput.key.class";
	public static final String MAPOUTPUT_VALUE_CLASS = "mapred.mapoutput.value.class";
	public static final String MAPPER_CLASS = "mapred.mapper.class";
	public static final String MAPPER_NEW_API = "mapred.mapper.new-api";
	public static final String MAX_MAP_FAILURES_PERCENT = "mapred.max.map.failures.percent";
	public static final String MAX_REDUCE_FAILURES_PERCENT = "mapred.max.reduce.failures.percent";
	public static final String MAX_TRACKER_FAILURES = "mapred.max.tracker.failures";
	public static final String OUTPUT_COMMITTER_CLASS = "mapred.output.committer.class";
	public static final String OUTPUT_FORMAT_CLASS = "mapred.output.format.class";
	public static final String OUTPUT_KEY_CLASS = "mapred.output.key.class";
	public static final String OUTPUT_KEY_COMPARATOR_CLASS = "mapred.output.key.comparator.class";
	public static final String OUTPUT_VALUE_CLASS = "mapred.output.value.class";
	public static final String OUTPUT_VALUE_GROUPFN_CLASS = "mapred.output.value.groupfn.class";
	public static final String PARTITIONER_CLASS = "mapred.partitioner.class";
	public static final String REDUCE_CHILD_ENV = "mapred.reduce.child.env";
	public static final String REDUCE_CHILD_JAVA_OPTS = "mapred.reduce.child.java.opts";
	public static final String REDUCE_CHILD_LOG_LEVEL = "mapred.reduce.child.log.level";
	public static final String REDUCE_CHILD_ULIMIT = "mapred.reduce.child.ulimit";
	public static final String REDUCE_MAX_ATTEMPTS = "mapred.reduce.max.attempts";
	public static final String REDUCE_TASK_DEBUG_SCRIPT = "mapred.reduce.task.debug.script";
	public static final String REDUCE_TASKS = "mapred.reduce.tasks";
	public static final String REDUCE_TASKS_SPECULATIVE_EXECUTION = "mapred.reduce.tasks.speculative.execution";
	public static final String REDUCER_CLASS = "mapred.reducer.class";
	public static final String REDUCER_NEW_API = "mapred.reducer.new-api";
	public static final String TASK_DEFAULT_MAXVMEM = "mapred.task.default.maxvmem";
	public static final String TASK_LIMIT_MAXVMEM = "mapred.task.limit.maxvmem";
	public static final String TASK_MAXPMEM = "mapred.task.maxpmem";
	public static final String TASK_MAXVMEM = "mapred.task.maxvmem";
	public static final String TASK_PROFILE = "mapred.task.profile";
	public static final String TASK_PROFILE_MAPS = "mapred.task.profile.maps";
	public static final String TASK_PROFILE_PARAMS = "mapred.task.profile.params";
	public static final String TASK_PROFILE_REDUCES = "mapred.task.profile.reduces";
	public static final String TEXT_KEY_COMPARATOR_OPTIONS = "mapred.text.key.comparator.options";
	public static final String TEXT_KEY_PARTITIONER_OPTIONS = "mapred.text.key.partitioner.options";
	public static final String WORKING_DIR = "mapred.working.dir";
}
