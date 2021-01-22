package org.weserve.exam.bw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Record implements Comparable <Record>{
	
	final private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	/* The word delimiter */
	final private static String delims = "\\s+";
			
	/*
	 * The log entry represented as array of words.
	 * Keeping data as array of words simplifies manipulation 
	 * with entry (remove word, count words, etc...)
	 */
	final private String[] tokens;
	
	final private long timestamp;
	
	
	private Record(String[] tokens, long timestamp) {
		this.tokens = tokens;
		this.timestamp = timestamp;
	}
	
	/**
	 * Converts a given string to a record
	 * 
	 * @param string the string to convert to a record
	 * 
	 * @return new instance of a record initialized with given 
	 *         string or null if given string is illegal
	 */
	public static Record getInstance(String string) {

		if (string == null || string.isEmpty())
			return null;

		String[] tokens = string.split(delims);
		
		if (tokens.length <= 2)
			return null;
		
		
		try {
			
			Date date = dateFormat.parse(String.join(" ", tokens[0], tokens[1]));
			return new Record(tokens, date.getTime());
			
		} catch (ParseException e) {
			return null;
		}		
	}
	
	/**
	 * Returns a string representation of the record 
	 * 
	 * @return string representation of the record
	 */
	public String getAsString() {
		return getAsString(-1, false);
	}
	
	
	/**
	 * Returns a string representation of the record without time stamp part 
	 * 
	 * @return string representation of the record without time stamp part
	 */
	public String getBodyAsString() {
		return getAsString(-1, true);
	}
	
	/**
	 * Returns a string representation of the record body without the word pointed 
	 * by the given position. The position counting starts for 0. 
	 * The record body is a part of a record without time stamp
	 * If the given position is greater then number of words in the record body then 
	 * representation of full record body is returned
	 * 
	 * @param pos the position of word to remove
	 * 
	 * @return string representing the record without word in the given position
	 * 
	 * @throws IllegalArgumentException if given position is negative
	 */
	public String removeWordFromBodyAt (int pos) throws IllegalArgumentException {
		
		if (pos < 0)
			throw new IllegalArgumentException("position cannot be negative");
		
		return getAsString (pos, true);
	}
	
	/**
	 * Returns the word at the specified position in record body
	 * The record body is part of record without time stamp
	 * The position counting starts from 0.
	 * 
	 * @param pos the position
	 * 
	 * @return word in the specified position or null if position 
	 *         greater then number of words in the record
	 * 
	 * @throws IllegalArgumentException if given position is negative
	 */
	public String getWordFromBodyAt (int pos) {
		
		if (pos < 0)
			throw new IllegalArgumentException("position cannot be negative");
		
		int index = pos + 2;
		
		if (index >= tokens.length)
			return null;
		
		return tokens[index];
		
	}
	
	/**
	 * Returns the number of words in the record
	 * 
	 * @return number of word in the record
	 */
	public int getWordsCount() {
		return tokens.length;
	}
	
	/**
	 * Implementation of Comparable interface.
	 * Used to sort records by time stamp
	 */
	@Override
	public int compareTo(Record record) {
		return (int) (this.timestamp - record.timestamp) ;
	}
	
	
	/*
	 * Returns string representation of the record according to given parameters
	 * 
	 * @param pos the position of word to exclude from the resulting string
	 * @param skipTimestamp if set to true then time stamp will be excluded 
	 *        from the resulting string
	 * 
	 * @return string representation of the record
	 */
	private String getAsString(int pos, boolean skipTimestamp) {

		StringBuilder sb = new StringBuilder();

		int start = skipTimestamp ? 2 : 0;
		int realPos = pos + start;
		
		for (int i = start; i < tokens.length; i++) {

			if (i != realPos ) {
				sb.append(tokens[i]);
				if (i + 1 < tokens.length)
					sb.append(' ');
			}
		}

		return sb.toString();
	}



}
