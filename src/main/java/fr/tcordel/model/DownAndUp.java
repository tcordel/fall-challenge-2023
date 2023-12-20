package fr.tcordel.model;

import java.util.List;
import java.util.Set;

public class DownAndUp {

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

	public void process(Radar[] radars, Set<Integer> scans) {
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


	private void checkCollision(Drone drone, Vector vector) {
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
		Strat newStrat = checkStrat(drone, isLeft ? left : right);
		if (isLeft) {
			left = newStrat;
		} else {
			right = newStrat;
		}
		if (newStrat == Strat.UP) {
			return new Vector(0, -game.getMoveSpeed(drone));
		}
		boolean goToCenter = isLeft;
		List<Integer> integers = isLeft ? radar.bottomLeft : radar.bottomRight;
		integers.removeAll(scans);
		for (Integer integer : integers) {
			if (game.fishesMap.containsKey(integer)) {
				goToCenter = !goToCenter;
				break;
			}
		}
		RadarDirection rd = goToCenter ? RadarDirection.BR : RadarDirection.BL;
		Vector vector = drone.pos.add(rd.getDirection());
		double toXborder = Math.min(vector.getX(), Math.abs(vector.getX() - Game.WIDTH));
		if (toXborder < 2200) {
			return new Vector(0, game.getMoveSpeed(drone));
		} else {
			return rd.getDirection();
		}

	}

	private Strat checkStrat(Drone drone, Strat strat) {
		return switch (strat) {
			case UP -> drone.getY() < 500 ? Strat.DOWN : Strat.UP;
			case DOWN -> drone.getY() > 7400 ? Strat.UP : Strat.DOWN;
		};
	}
}
