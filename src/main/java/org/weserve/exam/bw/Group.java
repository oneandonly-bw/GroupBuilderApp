package org.weserve.exam.bw;

import java.util.HashSet;
import java.util.List;

public class Group {
	

	final private List<String> logStrings;
	final private HashSet<String> changes;
	
	public Group(List<String> logStrings, HashSet<String> changes) {
		this.logStrings = logStrings;
		this.changes = changes;
	}		
	
	public String getAsFormatedString() {
		
		String logs = String.join("\n", logStrings);
		String changed = "The changing word was: " + String.join(", ", changes);
		
		return logs + "\n" + changed + "\n";
	}

}
