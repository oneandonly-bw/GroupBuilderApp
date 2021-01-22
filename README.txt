Assumptions 
--------------------------
 From the requirements document:
 "Your task is to write code that groups together similar sentences (sentences where only a single word differ between them)"
	
1. Since the document doesn't define terms "sentence"  and " similar" I assume the following:
   1.1. Sentence is an ordered sequence of words (strings) separated by whitespace separator and terminated by new line(\n).
   1.2. Similar sentences are those that differ in exactly one word, and this word occurs in both of them at the same position.
		 
   Example 1.1:  
	A B C and B C D are not similar sentences since there are two differences in two positions (0, 2)
	A B C and D B C are similar sentences since there is only one difference in position 0. 
			
 So, the first assumption is that two sentences with different order of words cannot be similar
	
2. The problem description doesn't clearly define the expected behavior of the program on a data set 
   containing identical records (differing only in time stamp). The existence of such identical records 
   is not ruled out by the description, so I had to assume the possibility is real. 
   This is a serious complication. 
  
   Example 2.1:
	time stamp #1 A B C
	time stamp #2 A B D
	time stamp #3 A B C
	time stamp #4 A B D

    Here we have two pairs of identical records. How to group them? It appears that we have to form 4 groups:
    Example 2.2
	Group 1:
		time stamp #1 A B C
		time stamp #2 A B D
		changing words: C, D
	Group 2:
		time stamp #1 A B C
		time stamp #4 A B D
		changing words: C, D
	Group 3:
		time stamp #2 A B D
		time stamp #3 A B C
		changing words: C, D
	Group 4:
		time stamp #3 A B C
		time stamp #4 A B D
		changing words: C, D

	In general, if we have many groups of identical records (after discarding times stamps), we might 
	have an exponential explosion of groups (in total, we may have n1*n2*n3*, etc groups, where n1, n2, etc 
	- the sizes of the sets of identical sentence of i-th kind). E.g. in the above example, we have n1=2, n2=2, 
	so in total, we have 4 groups. 
	It might very well be the case that such interpretation of conditions was not meant by the description, 
	but it's difficult to come up with a better interpretation - because we certainly CANNOT write in the result:
	
	Example 2.3 
		time stamp #1 A B C
		time stamp #2 A B D
		time stamp #3 A B C
		time stamp #4 A B D
		changing words: C, D, C, D
		
	The set of changing words is (probably) supposed to contain only unique words, not repeated words.
	 
	All examples in the description reinforce this view. Consequently, I had to implement a combinatoric 
	grouping mechanism, (Examples 2.1 and 2.2) which significantly complicates the code.
	
	
Overview of solution
--------------------

1. Algorithm
  
  The naive method would suggest comparing 1-st record with all other records, then 2-nd record with 
  records 3..N, and so on, which would lead to O(N^2) complexity (where N - the total number of records).
  
  To avoid quadratic complexity, the algorithm implements the following logic:
  
  1.1 read and parse all records (putting time stamp aside for the time being: time stamp doesn't affect the grouping, 
      it affects just the order of sentences in output).
  
  2.1 calculate the maximal size of the record (in words).
  
  3.1 for each index i (in range from 0 to max number of words) we retrieve the list of words from each 
      record with i-th word left out (discarded). Observation: if two records differ only in a single i-th word, 
      they become identical upon removal of i-th word. 
      Let's call a sentence with i-th word removed an i-th sub-sentence. This simplifies the problem: now we have 
      to just search for identical i-th sub-sentences, grouping them together. If some sub-sentence is the only element 
      in its group, then it has no similar records within this iteration (it may still have them in a different (j-th iteration), 
      but not now). But if the group contains 2 or more elements, then it may (or may not) contain similar sentences - it all 
      depends on what word was removed from each of these sentences. 
     
   Example 1:
	time stamp #1 A B C
	time stamp #2 A B D

  Here, iteration 0 doesn't find anything similar, and neither iteration 1, but iteration 2, after removing word at 2-nd 
  position (0-based), leaves us with 2 identical records A B. They form a group because each of them has a different word 
  removed: the first one removed C, the second - D. So C, D are "changing words" in a group of 2 phrases.
  
   Example 2: 
    time stamp #1 A B C
    time stamp #2 A B C
    
  Here, no matter which word we remove, we have 2 identical sub-sentence - but in each case, the removed word is also the same, 
  so we don't count it as a group
  
   Example 3 (where things become more interesting):
	time stamp #1 A B C
	time stamp #2 A B C
	time stamp #3 A B D

  Here, the first and second phases are the same - they are not similar to each other, but each of them is similar to the 3rd 
  record. Accordingly, we have 2 groups - they get detected on iteration i=2 (by removing word at position 2):
   
   Group 1:
	time stamp #1 A B C
	time stamp #3 A B D
	changing word: C, D
	
   Group 2:
	time stamp #2 A B C
	time stamp #3 A B D
	changing word: C, D
  
 In general, due to combinatoric explosion, we might need to be able to group every record of the same kind with every record of 
 another kind, and so on. The algorithm finds all such multi-dimensional combinations and prints them
 
 The complexity of the algorithm: if we ignore the case of totally identical records (differing only in time stamp), then the 
 complexity of the algorithm is O(N*m) where N- number of records, m - maximal size of the record in words. 
 The case of identical records is more problematic. Probably, it would be a good idea for a problem description to include 
 the specific examples of behavior on identical records to avoid misunderstanding. It's unlikely that the goal was to generate 
 the exponential number of combinations, but, for the lack of explicit requirements, the program chose to do exactly that.
 
 2. Implementation notes.
 
 To avoid comparison of strings (or lists of string) implementation uses HashMap to store groups data. 
 The key of the map is sub-sentence and value is a container which stores records (record is in-memory 
 representation of a sentence) from which the key was generated by removal of a word (see Algorithm). 
 
 The classes are:
  FileLoader - responsible for loading data from file and converting it to list of records
  
  GroupBuilder - responsible for building group from group data. It also Implements the logic of collecting the 
                 groups of identical sub-sentences (see Example 3). 
                                  
  GroupWriter - responsible for writing a result to a file
  
  Record - is in-memory representation of sentence
  
  Group - is in-memory representation of group, it keeps list of changed words and the corresponding sentences
  
  GroupBuilderApp - is main class that handles file loading, group creation and writing result to a file. It also implements algorithm 
                    described in Algorithm in section 3.1.
  
 How to run
 ----------
 
 1. Maven Build
 
 To build the project go to directory where pom.xml file is located
 and run the following command:

   >mvn package
   
 This command builds the project and creates GroupBuilderApp-1.0-SNAPSHOT.jar file.  
 
 2. Run
 
  2.1 Go to the target directory and run the following commands:
  
  command to run application:
  >java -cp GroupBuilderApp-1.0-SNAPSHOT.jar org.weserve.exam.bw.GroupBuilderApp parameter1  parameter2
   
   Where 
      parameter1 is fully qualified name of input file
      parameter2 is fully qualified name of output file
 
   	
  command to run tests:
  >java -cp GroupBuilderApp-1.0-SNAPSHOT.jar org.weserve.exam.bw.test.TestSuite

   	
 	
 Scalability
 ------------
  The application is scalable 
 
 Possible improvement
 --------------------
 If I had two weeks to do this task I would:
 1. Implement concurrent running of group builders (see Algorithm section 3.1) 
 2. Implement more tests
 3. Improve error (exception) handling



  
 
 
 
     
   
  
   
