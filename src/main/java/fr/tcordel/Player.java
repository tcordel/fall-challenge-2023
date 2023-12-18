package fr.tcordel;

import java.util.Scanner;
public class Player {

	public static boolean FIRST_ROUND = true;

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int creatureCount = in.nextInt();
		for (int i = 0; i < creatureCount; i++) {
			int creatureId = in.nextInt();
			int color = in.nextInt();
			int type = in.nextInt();
		}

		// game loop
		while (true) {
			int myScore = in.nextInt();
			int foeScore = in.nextInt();
			int myScanCount = in.nextInt();
			for (int i = 0; i < myScanCount; i++) {
				int creatureId = in.nextInt();
			}
			int foeScanCount = in.nextInt();
			for (int i = 0; i < foeScanCount; i++) {
				int creatureId = in.nextInt();
			}
			int myDroneCount = in.nextInt();
			for (int i = 0; i < myDroneCount; i++) {
				int droneId = in.nextInt();
				int droneX = in.nextInt();
				int droneY = in.nextInt();
				int emergency = in.nextInt();
				int battery = in.nextInt();
			}
			int foeDroneCount = in.nextInt();
			for (int i = 0; i < foeDroneCount; i++) {
				int droneId = in.nextInt();
				int droneX = in.nextInt();
				int droneY = in.nextInt();
				int emergency = in.nextInt();
				int battery = in.nextInt();
			}
			int droneScanCount = in.nextInt();
			for (int i = 0; i < droneScanCount; i++) {
				int droneId = in.nextInt();
				int creatureId = in.nextInt();
			}
			int visibleCreatureCount = in.nextInt();
			for (int i = 0; i < visibleCreatureCount; i++) {
				int creatureId = in.nextInt();
				int creatureX = in.nextInt();
				int creatureY = in.nextInt();
				int creatureVx = in.nextInt();
				int creatureVy = in.nextInt();
			}
			int radarBlipCount = in.nextInt();
			for (int i = 0; i < radarBlipCount; i++) {
				int droneId = in.nextInt();
				int creatureId = in.nextInt();
				String radar = in.next();
			}
			for (int i = 0; i < myDroneCount; i++) {

				// Write an action using System.out.println()
				// To debug: System.err.println("Debug messages...");

				System.out.println("WAIT 1"); // MOVE <x> <y> <light (1|0)> | WAIT <light (1|0)>
			}
		}
	}
}