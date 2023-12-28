package fr.tcordel.model;

public abstract class AbstractStrat {

	protected final Game game;
	protected double _5DegToRadians = Math.toRadians(5);
	protected double _15DegToRadians = Math.toRadians(15);

	Vector UP = new Vector(0, -1000);
	Vector DOWN = new Vector(0, 1000);
	Vector DOWN_LEFT = DOWN.rotate(_15DegToRadians).round();
	Vector DOWN_RIGHT = DOWN.rotate(-_15DegToRadians).round();

	protected AbstractStrat(Game game) {this.game = game;}


	boolean moveAndCheckNoCollision(Drone drone, Vector vector, int i, boolean moveDrone) {
		if (moveDrone) {
			drone.move = drone.pos.add(vector.rotate(i * _5DegToRadians)).round();
			game.updateDrone(drone);
		}
		if (game.visibleUglies
			.stream()
			.allMatch(u -> game.getCollision(drone, u) == Collision.NONE)) {
			//			if (i == 0) {
			//					vector.normalize().mult(game.getMoveSpeed(drone));
			//			}
			System.err.println("No Collision spotted : " + drone.id + "," + i + " original vector " + vector);
			return true;
		} else {
//			System.err.println("Collision spotted, new attemps processing for " + drone.id + "," + i);
		}
		return false;
	}

	protected Vector filterDirection(Drone drone, Vector vector, Vector direction, int xLimit, int yLimit) {
		double toXborder = Math.min(vector.getX(), Math.abs(vector.getX() - Game.WIDTH));
		if (toXborder < xLimit) {
			System.err.println("Reset X for drone " + drone.getId());
			direction =  new Vector(0, direction.getY());
		}

		vector = drone.pos.add(direction);
		double toYborder = Game.HEIGHT - vector.getY();
		if (toYborder < yLimit) {
			System.err.println("Reset Y for drone " + drone.getId());
			direction =  new Vector(direction.getX(), 0);
		}
		if (direction.getX() == 0 && direction.getY() == 0) {
			System.err.println("GoingUP for drone " + drone.getId());
			direction = UP;
		}
		return direction;
	}
}
