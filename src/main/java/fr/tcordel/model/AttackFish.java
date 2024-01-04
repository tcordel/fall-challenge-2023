package fr.tcordel.model;

public class AttackFish extends AbstractStrat {

	//seed=2740983747143833600
	public AttackFish(Game game) {super(game);}

	Vector process(Fish fish, Drone drone) {
		Vector target = null;
		Vector pos = null;
		boolean visible = false;
		if (fish.pos != null) {
			visible = true;
			//			game.updateSingleFleeingFish(fish, drone);
			pos = game.snapToFishZone(fish.pos.add(fish.speed), fish);
			int offset = ((pos.getX() < (Game.WIDTH / 2)) ?  1 : -1) * Game.FISH_AVOID_RANGE;
			target = new Vector((int)pos.getX() + offset - drone.getX(),
				(int)pos.getY() - drone.getY());
		} else {
			target = drone.getRadar().get(fish.id)
				.getDirection();
		}
		System.err.printf("Drone %d attacking %d, f.p %s, f.v %s, t %s %b%n", drone.id, fish.id, pos, fish.speed, target, visible);
		return target;
	}
}
