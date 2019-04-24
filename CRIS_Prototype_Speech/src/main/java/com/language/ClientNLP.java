package com.language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.config.Config;
import com.google.cloud.language.v1beta2.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1beta2.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.Document.Type;
import com.storage.DataStorage;
import com.google.cloud.language.v1beta2.EncodingType;
import com.google.cloud.language.v1beta2.Entity;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.cloud.language.v1beta2.Sentiment;

public class ClientNLP {

	DataStorage d1 = new DataStorage(Config.CALL_CENTER_CONVERSATION_1_NLP_FILE_PATH);
	DataStorage d2 = new DataStorage(Config.CALL_CENTER_CONVERSATION_2_NLP_FILE_PATH);
	DataStorage d3 = new DataStorage(Config.CALL_CENTER_CONVERSATION_3_NLP_FILE_PATH);
	String shortPhrasesFile = Config.SHORT_RECORDING_SCRIPTS_FILE_PATH;

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	Conversation c1 = new Conversation("c#" + timestamp);
	Conversation c2 = new Conversation("c#" + timestamp);
	Conversation c3 = new Conversation("c#" + timestamp);

	public void readConversations() throws FileNotFoundException, IOException {
		readText(this.c1, "src\\main\\resources\\Conversation1");
		readText(this.c2, "src\\main\\resources\\Conversation2");
		readText(this.c3, "src\\main\\resources\\Conversation3");
	}

	public void saveConversations() {
		d1.saveCoversation(c1);
		d2.saveCoversation(c2);
		d3.saveCoversation(c3);
	}

	public static void runNLP(String text) throws IOException {

		try (LanguageServiceClient language = LanguageServiceClient.create()) {

			Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

			Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

			System.out.println("======================================================================");
			System.out.println("Text: " + text);
			System.out.println("======================================================================");

			System.out.println("Sentiment analysis...");
			if (sentiment != null) {
				System.out.println();
				System.out.println("--> Sentiment: " + sentiment.getScore());
				System.out.println("--> Magnitude: " + sentiment.getMagnitude());
			} else {
				System.out.println("--> No sentiment found in the text!");
			}
			System.out.println();
			System.out.println("======================================================================");

			AnalyzeEntitiesRequest entityRequest = AnalyzeEntitiesRequest.newBuilder().setDocument(doc)
					.setEncodingType(EncodingType.UTF16).build();
			System.out.println("Entity analysis...");
			AnalyzeEntitiesResponse entityResponse = language.analyzeEntities(entityRequest);

			HashMap<String, Float> foundEntities = new HashMap<>();

			for (Entity entity : entityResponse.getEntitiesList()) {
				if (foundEntities.containsKey(entity.getName().toLowerCase()) == false) {
					foundEntities.put(entity.getName().toLowerCase(), entity.getSalience());
					System.out.print("(" + entity.getName() + ", ");
					System.out.printf("%.3f", entity.getSalience());
					System.out.print(")");
					System.out.println();
				}
			}

			System.out.println("======================================================================");
			System.out.println();
		}

	}

	public void readText(Conversation c, String filePath) throws FileNotFoundException, IOException {
		File textFile = new File(filePath);
		try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
			String line;
			boolean flag = false;
			while ((line = br.readLine()) != null) {
				if (flag == false) {
					c.addAgentLine(line);
					flag = true;
				} else {
					c.addClientLine(line);
					flag = false;
				}
			}
		}
	}

	public void analyzeConversation(int choice) throws IOException {
		Conversation conversationSelected = new Conversation("c#" + timestamp);
		if (choice == 1)
			conversationSelected = c1;
		if (choice == 2)
			conversationSelected = c2;
		if (choice == 3)
			conversationSelected = c3;
		String entireConversation = conversationSelected.getConversationText();
		String whatTheAgentSays = conversationSelected.getAgentTextCombined();
		String whatTheClientSays = conversationSelected.getClientTextCombined();
		System.out.println("NATURAL LANGUAGE API START");
		System.out.println("--> APPLYING NLP ON CONVERSATION AS BULK...");
		runNLP(entireConversation);
		System.out.println("--> APPLYING NLP ON AGENT SIDE CONVERSATION...");
		runNLP(whatTheAgentSays);
		System.out.println("--> APPLYING NLP ON CLIENT SIDE CONVERSATION...");
		runNLP(whatTheClientSays);
		System.out.println("--> APPLYING NLP ON EACH INDIVIDUAL PHRASE...");
		for (String line : conversationSelected.getArrayConversationText()) {
			runNLP(line);
		}
		System.out.println("NATURAL LANGUAGE API END");
	}

	public ArrayList<String> generateShortPhrasesArray() throws FileNotFoundException, IOException {
		File textFile = new File(shortPhrasesFile);
		ArrayList<String> phrases = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				phrases.add(line);
			}
		}
		return phrases;
	}

	public void analyzeShortPhrase(String line) throws IOException {
		runNLP(line);
	}
}
