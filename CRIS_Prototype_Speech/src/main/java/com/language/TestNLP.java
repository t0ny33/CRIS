package com.language;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class TestNLP {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		ClientNLP nlp = new ClientNLP();
		nlp.readConversations();
		ArrayList<String> generatedPhrases = nlp.generateShortPhrasesArray();
		nlp.analyzeConversation(3);
		//nlp.analyzeShortPhrase(25);
		for (String string : generatedPhrases) {
			//System.out.println(string);
		}

	}

}
