package org.weserve.exam.bw.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.weserve.exam.bw.GroupBuilderApp;

public class TestSuite {

	public static void main(String[] args) throws IOException {

		String input;
		String expected;
		Test test;


		input = "01-01-2012 19:45:00 Naomi is getting into the car\n";

		expected = "";

		test = new Test(input, expected, "One line");
		test.run();

		// ------------------------------------------------------------------------//
		input = "01-01-2012 19:45:00 Naomi is getting into the car\n" +
				"03-01-2012 11:22:40 Mike is getting into the car\n";

		expected = "01-01-2012 19:45:00 Naomi is getting into the car\n" +
				   "03-01-2012 11:22:40 Mike is getting into the car\n" + 
				   "The changing word was: Naomi, Mike\n";

		test = new Test(input, expected, "One group, no duplications");
		test.run();

		// ------------------------------------------------------------------------//
		input = "01-01-2012 19:45:00 Naomi is getting into the car\n"+ 
				"01-01-2012 19:45:01 Naomi is getting into the car\n" +
				"03-01-2012 11:22:40 Mike is getting into the car\n";

		expected = "01-01-2012 19:45:00 Naomi is getting into the car\n" +
				   "03-01-2012 11:22:40 Mike is getting into the car\n" + 
				   "The changing word was: Naomi, Mike\n" +
				   "01-01-2012 19:45:01 Naomi is getting into the car\n" +
				   "03-01-2012 11:22:40 Mike is getting into the car\n" + 
				   "The changing word was: Naomi, Mike\n";

		test = new Test(input, expected, "Two group, one duplication");
		test.run();
		
		// ------------------------------------------------------------------------//
		input = "01-01-2012 19:45:00 Naomi is getting into the car\n"+ 
				"01-01-2012 19:45:01 Naomi is getting into the car\n" +
				"03-01-2012 11:22:40 Mike is getting into the car\n" +
				"03-01-2012 11:22:41 Mike is getting into the car\n" +
				"03-01-2012 11:22:41 Boris is getting into the car\n";

		expected = 
				"01-01-2012 19:45:00 Naomi is getting into the car\n" + 
				"03-01-2012 11:22:40 Mike is getting into the car\n" + 
				"03-01-2012 11:22:41 Boris is getting into the car\n" + 
				"The changing word was: Naomi, Mike, Boris\n" + 
				"01-01-2012 19:45:01 Naomi is getting into the car\n" + 
				"03-01-2012 11:22:40 Mike is getting into the car\n" + 
				"03-01-2012 11:22:41 Boris is getting into the car\n" + 
				"The changing word was: Naomi, Mike, Boris\n" + 
				"01-01-2012 19:45:00 Naomi is getting into the car\n" + 
				"03-01-2012 11:22:41 Mike is getting into the car\n" + 
				"The changing word was: Naomi, Mike, Boris\n" + 
				"01-01-2012 19:45:01 Naomi is getting into the car\n" + 
				"03-01-2012 11:22:41 Mike is getting into the car\n" + 
				"The changing word was: Naomi, Mike, Boris\n";

		test = new Test(input, expected, "Four groups, two duplication");
		test.run();

		// ------------------------------------------------------------------------//
		input = "01-01-2012 19:45:00 Naomi is getting into the car\n" +
				"01-01-2012 19:45:01 Naomi is getting into the car\n" + 
				"01-01-2012 19:45:02 Naomi is getting into the car\n";
		
		expected = "";

		test = new Test(input, expected, "No groups, full duplication");
		test.run();

		// ------------------------------------------------------------------------//
		input = "01-01-2012 19:45:00 Naomi is getting into the car\n" +
				"01-01-2012 19:45:01 Mike is getting into the bar\n";

		expected = "";

		test = new Test(input, expected, "No groups, two differences");
		test.run();
		
		// ------------------------------------------------------------------------//
		input = "01-01-2012 19:45:00 Naomi is getting into the car\n" +
				"01-01-2012 19:45:01 Naomi is getting into the blue car\n";

		expected = "";

		test = new Test(input, expected, "No groups, different length");
		test.run();

		// ------------------------------------------------------------------------//
		input = "01-01-2012 19:45:00 Naomi is getting into the car\n" + 
				"01-01-2012 20:12:39 Naomi is eating at a restaurant\n" + 
				"01-01-2012 20:12:40 Naomi is eating at a restaurant\n" + 
				"02-01-2012 09:13:15 George is getting into the car\n" + 
				"02-01-2012 10:14:00 George is eating at a diner\n" + 
				"03-01-2012 10:15:00 Naomi is eating at a diner\n" + 
				"03-01-2012 11:22:40 Mike is getting into the car\n" + 
				"03-01-2012 12:52:23 Mike is getting into the office\n" + 
				"04-01-2012 21:55:05 Naomi is running into the car\n";
		
		
		expected = 
				"01-01-2012 19:45:00 Naomi is getting into the car\n" + 
				"02-01-2012 09:13:15 George is getting into the car\n" + 
				"03-01-2012 11:22:40 Mike is getting into the car\n" + 
				"The changing word was: Naomi, Mike, George\n" + 
				"02-01-2012 10:14:00 George is eating at a diner\n" + 
				"03-01-2012 10:15:00 Naomi is eating at a diner\n" + 
				"The changing word was: Naomi, George\n" + 
				"01-01-2012 19:45:00 Naomi is getting into the car\n" + 
				"04-01-2012 21:55:05 Naomi is running into the car\n" + 
				"The changing word was: getting, running\n" + 
				"01-01-2012 20:12:39 Naomi is eating at a restaurant\n" + 
				"03-01-2012 10:15:00 Naomi is eating at a diner\n" + 
				"The changing word was: diner, restaurant\n" + 
				"01-01-2012 20:12:40 Naomi is eating at a restaurant\n" + 
				"03-01-2012 10:15:00 Naomi is eating at a diner\n" + 
				"The changing word was: diner, restaurant\n" + 
				"03-01-2012 11:22:40 Mike is getting into the car\n" + 
				"03-01-2012 12:52:23 Mike is getting into the office\n" + 
				"The changing word was: car, office\n";

		//Put all together
		test = new Test(input, expected, "All together");
		test.run();
	}

	private static class Test {

		final private static String inputNamePrefix = "input";
		final private static String resultPrefix = "result";
		final private static String fileNameSuffix = "txt";

		final private String testName;

		final private String inputData;
		final private String expectedData;

		private Test(String inputData, String expectedData, String testName) {
			this.inputData = inputData;
			this.expectedData = expectedData;
			this.testName = testName;
		}

		public String getTestName() {
			return testName;
		}

		/*
		 * Creates temporary file and writes given data into
		 */
		private File createInputFile(String inputData) throws IOException {

			BufferedWriter writer = null;

			try {

				File file = File.createTempFile(inputNamePrefix, fileNameSuffix);
				file.deleteOnExit();

				writer = new BufferedWriter(new FileWriter(file));
				
				if (!inputData.isEmpty())
					writer.write(inputData);
				else 
					writer.write("\n");
				

				return file;

			} finally {

				if (writer != null) {
					try {
						writer.close();
					} catch (Exception e) {};
				}
			}
		}

		/*
		 * Builds unique file name for specified directory
		 */
		private String buildUniqueFileName(String directory) {

			String separator = directory.endsWith(File.separator) ? "" : File.separator;

			int count = 0;

			while (true) {

				count++;
				String fileName = directory + separator + resultPrefix + "_" + count + '.' + fileNameSuffix;
				File file = new File(fileName);

				if (!file.exists()) {
					file.deleteOnExit();
					return fileName;
				}
			}
		}

		/*
		 * Loads result string from result file
		 */
		private String loadResultAndDeleteFile(String outputFile) throws IOException {

			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(outputFile));
				StringBuilder builder = new StringBuilder();

				String line = null;

				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append('\n');
				}

				return builder.toString();

			} finally {

				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {};
				}

				try {
					Path path = Paths.get(outputFile);
					Files.deleteIfExists(path);
				} catch (IOException e) {};

			}
		}

		/*
		 * Runs the test and prints result.
		 * If test failed it prints expected and actual results
		 */
		private boolean run() {

			String outputFile = null;
			File inputFile = null;

			try {

				inputFile = createInputFile(inputData);
				outputFile = buildUniqueFileName(inputFile.getParent());

				// run our main
				String[] arguments = { inputFile.getPath(), outputFile };
				GroupBuilderApp.main(arguments);
				
				/*
				 * Get result
				 */
				String result = loadResultAndDeleteFile(outputFile);

				boolean success = result.equals(expectedData);

				if (success) {
					System.out.println("Test \"" + getTestName() + "\" - passed");
				} else {
					System.err.println("Test " + getTestName() + " - failed");
					System.out.println("Expected: " + expectedData);
					System.out.println("Got: " + result);
					return false;
				}

				return success;

			} catch (Exception e) {
				System.err.println("Error running test " + getTestName() + ": "+ e.getMessage());
				return false;
			}

		}
	}
}
