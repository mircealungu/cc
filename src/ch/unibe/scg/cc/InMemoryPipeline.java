package ch.unibe.scg.cc;

import java.io.IOException;

import javax.inject.Provider;

import com.google.common.collect.Iterables;

// TODO: Run multithreaded
class InMemoryPipeline<IN, OUT> implements Pipeline<IN, OUT> {
	final private CellSource<IN> pipeSrc;
	final private CellSink<OUT> pipeSink;

	InMemoryPipeline(CellSource<IN> pipeSrc, CellSink<OUT> pipeSink) {
		this.pipeSrc = pipeSrc;
		this.pipeSink = pipeSink;
	}

	@Override
	public MappablePipeline<IN, OUT> influx(Codec<IN> c) {
		return new InMemoryMappablePipeline<>(pipeSrc, c);
	}

	private class InMemoryMappablePipeline<I> implements MappablePipeline<I, OUT> {
		final private CellSource<I> src;
		final private Codec<I> srcCodec;

		InMemoryMappablePipeline(CellSource<I> src, Codec<I> srcCodec) {
			this.src = src;
			this.srcCodec = srcCodec;
		}

		@Override
		public <E> ShuffleablePipeline<E, OUT> mapper(Provider<? extends Mapper<I, E>> mapper) {
			return new InMemoryShuffleablePipeline<>(src, srcCodec, mapper);
		}

		@Override
		public void efflux(Provider<? extends Mapper<I, OUT>> m, Codec<OUT> sinkCodec) {
			run(src, srcCodec, m, pipeSink, sinkCodec);
		}
	}

	private class InMemoryShuffleablePipeline<I, E> implements ShuffleablePipeline<E, OUT> {
		final private CellSource<I> src;
		final private Codec<I> srcCodec;
		final private Provider<? extends Mapper<I, E>> mapper;

		InMemoryShuffleablePipeline(CellSource<I> src, Codec<I> srcCodec, Provider<? extends Mapper<I, E>> mapper) {
			this.src = src;
			this.srcCodec = srcCodec;
			this.mapper = mapper;
		}

		@Override
		public MappablePipeline<E, OUT> shuffle(Codec<E> sinkCodec) {
			InMemoryShuffler<E> shuffler = new InMemoryShuffler<>();
			run(src, srcCodec, mapper, shuffler, sinkCodec);
			return new InMemoryMappablePipeline<>(shuffler, sinkCodec);
		}
	}

	private static <I, E> void run(CellSource<I> src, Codec<I> srcCodec, Provider<? extends Mapper<I, E>> mapper,
			CellSink<E> sink, Codec<E> sinkCodec) {
		try (Mapper<I, E> m = mapper.get()) {
			for (Iterable<Cell<I>> part : src.partitions()) {
				Iterable<I> decoded = Codecs.decode(part, srcCodec);
				// In memory, since all iterables are backed by arrays, this is safe.
				m.map(Iterables.get(decoded, 0), decoded, Codecs.encode(sink, sinkCodec));
			}
			// In memory, there's very little we should do. We certainly won't restart maps.
		} catch (EncodingException e) {
			throw new RuntimeException("Mapper " + mapper + " failed", e.getCause());
		} catch (IOException e) {
			throw new RuntimeException("Mapper " + mapper + " failed", e);
		}
	}
}