package fr.tcordel.model;

import java.util.*;

public class Player {

	static Game game = new Game();
	public static boolean FIRST_ROUND = true;

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int creatureCount = in.nextInt();
		game.fishes = new ArrayList<>(creatureCount);
		game.uglies = new ArrayList<>(creatureCount);
		Set<Integer> scans = new HashSet<>();
		Set<Integer> visibleUnits = new HashSet<>();
		var fishes = new HashMap<Integer, Fish>();
		var uglies = new HashMap<Integer, Ugly>();
		var drones = new HashMap<Integer, Drone>();

		Radar[] radars = new Radar[2];
		Map<Integer, Integer> droneIdToIndex = new HashMap<>();

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

		//		List<List<Vector>> targets = List.of(
		//			List.of(
		//		new Vector(8500, 5000),
		//		new Vector(8500, 8500),
		//		new Vector(1500, 8500),
		//			new Vector(1500, 5000),
		//			new Vector(5000, 0)
		//		), List.of(
		//				new Vector(1500, 5000),
		//				new Vector(8500, 5000),
		//				new Vector(5000, 0)
		//			));
		//		int[] targetIndex = new int[] {0, 0};
		game.gamePlayers = List.of(new GamePlayer(), new GamePlayer());
		// game loop
		while (true) {
			int myScore = in.nextInt();
			game.gamePlayers.get(0).setScore(myScore);
			int foeScore = in.nextInt();
			game.gamePlayers.get(1).setScore(foeScore);

			int myScanCount = in.nextInt();
			scans.clear();
			visibleUnits.clear();
			Arrays.fill(radars, null);
			game.gamePlayers.get(0).scans.clear();
			for (int i = 0; i < myScanCount; i++) {
				int creatureId = in.nextInt();
				game.gamePlayers.get(0).scans.add(new Scan(fishes.get(creatureId)));
				scans.add(creatureId);
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
				scans.add(creatureId);
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
				visibleUnits.add(creatureId);
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
				System.err.println(droneId + " -> " + creatureId + " @ " + radar);
				if (fishes.containsKey(creatureId)
					&& radars[droneIdToIndex.get(droneId)] == null) {
					radars[droneIdToIndex.get(droneId)] = new Radar(creatureId, RadarDirection.valueOf(radar));
				}
			}

			for (int i = 0; i < myDroneCount; i++) {
				Drone drone = game.gamePlayers.get(0).drones.get(i);

				Radar radar = radars[i];
				if (radar == null) {
					System.out.println("MOVE %d %d %d UP".formatted(
						(int)drone.getX(),
						(int)drone.getY() - 800,
						0
					));
					continue;
				}

				Vector vector = drone.getPos().add(radar.radarDirection().getDirection());
				boolean turnOnlight = radar.radarDirection() == RadarDirection.TL || radar.radarDirection() == RadarDirection.TR;
				System.out.println("MOVE %d %d %d Aiming %d".formatted(
					(int)vector.getX(),
					(int) vector.getY(),
					turnOnlight ? 1 : 0,
					radar.creatureId
				));
			}
		}
	}

	record Radar(int creatureId, RadarDirection radarDirection) {
	}
}