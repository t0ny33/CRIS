package sandbox;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class TestSpeech {

	public static void main(String[] args) throws Exception {

		Scanner sc = new Scanner(System.in);
		Recognizer r = new Recognizer();
		Boolean correct = false;
		String response;
		

		System.out.println("Welcome to CRIS real-time speech recognition demonstration!\n");
		System.out.println(
				"This demostration is built to show possible capabilities of real-time speech recognition using cloud libraries!\n");
		while (correct != true) {
			System.out.print("Do you want to continue (y/n)?: ");
			response = sc.nextLine().toLowerCase();
			if (response.equals("y")) {
				System.out.println("We shall proceed then!");
				TimeUnit.SECONDS.sleep(1);
				correct = true;

			} else if (response.equals("n")) {
				System.out.println("Shutting down...");
				sc.close();

				System.exit(1);
			} else {
				System.out.println("Invlaid input! Try again!");
			}
		}

		while (true) {

			// r.microphoneTest();

			r.streamingMicRecognize();

			Response r1 = new Response(r.getTrasncript());
			TimeUnit.SECONDS.sleep(5);

			System.out.println(r1);
			System.out.print("Is this correct (y/n)?: ");
			response = sc.nextLine().toLowerCase();

			if (response.equals("y")) {
				System.out.println(r1);
				System.exit(1);
			} else if (response.equals("n")) {
				System.out.println("Let's try that again!");
				TimeUnit.SECONDS.sleep(1);
			} else {
				System.out.println("Invalid input! Try again!");
				TimeUnit.SECONDS.sleep(1);
			}
		}
	}
}
