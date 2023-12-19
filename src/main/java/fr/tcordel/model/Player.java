package fr.tcordel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class Player {

	static Game game = new Game();
	public static boolean FIRST_ROUND = true;

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int creatureCount = in.nextInt();
		game.fishes = new ArrayList<>(creatureCount);
		game.uglies = new ArrayList<>(creatureCount);
		Set<Integer> scans = new HashSet<>();
		var fishes = new HashMap<Integer, Fish>();
		var uglies = new HashMap<Integer, Ugly>();
		var drones = new HashMap<Integer, Drone>();

		for (int i = 0; i < creatureCount; i++) {
			int creatureId = in.nextInt();
			int color = in.nextInt();
			int type = in.nextInt();
			if (type >= 0) {
				Fish fish = new Fish(creatureId, color, FishType.values()[type]);
				fishes.put(creatureId, fish);
				game.fishes.add(fish);
			} else {
				Ugly ugly = new Ugly(0, 0, creatureId);
				game.uglies.add(ugly);
				uglies.put(creatureId, ugly);
			}
		}

		List<List<Vector>> targets = List.of(
			List.of(
		new Vector(8500, 5000),
		new Vector(8500, 8500),
		new Vector(1500, 8500),
			new Vector(1500, 5000),
			new Vector(5000, 0)
		), List.of(
				new Vector(1500, 5000),
				new Vector(8500, 5000),
				new Vector(5000, 0)
			));
		int[] targetIndex = new int[] {0, 0};
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
				Vector pos = new Vector(creatureX, creatureY);
				Vector speed = new Vector(creatureVx, creatureVy);
				if (fishes.containsKey(creatureId)) {
					Fish fish = fishes.get(creatureId);
					fish.pos = pos;
					fish.speed = speed;
				} else if (uglies.containsKey(creatureId)) {
					Ugly ugly = uglies.get(creatureId);
					ugly.pos = pos;
					ugly.speed = speed;
				}
			}
			int radarBlipCount = in.nextInt();
			for (int i = 0; i < radarBlipCount; i++) {
				int droneId = in.nextInt();
				int creatureId = in.nextInt();
				String radar = in.next();
			}

//			game.performGameUpdate(0);
			for (int i = 0; i < myDroneCount; i++) {
				Drone drone = game.gamePlayers.get(0).drones.get(i);
				List<Vector> vectors = targets.get(i);
				Vector vector = vectors.get(targetIndex[i]);
				if (vector.inRange(drone.pos, 100)) {
					System.err.println("Next target for " + i + ", " + targetIndex[i]);
					targetIndex[i]++;
					if (targetIndex[i] >= vectors.size()) {
						targetIndex[i] = 0;
					}
					vector = vectors.get(targetIndex[i]);
				}
				System.out.println("MOVE %d %d %d".formatted(
					(int)vector.getX(),
					(int) vector.getY(),
					Math.random() > 0.10d ? 1 : 0
				));
			}
		}
	}
}