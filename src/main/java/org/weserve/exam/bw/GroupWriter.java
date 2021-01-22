package org.weserve.exam.bw;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Objects;

public class GroupWriter {
	
	final private FileWriter out;
	
	/**
	 * Creates GroupWriter 
	 * 
	 * @param fileName the fully qualified name of file to write to
	 * 
	 * @throws NullPointerException if given file name is null
	 * @throws IllegalArgumentException if given file name is empty string
	 * @throws FileAlreadyExistsException if the named file exists or it is a directory 
	 * @throws IOException if the named file does not exist, but cannot be 
	 *                     created, or cannot be opened for any other reason
	 */
	public GroupWriter (String fileName) 
	throws NullPointerException, IllegalArgumentException, FileAlreadyExistsException, IOException {
	
		validateFileForWrite (fileName);
		out = new FileWriter(fileName);
	}
	
	/**
	 * Closes the writer
	 */
	public void close() {
		try {
			out.close();
		} catch (Exception e) {};
	}
	
	/**
	 * Writes given group to a file
	 * 
	 * @param group the group to write
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public void write(Group group) throws IOException {
		out.write(group.getAsFormatedString());
	}
	
	
	/*
	 * Validates given file for write
	 * 
	 * @param fileName the fully qualified name of the file
	 * 
	 * @throws NullPointerException if file name is null
	 * @throws IllegalArgumentException if file name is empty string
	 * @throws FileAlreadyExistsException if file or directory with given name already exists
	 */
	private static void validateFileForWrite(String fileName)
	throws NullPointerException, IllegalArgumentException, FileAlreadyExistsException {

		Objects.requireNonNull(fileName, "file name cannot be null");

		if (fileName.isEmpty())
			throw new IllegalArgumentException("file name cannot be empty");

		File file = new File(fileName);

		if (file.isDirectory())
			throw new FileAlreadyExistsException("file denoted by the file name is adirectory " + fileName);

		if (file.exists())
			throw new FileAlreadyExistsException("file  already exists " + fileName);

		}

}
