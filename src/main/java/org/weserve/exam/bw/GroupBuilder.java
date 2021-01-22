package org.weserve.exam.bw;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class GroupBuilder {
	
	//final private HashMap<String, List<String>> map = new HashMap<String, List<String>>();
	final private HashMap<String, List<Record>> map = new HashMap<String, List<Record>>();
	final private HashSet<String> changes = new HashSet<String>();
	
	public boolean tryToAddRecord(Record record, int pos) {
		
		String word = record.getWordFromBodyAt(pos);
		
		if (word == null)
			return false;
		
		String key = record.getBodyAsString();
		
	//	List<String> list = map.get(key);
		List<Record> list = map.get(key);
		if (list == null) {
		//	list = new ArrayList<String>();
			list = new ArrayList<Record>();
			map.put(key, list);
		}
		
		//list.add(record.getAsString());
		list.add(record);
		changes.add(word);
		return true;
	};
	

	public int getChangeCount() {
		return changes.size();
	};
	
	/**
	 * Builds groups from records accumulated by the builder.
	 * Note that equal records (which differ only by time stamp),
	 * can not belong to the same group. Hence, groups that contain
	 * equal records are split to several groups, which contain 
	 * all combinations of similar records, but non of the combinations 
	 * contain equal records
	 * <pre>
	 * For example: 
	 *  the  group
	 * 	 01-01-2012 19:45:00 Naomi is getting into the car 
	 *	 01-01-2012 19:45:01 Naomi is getting into the car
	 *	 03-01-2012 11:22:40 Mike is getting into the car
	 *
	 *  split to two groups
	 *	 01-01-2012 19:45:00 Naomi is getting into the car
	 *	 03-01-2012 11:22:40 Mike is getting into the car 
	 *	 The changing word was: Naomi, Mike
	 *		
     *	01-01-2012 19:45:01 Naomi is getting into the car
     *	03-01-2012 11:22:40 Mike is getting into the car 
	 *	The changing word was: Naomi, Mike;
	 *</pre>
	 *  
	 * @return list of groups or null if no group found
	 */
	public List<Group> build() {
		
		/*
		 * The group should contain at least two members,
		 * so the number of changes should be at least 2 
		 */
		if (changes.size() < 2)
			return null;
		
		CombinationGenerator generator = new CombinationGenerator(map.values());
		List<Group> groups =  new ArrayList<Group>();
		
		List<String> list = null;
		
		while ((list = generator.getNextCombination()) !=  null) {
			Group group = new Group(list, changes);
			groups.add(group);
		}
			
		return groups;	
	};
	
	
	private static class CombinationGenerator {

	//	final private List<List<String>> buckets;
		final private List<List<Record>> buckets;
		final private int[] indices;

		private boolean hasNext = true;

		
		private CombinationGenerator(Collection<List<Record>> buckets) {
			//this.buckets = new ArrayList<List<String>>(buckets);
			this.buckets = new ArrayList<List<Record>>(buckets);
			this.indices = new int[buckets.size()];
		}
		
//		private CombinationGenerator(Collection<List<String>> buckets) {
//			this.buckets = new ArrayList<List<String>>(buckets);
//			this.indices = new int[buckets.size()];
//		}

		private List<String> getNextCombination() {

			if (!hasNext)
				return null;
			
			List<String> result = getGroupMembers();
			hasNext = increment(0);

			return result;
		}

		private boolean increment(int indexOfBucket) {

			int size = buckets.get(indexOfBucket).size();

			if (indices[indexOfBucket] < size - 1) {
				indices[indexOfBucket]++;
				return true;
			}

			if (indexOfBucket == indices.length - 1)
				return false;

			indices[indexOfBucket] = 0;
			
			return increment(++indexOfBucket);
		}
		
		
		private List<String> getGroupMembers() {
			
			TreeSet<Record> records = new TreeSet<Record>();
				
			// Create list of records sorted by time stamp
			for (int i = 0; i < indices.length; i++) {
				List<Record> bucket = buckets.get(i);
				Record record = bucket.get(indices[i]);
				records.add(record);
			}

			return records.stream().map(r -> r.getAsString()).collect(toList());

		}

	}
}
