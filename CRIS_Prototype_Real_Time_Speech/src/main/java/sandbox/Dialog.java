package com.dialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.dialogflow.v2beta1.*;
import com.google.cloud.dialogflow.v2beta1.EntityType.Entity;
import com.google.cloud.dialogflow.v2beta1.TextInput.Builder;
import com.google.common.collect.Lists;

public class Dialog {

	/**
	 * Returns the result of detected intent with texts as main inputs.
	 *
	 * Using the same `session_id` between requests allows continuation of the
	 * conversation.
	 *
	 * @param projectId    Project/Agent Id.
	 * @param texts        The text intents to be detected based on what a user
	 *                     says.
	 * @param sessionId    Identifier of the DetectIntent session.
	 * @param languageCode Language code of the query.
	 * @return The QueryResult for each input text.
	 */
	public static String detectIntentTexts(String projectID, String input, String sessionID,
			String languageCode) throws Exception {


		// Instantiate a dialog client
		try (SessionsClient sessionsClient = SessionsClient.create()) {
			SessionName session = SessionName.of(projectID, sessionID);
			// System.out.println("Session Path: " + session.toString());

				// Set the text and language code (en-US) for the dialog query
				Builder textInput = TextInput.newBuilder().setText(input).setLanguageCode(languageCode);

				// Build the query with the Textinput
				QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

				// Perform detection on intent requests
				DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

				// Display the query result
				QueryResult queryResult = response.getQueryResult();
				System.out.println("<=== Query Result ===>");
				System.out.println("Query Text: " + queryResult.getQueryText());
				System.out.println("Detected Intent (intent, confidence): (" + queryResult.getIntent().getDisplayName()
						+ ", " + queryResult.getIntentDetectionConfidence() + ")");
				System.out.println("Response Text: " + queryResult.getFulfillmentText());
				System.out.println("<=== Query Result ===>\n");
				return queryResult.getFulfillmentText();

				// Add the result to the map queryResults Map<Stirng, QueryResult>
			
		}

	}

	/**
	 * List entities defined within your agent.
	 * 
	 * @param projectId    Project/agent id.
	 * @param entityTypeId The id of the entity_type.
	 * @return List of found entities.
	 * @throws Exception
	 */
	public static List<Entity> listEntities(String projectID, String entitiyTypeID) throws Exception {
		// Instantiate a dialog client
		try (EntityTypesClient entityTypesClient = EntityTypesClient.create()) {
			// Set the entity type name using the projectID and entityTypeID (KIND_LIST)
			EntityTypeName entityTypeName = EntityTypeName.of(projectID, entitiyTypeID);

			// Perform the get entity type request
			EntityType entityType = entityTypesClient.getEntityType(entityTypeName);
			List<Entity> entities = entityType.getEntitiesList();
			for (Entity entity : entities) {
				System.out.println("<=== Entity List ===>");
				System.out.println("Entitiy value: " + entity.getValue());
				System.out.println("Entity synonyms: " + entity.getSynonymsList().toString());
				System.out.println("<=== Entity List ===>\n");
			}
			return entities;
		}
	}

	/**
	 * List intents defined within your agent.
	 * 
	 * @param projectId Project/Agent Id.
	 * @return Intents defined within the agent.
	 * @throws Exception
	 */
	public static List<Intent> listIntents(String projectID) throws Exception {
		List<Intent> intents = Lists.newArrayList();

		// Instantiate a dialog client
		try (IntentsClient intentsClient = IntentsClient.create()) {
			// Set the project agent name using the projectID
			ProjectAgentName parent = ProjectAgentName.of(projectID);

			// Performing intent list request
			for (Intent intent : intentsClient.listIntents(parent).iterateAll()) {
				/*
				 * System.out.println("<=== Intent List ===>");
				 * System.out.println("Intent name: " + intent.getName());
				 * System.out.println("Action: " + intent.getAction());
				 * System.out.println("Root followup intent: " +
				 * intent.getRootFollowupIntentName());
				 * System.out.println("Parent followup inent: " +
				 * intent.getParentFollowupIntentName());
				 * 
				 * System.out.println("Input contexts:\n"); for (String inputContextName :
				 * intent.getInputContextNamesList()) {
				 * System.out.println("Input context name: " + inputContextName); }
				 * 
				 * System.out.println("\nOutput contexts:\n"); for (String outputContextName :
				 * intent.getInputContextNamesList()) {
				 * System.out.println("output context name: " + outputContextName); }
				 */

				intents.add(intent);

				//System.out.println("\n<=== Intent List ===>\n");
			}
		}

		return intents;
	}

}
