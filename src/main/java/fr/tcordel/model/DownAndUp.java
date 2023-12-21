package fr.tcordel.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
public class DownAndUp {

	Vector UP = new Vector(0, -1000);
	Vector DOWN = new Vector(0, 1000);

	private final Game game;
	public int leftIndex = 0;
	private double twentyDegToRadians = Math.toRadians(20);

	public DownAndUp(Game game) {
		this.game = game;
	}

	enum Strat {
		DOWN, UP;
	}

	Strat left = Strat.DOWN;
	Strat right = Strat.DOWN;
	List<Set<Integer>> allocations = List.of(new HashSet<>(), new HashSet<>());
	public void process(Radar[] radars, Set<Integer> scans) {
		preAllocate(radars);
		for (int i = 0; i < game.gamePlayers.get(0).drones.size(); i++) {
			Drone drone = game.gamePlayers.get(0).drones.get(i);
			Vector vector = getVector(radars, scans, i, drone);
			checkCollision(drone, vector);
			System.out.printf(
				"MOVE %d %d %d %n", (int)drone.move.getX(),
				(int)drone.move.getY(),
				Math.random() > 0.20d ? 1 : 0
								);
		}
	}

	private void preAllocate(Radar[] radars) {
		for (int i = 0; i < game.gamePlayers.get(0).drones.size(); i++) {
			boolean isLeft = leftIndex == i;
			allocations.get(i).clear();
			allocations.get(i).addAll(isLeft ? radars[i].bottomLeft : radars[i].bottomRight);
			allocations.get(i).addAll(isLeft ? radars[i].topLeft : radars[i].topRight);
		}

	}

	private void checkCollision(Drone drone, Vector vector) {

		// bug : seed=-175664990971267260
		for (int i = 0; i < 5; i++) {
			if (moveAndCheckNoCollision(drone, vector, i))
				return;
		}

		for (int i = -1; i > -5; i--) {
			if (moveAndCheckNoCollision(drone, vector, i))
				return;
		}

		for (int i = 5; i < 10; i++) {
			if (moveAndCheckNoCollision(drone, vector, i))
				return;
		}

		for (int i = -5; i > -10; i--) {
			if (moveAndCheckNoCollision(drone, vector, i))
				return;
		}

		for (int i = 10; i < 15; i++) {
			if (moveAndCheckNoCollision(drone, vector, i))
				return;
		}

		for (int i = -10; i > -15; i--) {
			if (moveAndCheckNoCollision(drone, vector, i))
				return;
		}
		drone.move = drone.pos.add(vector);
	}

	private boolean moveAndCheckNoCollision(Drone drone, Vector vector, int i) {
		drone.move = drone.pos.add(vector.rotate(i * twentyDegToRadians));
		game.updateDrone(drone);
		if (game.visibleUglies
			.stream()
			.allMatch(u -> game.getCollision(drone, u) == Collision.NONE)) {
			return true;
		} else {
			System.err.println("Collision spotted, new attemps processing for " + drone.id + "," + i );
		}
		return false;
	}

	private Vector getVector(Radar[] radars, Set<Integer> scans, int i, Drone drone) {
		Radar radar = radars[i];
		boolean isLeft = leftIndex == i;

		RadarDirection rd = null;
		FishType targetting = null;
		for (FishType fishType : FishType.FISH_ORDERED) {
			targetting = fishType;
			RadarDirection radarDirection = isLeft ? RadarDirection.BL : RadarDirection.BR;
			rd = checkForType(i, fishType, isLeft ? radar.bottomLeft : radar.bottomRight, radarDirection);
			if (rd != null) {
				System.err.println(radarDirection + " for " + drone.id + " aiming for " + fishType);
				break;
			}
			radarDirection = !isLeft ? RadarDirection.BL : RadarDirection.BR;
			rd = checkForType(i, fishType, !isLeft ? radar.bottomLeft : radar.bottomRight, radarDirection);
			if (rd != null) {
				System.err.println(radarDirection + " for " + drone.id + " aiming for " + fishType);
				break;
			}
			radarDirection = isLeft ? RadarDirection.TL : RadarDirection.TR;
			rd = checkForType(i, fishType, isLeft ? radar.topLeft : radar.topRight, radarDirection);
			if (rd != null) {
				System.err.println(radarDirection + " for " + drone.id + " aiming for " + fishType);
				break;
			}
			radarDirection = !isLeft ? RadarDirection.TL : RadarDirection.TR;
			rd = checkForType(i, fishType, !isLeft ? radar.topLeft : radar.topRight, radarDirection);
			if (rd != null) {
				System.err.println(radarDirection + " for " + drone.id + " aiming for " + fishType);
				break;
			}
		}

//		Strat newStrat = checkStrat(drone, isLeft ? left : right);
//		if (isLeft) {
//			left = newStrat;
//		} else {
//			right = newStrat;
//		}
		if (rd == null) {
			return UP;
		}

		int threshold = game.getMoveSpeed(drone) / 2;
		if ((drone.getY() + threshold) >= targetting.getDeeperLimit() || (drone.getY() - threshold) <= targetting.getUpperLimit()) {
			System.err.println(drone.getId() + " depth too far from target type " + targetting);
			return switch (rd) {
				case BL -> DOWN;
				case BR -> DOWN;
				case TL -> UP;
				case TR -> UP;
			};
		}


//		boolean goToCenter = isLeft;
		//		List<Integer> integers = isLeft ? radar.bottomLeft : radar.bottomRight;
		//		for (Integer integer : integers) {
		//			if (game.fishesMap.containsKey(integer)) {
		//				goToCenter = !goToCenter;
		//				break;
		//			}
		//		}
		//		RadarDirection rd = goToCenter ? RadarDirection.BR : RadarDirection.BL;
		Vector direction = rd.getDirection();
		Vector vector = drone.pos.add(direction);
		double toXborder = Math.min(vector.getX(), Math.abs(vector.getX() - Game.WIDTH));
		if (toXborder < 1000) {
			System.err.println("Reset X for drone " + drone.getId());
			direction =  new Vector(0, direction.getY());
		}

		vector = drone.pos.add(direction);
		double toYborder =  Game.HEIGHT - vector.getY();
		if (toYborder < 1000) {
			System.err.println("Reset Y for drone " + drone.getId());
			direction =  new Vector(direction.getX(), 0);
		}
		if (direction.getX() == 0 && direction.getY() == 0) {
			System.err.println("GoingUP for drone " + drone.getId());
			direction = UP;
		}

		return direction;
	}

	private RadarDirection checkForType(int i, FishType fishType, List<Integer> integers, RadarDirection radarDirection) {
		Optional<Fish> any = integers.stream()
			.filter(integer -> !allocations.get(Math.abs(i - 1)).contains(integer))
			.map(integer -> game.fishesMap.get(integer))
			.filter(Objects::nonNull)
			.filter(fish -> fish.type == fishType)
			.findAny();
		if (any.isPresent()) {
			allocations.get(i).add(any.get().getId());
			return radarDirection;
		}
		return null;
	}

	private Strat checkStrat(Drone drone, Strat strat) {
		return switch (strat) {
			case UP -> drone.getY() < 500 ? Strat.DOWN : Strat.UP;
			case DOWN -> drone.getY() > 7400 ? Strat.UP : Strat.DOWN;
		};
	}
}
