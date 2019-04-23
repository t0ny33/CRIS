package com.dictionary;

import java.io.BufferedReader;
import java.util.Map;
import java.util.Scanner;

public class Test implements DictionaryInterface {
	// --Local variables declaration start
	private Scanner sc;
	private BufferedReader reader;
	private Map<String, Integer> dictionary;
	private Map<String, Integer> maxDictionary;
	private Map<String, Integer> trimMaxDictionary;
	private String filePath = "C:\\Users\\EneR\\Desktop\\Eclipse Workspace\\CRIS_Prototype_Speech\\src\\main\\resources\\Conversation1";
;
	private String error1 = ">>> ERROR: File has already been read!";
	private String error2 = ">>> ERROR: You must first read the input file!";
	private String error3 = ">>> ERROR: Dictionaries have already been built!";
	private String error4 = ">>> ERROR: You must first build the dictionary.";
	private String error5 = ">>> Invalid choice! Please try again.";
	private boolean read, build;
	private final int TOP = 10;
	private int choice;
	// --Local variables declaration end

	public static void main(String[] args) {
		Test t1 = new Test();

		t1.sc = new Scanner(System.in);

		// --Menu display and input selection
		displayMenu();
		System.out.print(">>> Input yur choice (0-7): ");
		t1.choice = t1.sc.nextInt();
		while (true) {
			switch (t1.choice) {
			case 1:
				if (!t1.read) {
					t1.reader = t1.readFile(t1.filePath);
					t1.read = true;
				} else {
					System.err.println(t1.error1);
				}
				break;
			case 2:
				if (!t1.build && t1.read) {
					t1.dictionary = t1.buildDictionary(t1.reader);
					t1.maxDictionary = t1.sortByDescendingOrder(t1.dictionary);
					t1.build = true;
				} else if (!t1.read) {
					System.err.println(t1.error2);
				} else {
					System.err.println(t1.error3);
				}
				break;
			case 3:
				System.out.println(">>> Printing unsorted dictionary...");
				if (t1.build) {
					t1.showAll(t1.dictionary);
				} else {
					System.err.println(t1.error4);
				}
				break;
			case 4:
				System.out.println(">>> Listing dictionary size...");
				if (t1.build) {
					System.out.println(">>> Dictionary size (no filter): " + t1.countWords(t1.dictionary) + " words");
					System.out.println(
							">>> Dictionary size (with filter): " + t1.countWords(t1.maxDictionary) + " words");
				} else
					System.err.println(t1.error4);
				break;
			case 5:
				System.out.println(">>> Printing descending order dictionary...");
				t1.showAll(t1.maxDictionary);
				break;
			case 6:
				System.out.println(">>> Removing useless words from dictionary...");
				if (t1.build) {
					t1.maxDictionary = t1.filterWords(t1.maxDictionary);
				} else {
					System.err.println(t1.error4);
				}
				break;
			case 7:
				if (t1.build) {
					t1.trimMaxDictionary = t1.buildTopDictionary(t1.maxDictionary, t1.TOP);
					t1.trimMaxDictionary = t1.sortByDescendingOrder(t1.trimMaxDictionary);
					System.out.println("\n>>> Printing top 10 keyword dictionary...");
					t1.showAll(t1.trimMaxDictionary);
				} else {
					System.err.println(t1.error4);
				}
				break;
			case 0:
				System.out.println(">>> Exiting program...");
				System.exit(0);
				break;
			default:
				System.err.println(t1.error5);
			}
			displayMenu();
			System.out.print("Input your choice (0-7): ");
			t1.choice = t1.sc.nextInt();
		}

	}

	private static void displayMenu() {
		System.out.println("\n=========================================================");
		System.out.println(">>> Listing menu options...");
		System.out.println(">>> 1.\tRead input file.");
		System.out.println(">>> 2.\tBuild dictionaries.");
		System.out.println(">>> 3.\tPrint unsorted dictionary.");
		System.out.println(">>> 4.\tShow dictionary sizes.");
		System.out.println(">>> 5.\tPrint dictionary.");
		System.out.println(">>> 6.\tFilter redundant words.");
		System.out.println(">>> 7.\tPrint top 10 dictionary.");
		System.out.println(">>> 0.\tExit");
		System.out.println("=========================================================\n");
	}

}
