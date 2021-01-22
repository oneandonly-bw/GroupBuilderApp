package org.weserve.exam.bw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import static java.util.stream.Collectors.toList;


public class FileLoader {

	final private List<Record> records;
	final private int maxWordCount;
	
	private FileLoader (List<Record> list, int maxWordCount) {
		this.records = list;
		this.maxWordCount = maxWordCount;
	}

	/**
	 * Returns list of records loaded by this loader
	 * 
	 * @return list of records loaded by this loader
	 */
	public List<Record> getRecords() {
		return records;
	}
	
	/**
	 * Returns number of words in longest record loaded by the loader
	 * 
	 * @return number of words in longest record 
	 */
	public int getMaxWordsCount() {
		return maxWordCount;
	}

	/**
	 * Loads the file defined by given file name
	 * 
	 * @param fileName the fully qualified file name of file to load data from
	 * 
	 * @return file loader
	 * 
	 * @throws NullPointerException if given file name is null
	 * @throws IllegalArgumentException if given file name is empty string
	 * @throws FileNotFoundException if file with given name not found
	 * @throws IOException if an I/O error occurs
	 */
	public static FileLoader load (String fileName) 
	throws NullPointerException, IllegalArgumentException, FileNotFoundException, IOException {

		validateFileForRead(fileName);
							
		List<Record> records = Files.readAllLines(Paths.get(fileName)).
				stream().
				filter(line -> !line.isEmpty()).
				map(line -> Record.getInstance(line)). 
				filter(record -> record !=  null).
				collect(toList());
		
		int maxWordCount = records.
				stream().
				mapToInt(record -> record.getWordsCount()).
				max().getAsInt();
		
		return new FileLoader(records, maxWordCount);
					
	}

	/*
	 * Tests whether the file denoted by the given file is accessible for read
	 * 
	 * @param fileName the fully qualified file name of file to read from
	 * 
	 * @throws NullPointerException if given file name is null
	 * @throws IllegalArgumentException if given file name is empty string
	 * @throws FileNotFoundException if file with given name not found
	 * @throws IOException if file with given file name cannot be read
	 */
	private static void validateFileForRead(String fileName) 
	throws NullPointerException, IllegalArgumentException,  FileNotFoundException, IOException		{

		Objects.requireNonNull(fileName, "file name cannot be null");

		if (fileName.isEmpty())
			throw new IllegalArgumentException("file name cannot be empty");

		File file = new File(fileName);

		if (!file.exists() || file.isDirectory())
			throw new FileNotFoundException("file not found: " + fileName);

		if (!file.canRead())
			throw new IOException("cannot read file: " + fileName);

	}

}
