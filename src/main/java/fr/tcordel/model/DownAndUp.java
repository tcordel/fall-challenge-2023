package fr.tcordel.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DownAndUp {

	Vector UP = new Vector(0, -1000);
	Vector DOWN = new Vector(0, 1000);

	private final Game game;
	private final GameEstimator gameEstimator = new GameEstimator();
	private final GameEstimator gameEstimator2 = new GameEstimator();
	public int leftIndex = 0;
	private double tenDegToRadians = Math.toRadians(10);

	public DownAndUp(Game game) {
		this.game = game;
	}

	enum Strat {
		DOWN, UP;
	}


	Strat left = Strat.DOWN;
	Strat right = Strat.DOWN;

	boolean[] batterieToogle = new boolean[2];
	boolean[] commit = new boolean[2];
	boolean commitCalled = false;
	boolean hasCommitted = false;
	List<Set<Integer>> allocations = List.of(new HashSet<>(), new HashSet<>());
	public void process(Radar[] radars, Set<Integer> scans) {
		if (!commitCalled) {
			checkWinningState();
		}
		preAllocate(radars);
		Arrays.fill(targets, null);
		for (int i = 0; i < game.gamePlayers.get(0).drones.size(); i++) {
			Drone drone = game.gamePlayers.get(0).drones.get(i);
			commit[i] = commit[i] && drone.getY() > Game.DRONE_START_Y;
			Vector vector = commit[i] ? UP : getVector(radars, scans, i, drone);
			boolean escaping = checkCollision(drone, vector);
			System.out.printf(
				"MOVE %d %d %d %s%s%n", (int)drone.move.getX(),
				(int)drone.move.getY(),
				switchOn(i, drone, radars[i], targets[i], escaping),
				(commit[i] ? "🏆" : "🤑"),
				escaping ? "🥵" : "🤗"
								);
		}
	}

	private void checkWinningState() {
		if (game.gamePlayers.get(0).getScore() > 0 ||
			game.gamePlayers.get(1).getScore() > 0 ) {
			return;
		}

		gameEstimator.reset();
		gameEstimator2.reset();

		Set<Scan> scans = game.gamePlayers.get(0).drones.stream().flatMap(drone -> drone.scans.stream())
			.collect(Collectors.toSet());
		Set<Scan> scansFoe = game.gamePlayers.get(1).drones.stream().flatMap(drone -> drone.scans.stream())
			.collect(Collectors.toSet());
		int myCommitPoint = gameEstimator.computeScanScore(scans, game.gamePlayers.get(0).getIndex());
		int himCommintPoint = gameEstimator2.computeScanScore(scansFoe, game.gamePlayers.get(1).getIndex());
		int oppMaxScore = gameEstimator.computeEndGameScore(game.gamePlayers.get(1).getIndex());
		int myScoreCommittingFirst = gameEstimator.computeEndGameScore(game.gamePlayers.get(0).getIndex());
		System.err.println("Committing me:%d, him %d".formatted(myCommitPoint, himCommintPoint));
		System.err.println("EndGame estimation me:%d, him %d".formatted(myScoreCommittingFirst, oppMaxScore));

		if (myScoreCommittingFirst > oppMaxScore) {
			commitCalled = true;
			commit[0] = true;
			commit[1] = true;
		}
		// Si je commit avant l'autre :
		// point bonus  moi
		// estimer la perte potentiel de point bonus (premier + combo) pour l'advesaire.

		// --> le reste ne permet pas de rattraper les données commit + bonus + le reste à récupérer.

		// mon score commit premier + reste potentiel second > reste potentiel premier pour l'autre.

	}

	private int switchOn(int i, Drone drone, Radar radar, FishType target, boolean escaping) {
		boolean lightOn = false;
		if (!escaping
			&& !batterieToogle[i]
			&& drone.getY() >= 3000) {
//			&& isInRange(drone, radar.getTypes(game.fishesMap))) {
			lightOn = true;
		}
		batterieToogle[i] = lightOn;
		return lightOn ? 1 : 0;
	}

	private boolean isInRange(Drone drone, Set<FishType> target) {
		game.updateDrone(drone);
		FishType zoneType = FishType.forY(drone.getY(), game.getMoveSpeed(drone) / 3);
		if (zoneType == null) {
			return false;
		}
		return target.stream().anyMatch(fishType -> fishType.equals(zoneType));
//		return drone.getY() >= 3000;
	}

	private void preAllocate(Radar[] radars) {
		if (!Player.FIRST_ROUND && !hasCommitted) {
			for (int i = 0; i < game.gamePlayers.get(0).drones.size(); i++) {
				hasCommitted = game.gamePlayers.get(0).drones.stream().anyMatch(drone -> drone.getY() <= Game.DRONE_START_Y);
			}
		}
		for (int i = 0; i < game.gamePlayers.get(0).drones.size(); i++) {
			allocations.get(i).clear();
		}
		if (hasCommitted) {
			return;
		}
		for (int i = 0; i < game.gamePlayers.get(0).drones.size(); i++) {
			boolean isLeft = leftIndex == i;
			allocations.get(i).addAll(isLeft ? radars[i].bottomLeft : radars[i].bottomRight);
			allocations.get(i).addAll(isLeft ? radars[i].topLeft : radars[i].topRight);
		}

	}

	private boolean checkCollision(Drone drone, Vector vector) {

		for (int i = 0; i < 60; i++) {
			int offset = (i % 2 > 0 ? 1 : -1) * i;
			if (moveAndCheckNoCollision(drone, vector, offset))
				return i > 0;
		}
		drone.move = drone.pos.add(vector);
		return true;
	}

	private boolean moveAndCheckNoCollision(Drone drone, Vector vector, int i) {
		drone.move = drone.pos.add(vector.rotate(i * tenDegToRadians));
		game.updateDrone(drone);
		if (game.visibleUglies
			.stream()
			.allMatch(u -> game.getCollision(drone, u) == Collision.NONE)) {
			return true;
			//		} else {
			//			System.err.println("Collision spotted, new attemps processing for " + drone.id + "," + i );
		}
		return false;
	}

	FishType[] targets = new FishType[2];
	private Vector getVector(Radar[] radars, Set<Integer> scans, int i, Drone drone) {
		Radar radarForDrone = radars[i];
		boolean isLeft = leftIndex == i;

		RadarDirection rd = null;
		Radar radarForType;
		FishType target = null;
		for (FishType fishType : FishType.FISH_ORDERED) {
			radarForType = radarForDrone.forType(game.fishesMap, fishType);
			target = fishType;
			RadarDirection radarDirection = isLeft ? RadarDirection.BL : RadarDirection.BR;
			rd = checkForType(i, isLeft ? radarForType.bottomLeft : radarForType.bottomRight, radarDirection);
			if (rd != null) {
				System.err.println(radarDirection + " for " + drone.id + " aiming for " + fishType);
				break;
			}
			radarDirection = !isLeft ? RadarDirection.BL : RadarDirection.BR;
			rd = checkForType(i, !isLeft ? radarForType.bottomLeft : radarForType.bottomRight, radarDirection);
			if (rd != null) {
				System.err.println(radarDirection + " for " + drone.id + " aiming for " + fishType);
				break;
			}
			radarDirection = isLeft ? RadarDirection.TL : RadarDirection.TR;
			rd = checkForType(i, isLeft ? radarForType.topLeft : radarForType.topRight, radarDirection);
			if (rd != null) {
				System.err.println(radarDirection + " for " + drone.id + " aiming for " + fishType);
				break;
			}
			radarDirection = !isLeft ? RadarDirection.TL : RadarDirection.TR;
			rd = checkForType(i, !isLeft ? radarForType.topLeft : radarForType.topRight, radarDirection);
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

		targets[i] = target;
		int threshold = game.getMoveSpeed(drone) / 2;
		if ((drone.getY() - threshold) >= target.getDeeperLimit() || (drone.getY() + threshold) <= target.getUpperLimit()) {
			System.err.println(drone.getId() + " depth too far from target type " + target);
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
		Vector direction = getFilteredVector(drone, rd);

		return direction;
	}

	private Vector getFilteredVector(Drone drone, RadarDirection rd) {
		Vector direction = rd.getDirection();
		Vector vector = drone.pos.add(direction);
		drone.move = vector;
		boolean processFilter = game.visibleUglies.isEmpty() ||
								game.visibleUglies
									.stream()
									.allMatch(u -> game.getCollision(drone, u) == Collision.NONE);

		if (!processFilter) {
			System.err.println("No filter target for " + drone.id + " due to collision");
			return direction;
		}


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

	private RadarDirection checkForType(int i, List<Integer> integers, RadarDirection radarDirection) {
		Optional<Integer> any = integers.stream()
			.filter(integer -> !allocations.get(Math.abs(i - 1)).contains(integer))
			.findAny();
		if (any.isPresent()) {
			allocations.get(i).add(any.get());
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
