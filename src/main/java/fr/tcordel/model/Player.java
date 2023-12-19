package fr.tcordel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Player {

	static Game game = new Game();
	public static boolean FIRST_ROUND = true;

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int creatureCount = in.nextInt();
		game.fishes = new ArrayList<>(creatureCount);
		game.uglies = Collections.emptyList();
		Set<Integer> scans = new HashSet<>();
		var fishes = new HashMap<Integer, Fish>();
		var drones = new HashMap<Integer, Drone>();

		for (int i = 0; i < creatureCount; i++) {
			int creatureId = in.nextInt();
			int color = in.nextInt();
			int type = in.nextInt();
			Fish fish = new Fish(creatureId, color, FishType.values()[type]);
			fishes.put(creatureId, fish);
			game.fishes.add(fish);
		}

		game.gamePlayers = List.of(new GamePlayer(), new GamePlayer());
		// game loop
		while (true) {
			int myScore = in.nextInt();
			game.gamePlayers.get(0).setScore(myScore);
			int foeScore = in.nextInt();
			game.gamePlayers.get(1).setScore(foeScore);

			int myScanCount = in.nextInt();
			game.gamePlayers.get(0).scans.clear();
			for (int i = 0; i < myScanCount; i++) {
				int creatureId = in.nextInt();
				game.gamePlayers.get(0).scans.add(new Scan(fishes.get(creatureId)));
			}
			int foeScanCount = in.nextInt();
			game.gamePlayers.get(1).scans.clear();
			for (int i = 0; i < foeScanCount; i++) {
				int creatureId = in.nextInt();
				game.gamePlayers.get(1).scans.add(new Scan(fishes.get(creatureId)));
			}
			int myDroneCount = in.nextInt();
			for (int i = 0; i < myDroneCount; i++) {
				int droneId = in.nextInt();
				int droneX = in.nextInt();
				int droneY = in.nextInt();
				int emergency = in.nextInt();
				int battery = in.nextInt();
				if (FIRST_ROUND) {
					Drone drone = new Drone(droneX, droneY, droneId, game.gamePlayers.get(0));
					game.gamePlayers.get(0).drones.add(drone);
					drones.put(droneId, drone);
				}

				Drone drone = game.gamePlayers.get(0).drones.get(i);
				drone.pos = new Vector(droneX, droneY);
				drone.battery = battery;
			}
			int foeDroneCount = in.nextInt();
			for (int i = 0; i < foeDroneCount; i++) {
				int droneId = in.nextInt();
				int droneX = in.nextInt();
				int droneY = in.nextInt();
				int emergency = in.nextInt();
				int battery = in.nextInt();
				if (FIRST_ROUND) {
					Drone drone = new Drone(droneX, droneY, droneId, game.gamePlayers.get(1));
					game.gamePlayers.get(1).drones.add(drone);
					drones.put(droneId, drone);
				}
				Drone drone = game.gamePlayers.get(1).drones.get(i);
				drone.pos = new Vector(droneX, droneY);
				drone.battery = battery;
			}
			int droneScanCount = in.nextInt();
			drones.values().forEach(drone -> drone.scans.clear());
			for (int i = 0; i < droneScanCount; i++) {
				int droneId = in.nextInt();
				int creatureId = in.nextInt();
				System.err.println("Scanned " + droneId + "-" + creatureId);
				Scan e = new Scan(fishes.get(creatureId));
				drones.get(droneId).scans.add(e);
			}
			int visibleCreatureCount = in.nextInt();
			for (int i = 0; i < visibleCreatureCount; i++) {
				int creatureId = in.nextInt();
				int creatureX = in.nextInt();
				int creatureY = in.nextInt();
				int creatureVx = in.nextInt();
				int creatureVy = in.nextInt();
				Fish fish = fishes.get(creatureId);
				fish.pos = new Vector(creatureX, creatureY);
				fish.speed = new Vector(creatureVx, creatureVy);
			}
			int radarBlipCount = in.nextInt();
			for (int i = 0; i < radarBlipCount; i++) {
				int droneId = in.nextInt();
				int creatureId = in.nextInt();
				String radar = in.next();
			}

//			game.performGameUpdate(0);
			for (int i = 0; i < myDroneCount; i++) {
				Drone drone = game.gamePlayers.get(0)
					.drones.get(i);
				Fish fish = fishes.values()
					.stream()
					.filter(f -> !scans.contains(f.id))
					.sorted(Comparator.comparingDouble(f -> f.pos.manhattanTo(drone.getPos())))
					.findFirst()
					.orElse(null);

				// Write an action using System.out.println()
				// To debug: System.err.println("Debug messages...");
				if (fish == null) {
					System.out.println("WAIT 0"); // MOVE <x> <y> <light (1|0)> | WAIT <light (1|0)>
				} else {
					boolean inRangeOfLight = fish.getPos().euclideanTo(drone.getPos()) <= Game.LIGHT_SCAN_RANGE;
					System.out.println("MOVE %d %d %d".formatted((int)fish.getX(), (int)fish.getY(),
						inRangeOfLight ? 1 : 0));

					if (inRangeOfLight) {
						System.err.println("ADD fish" + fish.getId());
						scans.add(fish.getId());
					}
				}
			}
		}
	}
}