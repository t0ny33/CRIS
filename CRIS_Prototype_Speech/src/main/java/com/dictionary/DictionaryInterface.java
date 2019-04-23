package com.dictionary;

import java.io.*;
import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public interface DictionaryInterface {

	// -- File path declaration start
	String pathAdverb = "C:\\Users\\EneR\\Desktop\\Eclipse Workspace\\CRIS_Prototype_Speech\\Input\\adverbs.txt";
	String pathConjunctions = "C:\\Users\\EneR\\Desktop\\Eclipse Workspace\\CRIS_Prototype_Speech\\Input\\conjunctions.txt";
	String pathDeterminers = "C:\\Users\\EneR\\Desktop\\Eclipse Workspace\\CRIS_Prototype_Speech\\Input\\determiners.txt";
	String pathPrepositions = "C:\\Users\\EneR\\Desktop\\Eclipse Workspace\\CRIS_Prototype_Speech\\Input\\prepositions.txt";
	String pathPronouns = "C:\\Users\\EneR\\Desktop\\Eclipse Workspace\\CRIS_Prototype_Speech\\Input\\pronouns.txt";
	// -- File path declaration end

	default BufferedReader readFile(String path) {
		System.out.print("\n>>> Reading file...");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			System.out.print(" Done!");
			return reader;
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: There is a problem with the file reading!");
			e.printStackTrace();
			return null;
		}
	}

	default Map<String, Integer> buildDictionary(BufferedReader reader) {
		String inputLine;
		Map<String, Integer> dictionary = new HashMap<>();
		System.out.print("\n>>> Building dictionary...");
		try {
			while ((inputLine = reader.readLine()) != null) {
				String[] words = inputLine.split("\\s+");
				if (inputLine.equals(""))
					continue;

				for (String word : words) {
					word = word.toLowerCase();
					word = word.replace(".", "");
					word = word.replace(",", "");
					word = word.replace("?", "");
					word = word.replace("!", "");
					word = word.replace(";", "");
					word = word.replace("-", "");
					if (dictionary.containsKey(word)) {
						Integer val = dictionary.get(word);
						dictionary.put(word, val + 1);
					} else
						dictionary.put(word, 1);
				}
			}
			System.out.print(" Done!");
			return dictionary;
		} catch (IOException e) {
			System.err.print("\nERROR: Something went wrong during the creation of the dictionary!");
			e.printStackTrace();
			return null;
		}
	}

	default void showAll(Map<String, Integer> dictionary) {
		System.out.print("\n>>> Printing dictionary...");
		System.out.println("\n//////////////////////////////////////////////////////////");
		for (String key : dictionary.keySet()) {
			System.out.printf("[---> Word: %" + 20 + "s  Occurrences: %" + 10 + "d]\n", key, dictionary.get(key));
		}
		System.out.println("//////////////////////////////////////////////////////////\n");
	}

	default Map<String, Integer> buildTopDictionary(Map<String, Integer> dictionary, int limit) {
		System.out.print("\n>>> Building dictionary with top " + limit + " words...");
		Map<String, Integer> topWords = new HashMap<>();
		for (String key : dictionary.keySet()) {
			if (limit > 0) {
				topWords.put(key, dictionary.get(key));
			} else {
				break;
			}
			limit--;
		}
		return topWords;
	}

	default Map<String, Integer> sortByDescendingOrder(Map<String, Integer> dictionary) {
		System.out.print("\n>>> Sorting the dictionary in descending order of occurrences...");
		Map<String, Integer> sortedDecreasingWords = dictionary.entrySet().stream()
				.sorted(Collections.reverseOrder(comparingByValue()))
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		System.out.print(" Done!");
		return sortedDecreasingWords;
	}

	default Map<String, Integer> filterWords(Map<String, Integer> dictionary) {
		ArrayList<String> filterAdverbs, filterConjunctions, filterDeterminers, filterPrepositions, filterPronouns;
		ArrayList<String> allFilterWords = new ArrayList<>();
		BufferedReader adverbReader = this.readFile(pathAdverb);
		BufferedReader conjunctionReader = this.readFile(pathConjunctions);
		BufferedReader determinerReader = this.readFile(pathDeterminers);
		BufferedReader prepositionReader = this.readFile(pathPrepositions);
		BufferedReader pronounReader = this.readFile(pathPronouns);

		System.out.println("\n>>> Building filters...");
		System.out.print("\n>>> Adverb \t filter...");
		filterAdverbs = this.buildFilter(adverbReader);
		this.removeWords(dictionary, filterAdverbs, allFilterWords);
		System.out.print(" Done!");
		System.out.print("\n>>> Conjunction \t filter...");
		filterConjunctions = this.buildFilter(conjunctionReader);
		this.removeWords(dictionary, filterConjunctions, allFilterWords);
		System.out.print(" Done!");
		System.out.print("\n>>> Determiner \t filter...");
		filterDeterminers = this.buildFilter(determinerReader);
		this.removeWords(dictionary, filterDeterminers, allFilterWords);
		System.out.print(" Done!");
		System.out.print("\n>>> Preposition \t filter...");
		filterPrepositions = this.buildFilter(prepositionReader);
		this.removeWords(dictionary, filterPrepositions, allFilterWords);
		System.out.print(" Done!");
		System.out.print("\n>>> Pronouns \t filter...");
		filterPronouns = this.buildFilter(pronounReader);
		this.removeWords(dictionary, filterPronouns, allFilterWords);
		System.out.print(" Done!");

		System.out.println("\n>>> Words to be removed from dictionary: ");
		for (String string : allFilterWords) {
			System.out.print("|" + string + "| ");
		}
		return dictionary;
	}

	default ArrayList<String> buildFilter(BufferedReader reader) {
		ArrayList<String> filter = new ArrayList<>();
		String inputLine;
		try {
			while ((inputLine = reader.readLine()) != null) {
				String[] words = inputLine.split(",");

				for (String word : words) {
					if (word.equals(" "))
						continue;
					filter.add(word);
				}
			}
		} catch (IOException e) {
			System.err.println("ERROR: Something went wrong during the creation of the filter!");
			e.printStackTrace();
		}
		return filter;
	}

	default void removeWords(Map<String, Integer> dictionary, ArrayList<String> filter, ArrayList<String> toRemove) {
		for (String string : filter) {
			if (dictionary.containsKey(string)) {
				toRemove.add(string);
				dictionary.remove(string);
			}
		}
	}

	default int countWords(Map<String, Integer> dictionary) {
		int wordCount = 0;
		for (String key : dictionary.keySet()) {
			wordCount += dictionary.get(key);
		}
		return wordCount;
	}

}
