package ch.unibe.scg.cc.activerecord;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import ch.unibe.scg.cc.StandardHasher;
import ch.unibe.scg.cc.lines.StringOfLines;

import postmatchphase.HashFactLoader;


public class HashFact {
	
	private static final String FILE_PATH = "filePath";
	private static final String PROJECT_NAME = "projectName";
	final HTable facts;

	@Inject
	public HashFact(@Named("facts") HTable  facts, StandardHasher standardHasher) {
		this.facts = facts;
		this.standardHasher = standardHasher;
	}

	byte[] hash;
	Project project;
	Function function;
	Location location;
	int type;
	
	StandardHasher standardHasher;

	

	
	

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void save() {
        Put put = new Put(getPrimaryKey());
        project.save(put);
        function.save(put);
        location.save(put);
        try {
			facts.put(put);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected byte[] getPrimaryKey() {
		return Bytes.add(hash, this.getSourceIdentifier());
	}

	byte[] getSourceIdentifier() {
		String value = getProject().getName() + getFunction().getFile_path() + getLocation().getFirstLine();
		return standardHasher.hash(value);
	}


}
