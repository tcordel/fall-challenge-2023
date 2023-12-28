package fr.tcordel.model;

public class AttackFish extends AbstractStrat {

	//seed=2740983747143833600
	public AttackFish(Game game) {super(game);}

	Vector process(Fish fish, Drone drone) {
		Vector target = null;
		boolean visible = false;
		if (game.visibleFishes.contains(fish)) {
			visible = true;
			//			game.updateSingleFleeingFish(fish, drone);
			int offset = ((fish.pos.getX() < (Game.WIDTH / 2)) ?  1 : -1) * 200;
			target = new Vector((int)fish.pos.getX() + offset - drone.getX(),
				(int)fish.pos.getY() - drone.getY());
		} else {
			target = drone.getRadar().get(fish.id)
				.getDirection();
		}
		System.err.println("Drone %d attacking %d, t %s %b".formatted(drone.id, fish.id, target, visible));
		return target;
	}
}
