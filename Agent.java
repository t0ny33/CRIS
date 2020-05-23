package com.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.alarm.Alarm;
import com.alarm.AlarmContainer;
import com.alarm.DayAlarm;
import com.alarm.NightAlarm;
import com.config.DialogConfig;
import com.config.ExcelConfig;
import com.dialog.Dialog;
import com.google.cloud.dialogflow.v2beta1.Intent;
import com.google.common.collect.Lists;
import com.gui.FileChooser;
import com.reader.ExcelReader;
import com.speech.Recognizer;
import com.speech.Response;

public class Agent {
	static Scanner sc = new Scanner(System.in);
	static Recognizer r = new Recognizer();
	static Boolean correct = false;
	static String currentResponse;

	static List<String> texts = new ArrayList<String>();
	static List<Intent> intents = Lists.newArrayList();

	static AlarmContainer<Alarm> alarmsHistory = new AlarmContainer<Alarm>();
	static AlarmContainer<DayAlarm> alarmsDay = new AlarmContainer<DayAlarm>();
	static AlarmContainer<NightAlarm> alarmsNight = new AlarmContainer<NightAlarm>();

	static AlarmContainer<Alarm> alarmsHistory_filtered = new AlarmContainer<Alarm>();
	static AlarmContainer<DayAlarm> alarmsDay_filtered = new AlarmContainer<DayAlarm>();
	static AlarmContainer<NightAlarm> alarmsNight_filtered = new AlarmContainer<NightAlarm>();

	static ExcelReader exReader = new ExcelReader();
	static FileChooser fc = new FileChooser();

	static ExcelConfig excelConfig = new ExcelConfig();

	public static void welcomeScreen() {
		System.out.println("Welcome to the IVC-Agent POC demonstration!");
		System.out.println(
				"This POC is built to show the capabilities of an chatbot interface for querying and filtering alarms within Power BI!\n");
	}

	public static boolean continueCheck() throws InterruptedException {
		boolean flag = false;
		String choice;
		System.out.print("Do you want to continue (y/n)?: ");
		choice = sc.nextLine().toLowerCase();
		if (choice.equals("y")) {
			System.out.println("We shall proceed then!\n");
			TimeUnit.SECONDS.sleep(1);
			flag = true;
		} else if (choice.equals("n")) {
			System.out.println("Exiting loop...\n");
			TimeUnit.SECONDS.sleep(1);
			/*
			 * System.out.println("You may now close the application!"); sc.close();
			 * System.exit(1);
			 */
		} else {
			System.out.println("Invlaid input! Try again!\n");
			continueCheck();
		}
		return flag;
	}

	public static void enterInput() {
		System.out.println("Please type in your text input!");
		System.out.print("Type in your input: ");
		currentResponse = sc.nextLine();
	}

	public static void changeInput() {
		System.out.println("You have requested to change you input!");
		System.out.println("Your previous input was: " + currentResponse + "\n");
		System.out.print("Please type in your new input: ");
		currentResponse = sc.nextLine();
		System.out.println("Your new input is: " + currentResponse + "\n");
	}

	public static boolean correctCheck() {
		String choice;
		System.out.println("Your input is: " + currentResponse);
		System.out.print("Is this correct (y/n)?: ");
		choice = sc.nextLine().toLowerCase();
		if (choice.equals("y")) {
			return true;
		} else if (choice.equals("n")) {
			changeInput();
		} else {
			System.out.println("Invlaid input! Try again!");
			return correctCheck();
		}
		return false;
	}

	public static void recordAudio() throws Exception {
		System.out.println("=========== Speech recognition ===========");
		r.streamingMicRecognize();
		Response r1 = new Response(r.getTrasncript());
		TimeUnit.SECONDS.sleep(1);
		System.out.println("Your audio input was: " + r1.getBody());
		System.out.println(r1);
		System.out.println("=========== Speech recognition ===========\n");
	}

	public static void loadData() throws InterruptedException {
		FileChooser.start();
		TimeUnit.SECONDS.sleep(45);
		excelConfig = fc.readExcelConfig();

		System.out.println("=========== Loading data paths ===========");
		System.out.println(excelConfig.getAlarmsHistoryPath());
		System.out.println(excelConfig.getAlarmsDayPath());
		System.out.println(excelConfig.getAlarmsNightPath());
		System.out.println("=========== Loading data paths ===========");
		System.out.println();
	}

	public static void main(String[] args) throws Exception {

		welcomeScreen();

		loadData();

		while (true) {
			System.out.print(
					"Do you want to test the chatbot interface, speech recognition, or the filtering system (1 / 2 / 3)?: ");
			String choice;
			choice = sc.nextLine().toLowerCase();

			if (choice.equals("1")) {

				while (continueCheck() == true) {
					enterInput();
					correctCheck();
					try {
						Dialog.detectIntentTexts(DialogConfig.PROJECT_ID, currentResponse, DialogConfig.SESSION_ID,
								DialogConfig.LANGUAGE_CODE);
						// Dialog.listEntities(DialogConfig.PROJECT_ID, DialogConfig.ENTITY_TYPE_ID);
						/*
						 * intents = Dialog.listIntents(DialogConfig.PROJECT_ID);
						 * System.out.println("=========== Defined intents ==========="); for (Intent
						 * intent : intents) { System.out.println(intent.getDisplayName());
						 * 
						 * 
						 * } System.out.println("=========== Defined intents ===========\n");
						 */
					} catch (Exception e) { // TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else if (choice.equals("2")) {
				while (continueCheck() == true) {
					recordAudio();
				}
			} else if (choice.equals("3")) {
				while (continueCheck() == true) {
					System.out.println("=========== Testing filtering system ===========\n");
					exReader.initializeExcelPaths(excelConfig);

					exReader.populateHistoryAlarms(alarmsHistory);
					exReader.populateDayAlarms(alarmsDay);
					exReader.populateNightAlarms(alarmsNight);

					System.out.println("=========== Testing filtering system (History) ===========");

					alarmsHistory_filtered = alarmsHistory.searchByIdx(alarmsHistory, 90);
					alarmsHistory_filtered.printAllAlarms();

					alarmsHistory_filtered = alarmsHistory.searchByStatistictimeUtc(alarmsHistory, "2019-06-27 05:15");
					alarmsHistory_filtered.printAllAlarms();

					alarmsHistory_filtered = alarmsHistory.searchByCarrier(alarmsHistory, "TELDE");
					alarmsHistory_filtered.printAllAlarms();

					alarmsHistory_filtered = alarmsHistory.searchByDestination(alarmsHistory, "Romania");
					alarmsHistory_filtered.printAllAlarms();

					alarmsHistory_filtered = alarmsHistory.searchByPriorityLevel(alarmsHistory, "high");
					alarmsHistory_filtered.printAllAlarms();

					alarmsHistory_filtered = alarmsHistory.searchByTrendAtTimeTriggered(alarmsHistory, "stable");
					alarmsHistory_filtered.printAllAlarms();

					System.out.println("=========== Testing filtering system (History) ===========\n");

					System.out.println("=========== Testing filtering system (Day) ===========");
					alarmsDay_filtered = alarmsDay.searchByPriority(alarmsDay, "P1.2");
					alarmsDay_filtered.printAllAlarms();

					alarmsDay_filtered = alarmsDay.searchByScoreDay(alarmsDay, 12);
					alarmsDay_filtered.printAllAlarms();

					alarmsDay_filtered = alarmsDay.searchByStatus(alarmsDay, "fired");
					alarmsDay_filtered.printAllAlarms();

					alarmsDay_filtered = alarmsDay.searchByRouteClass(alarmsDay, "OpCo:Outbound");
					alarmsDay_filtered.printAllAlarms();

					alarmsDay_filtered = alarmsDay.searchByRankingInfo(alarmsDay, "30/1 ,38");
					alarmsDay_filtered.printAllAlarms();

					alarmsDay_filtered = alarmsDay.searchByCarrier(alarmsDay, "VF_TR");
					alarmsDay_filtered.printAllAlarms();

					alarmsDay_filtered = alarmsDay.searchByDestination(alarmsDay, "Germany");
					alarmsDay_filtered.printAllAlarms();

					System.out.println("=========== Testing filtering system (Day) ===========\n");

					System.out.println("=========== Testing filtering system (Night) ===========");

					alarmsNight_filtered = alarmsNight.searchByTechnicalSeverity(alarmsNight, "high");
					alarmsNight_filtered.printAllAlarms();

					alarmsNight_filtered = alarmsNight.searchByDayUtc(alarmsNight, "2019-06-27");
					alarmsNight_filtered.printAllAlarms();

					alarmsNight_filtered = alarmsNight.searchByCallDirection(alarmsNight, "Inbound");
					alarmsNight_filtered.printAllAlarms();

					alarmsNight_filtered = alarmsNight.searchByMediaDirection(alarmsNight, "Outbound");
					alarmsNight_filtered.printAllAlarms();

					alarmsNight_filtered = alarmsNight.searchByVolumeCluster(alarmsNight, "med");
					alarmsNight_filtered.printAllAlarms();

					alarmsNight_filtered = alarmsNight.searchByKpiType(alarmsNight, "media");
					alarmsNight_filtered.printAllAlarms();

					alarmsNight_filtered = alarmsNight.searchByCountry(alarmsNight, "Turkey");
					alarmsNight_filtered.printAllAlarms();

					System.out.println("=========== Testing filtering system (Night) ===========\n");

					System.out.println("=========== Testing filtering system ===========");
				}
			}
		}
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * 
	 * welcomeScreen();
	 * 
	 * if (continueCheck() == true) {
	 * 
	 * while (true) {
	 * 
	 * // r.microphoneTest();
	 * 
	 * // r.streamingMicRecognize();
	 * 
	 * Response r1 = new Response(r.getTrasncript()); TimeUnit.SECONDS.sleep(1);
	 * 
	 * System.out.println(r1); System.out.print("Is this correct (y/n)?: ");
	 * response = sc.nextLine().toLowerCase();
	 * 
	 * if (response.equals("y")) { System.out.println(r1); texts.add(r1.getBody());
	 * try { Dialog.detectIntentTexts(DialogConfig.PROJECT_ID, r1.getBody(),
	 * DialogConfig.SESSION_ID, DialogConfig.LANGUAGE_CODE); //
	 * Dialog.listEntities(DialogConfig.PROJECT_ID, DialogConfig.ENTITY_TYPE_ID); //
	 * intents = Dialog.listIntents(DialogConfig.PROJECT_ID);
	 * 
	 * for (Intent intent : intents) {
	 * 
	 * if (intent.getDisplayName().
	 * equals("User-requests-history-alarm-table - show all alarms")) {
	 * exReader.populateHistoryAlarms(alarmsHistory);
	 * alarmsHistory.printAllAlarms();
	 * System.out.println(alarmsHistory.getNumberOfAlarms()); }
	 * 
	 * } } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } continue; } else if (response.equals("n")) {
	 * System.out.println("Let's try that again!"); TimeUnit.SECONDS.sleep(1); }
	 * else { System.out.println("Invalid input! Try again!");
	 * TimeUnit.SECONDS.sleep(1); } } }
	 * 
	 * }
	 */

}
