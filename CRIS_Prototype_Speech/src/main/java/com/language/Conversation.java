package com.language;

import java.util.ArrayList;

public class Conversation {

	private final String CONVERSATION_ID;
	private ArrayList<String> conversationText;
	private ArrayList<String> agentText;
	private ArrayList<String> clientText;

	public Conversation(String conversationID) {
		this.CONVERSATION_ID = conversationID;
		this.agentText = new ArrayList<>();
		this.clientText = new ArrayList<>();
		this.conversationText = new ArrayList<>();
	}
	
	public String getID() {
		return CONVERSATION_ID;
	}

	public void addAgentLine(String line) {
		agentText.add(line);
		conversationText.add(line);
	}

	public void addClientLine(String line) {
		clientText.add(line);
		conversationText.add(line);
	}


	public void printAgentLines() {
		for (String line : agentText) {
			System.out.println("AGENT: " + line);
		}
	}

	public void printClientLines() {
		for (String line : agentText) {
			System.out.println("CLIENT: " + line);
		}
	}

	public ArrayList<String> getArrayConversationText() {
		return conversationText;
	}

	public ArrayList<String> getAgentText() {
		return agentText;
	}

	public ArrayList<String> getClientText() {
		return clientText;
	}

	public String getConversationText() {
		String completeText = "";
		for (String string : conversationText) {
			completeText = completeText.concat(string + " ");
		}
		return completeText;
	}

	public String getAgentTextCombined() {
		String completeText = "";
		for (String string : agentText) {
			completeText = completeText.concat(string + " ");
		}
		return completeText;
	}

	public String getClientTextCombined() {
		String completeText = "";
		for (String string : clientText) {
			completeText = completeText.concat(string + " ");
		}
		return completeText;
	}

}
