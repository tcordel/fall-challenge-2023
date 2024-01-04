package fr.tcordel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DownAndUp extends AbstractStrat {

	private final AttackFish attackFish;


	public static final boolean FOE_WINNNING_COUNTER_ATTACK_STRAT = true;
	public static final boolean FOE_WINNNING_COMMIT_STRAT = false;
	public static final boolean ATTACK_RESSOURCE_ON_NO_ALLOCATION = true;

	private final GameEstimator gameEstimator = new GameEstimator();
	private final GameEstimator gameEstimator2 = new GameEstimator();
	public int leftIndex = 0;


	public DownAndUp(Game game) {
		super(game);
		attackFish = new AttackFish(game);
	}

	enum Strat {
		DOWN, UP, ATTACK
	}


	Strat left = Strat.DOWN;
	Strat right = Strat.DOWN;

	boolean[] batterieToogle = new boolean[2];
	boolean[] commit = new boolean[2];
	boolean commitCalled = false;
	boolean hasCommitted = false;

	Integer firstWinningIndex = null;
	List<Set<Integer>> allocations = List.of(new HashSet<>(), new HashSet<>());
	public void process(Radar[] radars, Set<Integer> scans) {
		if (!commitCalled) {
		checkWinningState();
		}
		preAllocate(radars);
		resetCaches();
		Arrays.fill(targets, null);
		for (int i = 0; i < game.gamePlayers.get(GamePlayer.ME).drones.size(); i++) {
			Drone drone = game.gamePlayers.get(GamePlayer.ME).drones.get(i);
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

	private void resetCaches() {
		for (Drone drone : game.gamePlayers.get(GamePlayer.ME)
			.drones) {
			if (drone.target != null && (
				!drone.getRadar().containsKey(drone.target.id)
				|| game.gamePlayers.get(GamePlayer.FOE).scans.contains(new Scan(drone.target))
				|| game.gamePlayers.get(GamePlayer.FOE).drones.stream().anyMatch(d -> d.scans.contains(new Scan(drone.target)))
			)) {
				drone.target = null;
			}
		}
	}

	private void checkWinningState() {
		if (game.gamePlayers.get(GamePlayer.ME).getScore() > 0 ||
			game.gamePlayers.get(GamePlayer.FOE).getScore() > 0 ) {
			System.err.println("No winning state SCORE");
			return;
		}

		if (left == Strat.ATTACK || right == Strat.ATTACK) {
			System.err.println("No winning state ATTACK");
			return;
		}

		boolean foeCommitting = game.gamePlayers
			.get(GamePlayer.FOE)
			.drones
			.stream()
//			.peek(drone -> System.err.println("Speed " + drone.id + " -> " + drone.speed))
			.map(d -> d.speed)
			.allMatch(s -> s.equals(Vector.UP));

		gameEstimator.reset();
		gameEstimator2.reset();

		Set<Scan> scans = game.gamePlayers.get(GamePlayer.ME).drones.stream().flatMap(drone -> drone.scans.stream())
			.collect(Collectors.toSet());
		Set<Scan> scansFoe = game.gamePlayers.get(GamePlayer.FOE).drones.stream().flatMap(drone -> drone.scans.stream())
			.collect(Collectors.toSet());
		int myCommitPoint = gameEstimator.computeScanScore(scans, game.gamePlayers.get(GamePlayer.ME).getIndex());
//		gameEstimator.computeScanScore(scansFoe, game.gamePlayers.get(GamePlayer.FOE).getIndex());
		int oppMaxScore = gameEstimator.computeFullEndGameScore(game.gamePlayers.get(GamePlayer.FOE));
		int myScoreCommittingFirst = gameEstimator.computeFullEndGameScore(game.gamePlayers.get(GamePlayer.ME));

		int himCommintPoint = gameEstimator2.computeScanScore(scansFoe, game.gamePlayers.get(GamePlayer.FOE).getIndex());
//		gameEstimator2.computeScanScore(scans, game.gamePlayers.get(GamePlayer.ME).getIndex());
		int myScoreCommittingFirst2 = gameEstimator2.computeFullEndGameScore(game.gamePlayers.get(GamePlayer.ME));
		int oppMaxScore2 = gameEstimator2.computeFullEndGameScore(game.gamePlayers.get(GamePlayer.FOE));
		System.err.println("Committing me:%d, him %d".formatted(myCommitPoint, himCommintPoint));
		System.err.println("EndGame estimation : I commit me:%d, him %d".formatted(myScoreCommittingFirst, oppMaxScore));
		System.err.println("EndGame estimation : FOE commit me:%d, him %d".formatted(myScoreCommittingFirst2, oppMaxScore2));

		boolean iWillCommitFirst = !foeCommitting ||
								   game.gamePlayers
									   .get(GamePlayer.ME)
									   .drones.stream()
									   .map(d -> d.pos)
									   .anyMatch(p -> p.getX() <= (Game.DRONE_MOVE_SPEED / 2) + game.gamePlayers
										   .get(GamePlayer.FOE)
										   .drones.stream()
										   .map(d -> d.pos)
										   .mapToDouble(Vector::getX)
										   .min().orElse(Double.MAX_VALUE));
		boolean iWin = myScoreCommittingFirst > oppMaxScore;
		boolean foeWins = oppMaxScore2 > myScoreCommittingFirst2;
		if (firstWinningIndex == null) {
			if (iWin) {
				firstWinningIndex = GamePlayer.ME;
			} else if (foeWins) {
				firstWinningIndex = GamePlayer.FOE;
			}
		}
		if (isWinning(GamePlayer.ME)) {
			System.err.println("COMMIT !!");
			commitCalled = true;
			commit[0] = true;
			commit[1] = true;
		}
		//		if (FOE_WINNNING_COUNTER_ATTACK_STRAT && isWinning(GamePlayer.FOE)) {
		//			System.err.println("Loosing, so attacking whatever i can");
		//			left = Strat.ATTACK;
		//			right = Strat.ATTACK;
		//		}
		if (FOE_WINNNING_COMMIT_STRAT && isWinning(GamePlayer.FOE)) {
			System.err.println("OPP can win :(");
			Map<Integer, List<Drone>> list = game.gamePlayers
				.stream()
				.flatMap(g -> g.drones.stream())
				.collect(Collectors.toMap(a -> (int)(a.getY() / Game.DRONE_MOVE_SPEED), drone -> {
					List<Drone> arrayList = new ArrayList<>();
					arrayList.add(drone);
					return arrayList;
				}, (a, b) -> {
					a.addAll(b);
					return a;
				}));

			gameEstimator.reset();
			list.keySet().stream().sorted().forEach(key -> {
				List<Drone> drones = list.get(key);
				Set<Scan> myScans = drones.stream().filter(d -> d.getOwner().getIndex() == 0)
					.flatMap(drone -> drone.scans.stream())
					.collect(Collectors.toSet());
				Set<Scan> oppScans = drones.stream().filter(d -> d.getOwner().getIndex() == 1)
					.flatMap(drone -> drone.scans.stream())
					.collect(Collectors.toSet());
				gameEstimator.commit(myScans, oppScans);
			});
			int myScore = gameEstimator.computeFullEndGameScore(game.gamePlayers.get(GamePlayer.ME));
			int foeScore = gameEstimator.computeFullEndGameScore(game.gamePlayers.get(GamePlayer.FOE));
			if (myScore >= foeScore) {
				System.err.println("Rushing toward surface %d vs %d".formatted(myScore, foeScore));
				commitCalled = true;
				commit[0] = true;
				commit[1] = true;
			} else {
				System.err.println("OPP will win :( %d vs %d".formatted(myScore, foeScore));
			}
		}

		// Si je commit avant l'autre :
		// point bonus  moi
		// estimer la perte potentiel de point bonus (premier + combo) pour l'advesaire.

		// --> le reste ne permet pas de rattraper les données commit + bonus + le reste à récupérer.

		// mon score commit premier + reste potentiel second > reste potentiel premier pour l'autre.

	}

	private boolean isWinning(int index) {
		return firstWinningIndex != null && firstWinningIndex == index;
	}

	private int switchOn(int i, Drone drone, Radar radar, FishType target, boolean escaping) {
		boolean lightOn = false;
		game.updateDrone(drone);
		boolean needLight = game.uglies.stream().anyMatch(ugly -> ugly.pos == null)
			|| game.fishes.stream().anyMatch(fish -> fish.pos == null)
			|| game.fishes.stream().anyMatch(fish -> {
			Scan scan = new Scan(fish);
			if (drone.scans.contains(scan) || game.gamePlayers.get(GamePlayer.ME).scans.contains(scan)) {
					return false;
				}
			double distance = drone.move.distance(fish.pos);
			return distance > (Game.DARK_SCAN_RANGE - Game.FISH_FLEE_SPEED)
				   && distance < (Game.LIGHT_SCAN_RANGE);
		});
		if (
//			!escaping&&
			drone.getY() >= FishType.JELLY.getUpperLimit()
			&& (!batterieToogle[i] || (drone.getY() > 6200))
			&& isInRange(drone, radar.getTypes(game.fishesMap))
			&& needLight
		) {
			lightOn = true;
		}
		batterieToogle[i] = lightOn;
		return lightOn ? 1 : 0;
	}

	private boolean isInRange(Drone drone, Set<FishType> target) {
		FishType zoneType = FishType.forY(drone.move.getY(), game.getMoveSpeed(drone) / 2);
		if (zoneType == null) {
			return false;
		}
		return target.stream().anyMatch(fishType -> fishType.equals(zoneType));
//		return drone.getY() >= 3000;
	}

	private void preAllocate(Radar[] radars) {
		if (!Player.FIRST_ROUND && !hasCommitted) {
			for (int i = 0; i < game.gamePlayers.get(GamePlayer.ME).drones.size(); i++) {
				hasCommitted = game.gamePlayers.get(GamePlayer.ME).drones.stream().anyMatch(drone -> drone.getY() <= Game.DRONE_START_Y);
			}
		}
		for (int i = 0; i < game.gamePlayers.get(GamePlayer.ME).drones.size(); i++) {
			allocations.get(i).clear();
		}
		if (hasCommitted) {
			return;
		}
		for (int i = 0; i < game.gamePlayers.get(GamePlayer.ME).drones.size(); i++) {
			boolean isLeft = leftIndex == i;
			allocations.get(i).addAll(isLeft ? radars[i].bottomLeft : radars[i].bottomRight);
			allocations.get(i).addAll(isLeft ? radars[i].topLeft : radars[i].topRight);
		}

	}

	public boolean checkCollision(Drone drone, Vector vector) {

		for (int i = 0; i < 360; i++) {
			int offset = (i % 2 > 0 ? 1 : -1) * (i / 2);
			if (moveAndCheckNoCollision(drone, vector, offset, true))
				return i > 0;
		}
		System.err.println("No escape found for drone " + drone.id + ", " + drone.pos + "@" + vector);
		drone.move = drone.pos.add(vector);
		return true;
	}


	FishType[] targets = new FishType[2];
	private Vector getVector(Radar[] radars, Set<Integer> scans, int i, Drone drone) {
		Radar radarForDrone = radars[i];
		boolean isLeft = leftIndex == i;

		Vector direction = null;

		if ((isLeft ? left : right) == Strat.ATTACK) {
			direction = applyAttackStrat(drone, isLeft);
		}
		if (direction != null) {
			return direction;
		}

		RadarDirection rd = null;
		Radar radarForType;
		FishType target = null;

//		Optional<Fish> fishToAttack = drone.scans.stream()
//			.filter(scan -> game.gamePlayers.get(GamePlayer.FOE).drones.stream().noneMatch(opp -> opp.scans.contains(scan)))
//			.map(scan -> game.fishesMap.get(scan.fishId))
//			.filter(fish -> isLeft ? (fish.getX() < Game.FISH_FLEE_SPEED * 2) : (fish.getX() > (Game.WIDTH - Game.FISH_FLEE_SPEED * 2)))
//			.filter(fish -> fish.pos.euclideanTo(drone.pos) < (Game.FISH_HEARING_RANGE + Game.DRONE_MOVE_SPEED))
//			.filter(fish -> game.gamePlayers.get(GamePlayer.FOE).drones.stream()
//				.allMatch(opp -> fish.pos.euclideanTo(opp.pos) > (Game.LIGHT_SCAN_RANGE + Game.DRONE_MOVE_SPEED - Game.FISH_FLEE_SPEED)))
//			.findFirst();
//
//		if (fishToAttack.isPresent()) {
//			direction = applyAttackStrat(drone, isLeft);
//			if (direction != null) {
//				return direction;
//			}
//		}

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

		if (rd == null) {
			if (drone.scans.isEmpty() && (ATTACK_RESSOURCE_ON_NO_ALLOCATION
				|| (FOE_WINNNING_COUNTER_ATTACK_STRAT && isWinning(GamePlayer.FOE)))) {
				direction = applyAttackStrat(drone, isLeft);
				if (direction != null) {
					return direction;
				}
			}
			return UP;
		}

		targets[i] = target;
		int threshold = game.getMoveSpeed(drone) / 2;
		if ((drone.getY() - threshold) >= target.getDeeperLimit() || (drone.getY() + threshold) <= target.getUpperLimit()) {
			System.err.println(drone.getId() + " depth too far from target type " + target);
			return switch (rd) {
				case BL -> drone.getX() > Game.FISH_HEARING_RANGE ? DOWN_LEFT : DOWN;
				case BR -> drone.getX() < (Game.WIDTH - Game.FISH_HEARING_RANGE) ? DOWN_RIGHT : DOWN;
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
		direction = getFilteredVector(drone, rd.getDirection(), 1000, 500);

		return direction;
	}

	private Vector applyAttackStrat(Drone drone, boolean isLeft) {
		Vector direction = null;
		if (drone.target == null) {
			drone.target = game.fishes.stream()
				.filter(f -> drone.getRadar().containsKey(f.id))
				.filter(f -> game.gamePlayers.get(GamePlayer.ME).drones.stream().allMatch(d -> d.target != f))
				.filter(f -> !game.gamePlayers.get(GamePlayer.FOE).scans.contains(new Scan(f)))
				.filter(f -> game.gamePlayers.get(GamePlayer.FOE).drones.stream().noneMatch(d -> d.scans.contains(new Scan(f))))
				.sorted(Comparator.comparingDouble((Fish f) -> f.getPos() == null ? Double.MAX_VALUE : f.getPos().manhattanTo(drone.getPos()))
					.thenComparing(f -> -f.getType().ordinal()))
				.findFirst().orElse(null);
		}
		if (drone.target != null) {
			direction = attackFish.process(drone.target, drone);
		} else if (isLeft) {
			left = Strat.UP;
		} else {
			right = Strat.UP;
		}
		if (direction != null) {
			boolean collision = !moveAndCheckNoCollision(drone, direction, 0, true);
			System.err.println("Attacking collision check for drone " + drone.id + ": " + collision);
			if (collision) {
				Vector direction2 = filterDirection(drone, drone.pos.add(direction), direction, 0, Game.HEIGHT);
				System.err.println("Collision ! " + direction + "->" + direction2);
				direction = direction2;
			}
		}
		return direction;
	}

	private Vector getFilteredVector(Drone drone, Vector direction, int xLimit, int yLimit) {
		if (direction == null) {
			return direction;
		}
		Vector vector = drone.pos.add(direction);
		drone.move = vector;
		game.updateDrone(drone);
		boolean processFilter = game.uglies
									.stream()
									.filter(ugly -> ugly.pos != null)
									.allMatch(u -> game.getCollision(drone, u) == Collision.NONE);

		if (!processFilter) {
			System.err.println("Filter border target only for " + drone.id + " due to collision");
			xLimit = 0;
			yLimit = Game.HEIGHT;
		}

		return filterDirection(drone, vector, direction, xLimit, yLimit);
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
}
