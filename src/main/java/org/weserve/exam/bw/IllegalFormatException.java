package org.weserve.exam.bw;

public class IllegalFormatException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
	 public IllegalFormatException() { 
		 super();
	 }
	 
	 public IllegalFormatException(String message) {
		 super(message);
	 }

}
