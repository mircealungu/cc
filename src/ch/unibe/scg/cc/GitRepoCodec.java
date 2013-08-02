package ch.unibe.scg.cc;

import java.io.IOException;


import ch.unibe.scg.cc.Protos.GitRepo;
import ch.unibe.scg.cells.Cell;
import ch.unibe.scg.cells.Codec;

import com.google.protobuf.ByteString;

class GitRepoCodec implements Codec<GitRepo> {
	@Override
	public Cell<GitRepo> encode(GitRepo s) {
		return Cell.make(ByteString.copyFromUtf8(s.getProjectName()), ByteString.EMPTY, s.toByteString());
	}

	@Override
	public GitRepo decode(Cell<GitRepo> encoded) throws IOException {
		return GitRepo.parseFrom(encoded.getCellContents());
	}
}
