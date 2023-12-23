package fr.tcordel.model;

import java.util.*;

public class Player {

	static Game game = new Game();
	static DownAndUp downAndUp = new DownAndUp(game);
	public static boolean FIRST_ROUND = true;

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int creatureCount = in.nextInt();
		game.fishes = new ArrayList<>(creatureCount);
		game.uglies = new ArrayList<>(creatureCount);
		Set<Integer> scans = new HashSet<>();
		game.visibleFishes = new ArrayList<>();
		game.visibleUglies = new ArrayList<>();
		Set<Integer> myDonesId = new HashSet<>();


		Map<Integer, Integer> droneIdToIndex = new HashMap<>();

		for (int i = 0; i < creatureCount; i++) {
			int creatureId = in.nextInt();
			int color = in.nextInt();
			int type = in.nextInt();
			if (type >= 0) {
				Fish fish = new Fish(creatureId, color, FishType.values()[type]);
				game.fishesMap.put(creatureId, fish);
				game.fishes.add(fish);
			} else {
				Ugly ugly = new Ugly(0, 0, creatureId);
				game.uglies.add(ugly);
				game.ugliesMap.put(creatureId, ugly);
			}
		}
		Radar[] radars = null;
		List<Vector>[] targets = new List[] {
			List.of(
				new Vector(2500, 9000),
				new Vector(2500, 0)
			), List.of(
				new Vector(7500, 9000),
				new Vector(7500, 0)
			)};
		int[] targetIndex = new int[] {0, 0};
		GamePlayer me = new GamePlayer();
		me.setIIndex(GamePlayer.ME);
		GamePlayer foe = new GamePlayer();
		foe.setIIndex(GamePlayer.FOE);
		game.gamePlayers = List.of(me, foe);
		// game loop
		while (true) {
			int myScore = in.nextInt();
			game.gamePlayers.get(0).setScore(myScore);
			int foeScore = in.nextInt();
			game.gamePlayers.get(1).setScore(foeScore);

			int myScanCount = in.nextInt();
			scans.clear();
			game.visibleFishes.clear();
			game.visibleUglies.clear();

			game.gamePlayers.get(0).scans.clear();
			for (int i = 0; i < myScanCount; i++) {
				int creatureId = in.nextInt();
				game.gamePlayers.get(0).scans.add(new Scan(game.fishesMap.get(creatureId)));
				scans.add(creatureId);
			}
			int foeScanCount = in.nextInt();
			game.gamePlayers.get(1).scans.clear();
			for (int i = 0; i < foeScanCount; i++) {
				int creatureId = in.nextInt();
				game.gamePlayers.get(1).scans.add(new Scan(game.fishesMap.get(creatureId)));
			}
			int myDroneCount = in.nextInt();
			if (FIRST_ROUND) {
				radars = new Radar[myDroneCount];
				for (int i = 0; i < myDroneCount; i++) {
					radars[i] = new Radar();
				}
			}
			for (Radar radar : radars) {
				radar.reset();
			}
			for (int i = 0; i < myDroneCount; i++) {
				int droneId = in.nextInt();
				int droneX = in.nextInt();
				int droneY = in.nextInt();
				int emergency = in.nextInt();
				int battery = in.nextInt();
				if (FIRST_ROUND) {
					myDonesId.add(droneId);
					if (i == 0 && droneX > (Game.WIDTH / 2)) {
						downAndUp.leftIndex = 1;
						System.err.println("switching targets " + droneId + ", " + droneX);
						List<Vector> tmp = targets[0];
						targets[0] = targets[1];
						targets[1] = tmp;
					}
					Drone drone = new Drone(droneX, droneY, droneId, game.gamePlayers.get(0));
					game.gamePlayers.get(0).drones.add(drone);
					game.dronesMap.put(droneId, drone);
					droneIdToIndex.put(droneId, i);
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
					game.dronesMap.put(droneId, drone);
				}
				Drone drone = game.gamePlayers.get(1).drones.get(i);
				drone.pos = new Vector(droneX, droneY);
				drone.battery = battery;
			}
			int droneScanCount = in.nextInt();
			game.dronesMap.values().forEach(drone -> drone.scans.clear());
			for (int i = 0; i < droneScanCount; i++) {
				int droneId = in.nextInt();
				int creatureId = in.nextInt();
//				System.err.println("Scanned " + droneId + "-" + creatureId);
				Scan e = new Scan(game.fishesMap.get(creatureId));
				game.dronesMap.get(droneId).scans.add(e);
				if (myDonesId.contains(droneId)) {
					scans.add(creatureId);
				}
			}
			int visibleCreatureCount = in.nextInt();
			game.fishes.forEach(fish -> fish.speed = null);
			game.uglies.forEach(ugly -> ugly.speed = null);
			for (int i = 0; i < visibleCreatureCount; i++) {
				int creatureId = in.nextInt();
				int creatureX = in.nextInt();
				int creatureY = in.nextInt();
				int creatureVx = in.nextInt();
				int creatureVy = in.nextInt();
				Vector pos = new Vector(creatureX, creatureY);
				Vector speed = new Vector(creatureVx, creatureVy);

				if (game.fishesMap.containsKey(creatureId)) {
					Fish fish = game.fishesMap.get(creatureId);
					fish.pos = pos;
					fish.speed = speed;
					game.visibleFishes.add(fish);
				} else if (game.ugliesMap.containsKey(creatureId)) {
					Ugly ugly = game.ugliesMap.get(creatureId);
					ugly.pos = pos;
					ugly.speed = speed;
					game.visibleUglies.add(ugly);
				}
			}
			int radarBlipCount = in.nextInt();
			for (int i = 0; i < radarBlipCount; i++) {
				int droneId = in.nextInt();
				int creatureId = in.nextInt();
				String radar = in.next();
//				System.err.println(droneId + " -> " + creatureId + " @ " + radar);
				if (!scans.contains(creatureId)) {
					radars[droneIdToIndex.get(droneId)].populate(creatureId, RadarDirection.valueOf(radar));
				}
			}


//			for (int i = 0; i < myDroneCount; i++) {
//				Drone drone = game.gamePlayers.get(0).drones.get(i);

//				Radar radar = radars[i];
//				if (radar == null) {
//					System.out.println("MOVE %d %d %d UP".formatted(
//						(int)drone.getX(),
//						(int)drone.getY() - 800,
//						0
//					));
//					continue;
//				}
//
//				Vector vector = drone.getPos().add(radar.radarDirection().getDirection());
//				boolean turnOnlight = radar.radarDirection() == RadarDirection.TL || radar.radarDirection() == RadarDirection.TR;
//				System.out.println("MOVE %d %d %d Aiming %d".formatted(
//					(int)vector.getX(),
//					(int) vector.getY(),
//					turnOnlight ? 1 : 0,
//					radar.creatureId
//				));

//				List<Vector> vectors = targets[i];
//				Vector vector = vectors.get(targetIndex[i]);
//				if (vector.inRange(drone.pos, 100)) {
//					System.err.println("Next target for " + i + ", " + targetIndex[i]);
//					targetIndex[i]++;
//					if (targetIndex[i] >= vectors.size()) {
//						targetIndex[i] = 0;
//					}
//					vector = vectors.get(targetIndex[i]);
//				}
//				System.out.println("MOVE %d %d %d".formatted(
//					(int)vector.getX(),
//					(int) vector.getY(),
//					Math.random() > 0.10d ? 1 : 0
//				));
//			}
			downAndUp.process(radars, scans);
			FIRST_ROUND = false;
		}
	}

}