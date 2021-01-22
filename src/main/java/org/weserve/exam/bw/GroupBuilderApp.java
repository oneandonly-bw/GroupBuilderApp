package org.weserve.exam.bw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class GroupBuilderApp {

	public static void main(String[] args) {
		
		/*
		 * Validate arguments
		 */
		if (args.length != 2) {
			System.out.println("Invalid number of arguments");
			printUsage();
			return;
		}

		/*
		 * Load data
		 */
		FileLoader loader = null;

		try {
			loader = FileLoader.load(args[0]);
		} catch (Exception e) {
			System.err.println("FileLoder. Error loading input file: " + e.getMessage());
			return;
		}

		/*
		 * The main loop. In each interaction we remove one word
		 * from record body of every record loaded by loader and
		 * use result of removal as hash map key. The records
		 * that differ only by a single removed word have the same key (hashCode)
		 * and will be grouped.
		 */
		List<Group> groups = new ArrayList<Group>();
			
		for (int i = 0; i < loader.getMaxWordsCount(); i++) {
			List<Group> g = process(i, loader.getRecords());
			groups.addAll(g);
		}
		
	
		/*
		 * Write result to output file 
		 */
		GroupWriter writer = null;

		try {

			writer = new GroupWriter(args[1]);
						
			for (Group group : groups)
				writer.write(group);

		} catch (Exception e) {
			System.err.println("Error writing to output file: " + e.getMessage());

		} finally {
			if (writer != null)
				writer.close();
		}
	}

	private static void printUsage() {

		System.out.println("Usage: " + "GroupBuilder [input file name] [output file name]\n" + "Where\n"
				+ "\tinput file name is fully quilified name of input file\n"
				+ "\toutput file name is fully quilified name of output file\n\n"
				+ "If file name contains spaces the name should be encolsed by " + "quotes (\") ");

	}

	/*
	 * 
	 */
	private static List<Group> process(int pos, List<Record> records) {

		HashMap<String, GroupBuilder> map = new HashMap<>();
		
		for (int i = 0; i < records.size(); i++) {
			
			//get the record
			Record record = records.get(i);
			
			//get grouping key
			String key = record.removeWordFromBodyAt(pos);
			
			//try to get records grouped by the key
			GroupBuilder builder = map.get(key);
			
			//if no such key then create new entry
			if (builder == null) {
				builder = new GroupBuilder();
				map.put(key, builder);
			}

			//add member to a group
			builder.tryToAddRecord(record, pos);
		}

		//build groups
		List<Group> groups = new ArrayList<Group>();
		
		for (Entry<String, GroupBuilder> entry : map.entrySet()) {
			
			List<Group> list = entry.getValue().build();
			
			if (list !=  null)
			   groups.addAll(list);
		}

		return groups;
	}

}
