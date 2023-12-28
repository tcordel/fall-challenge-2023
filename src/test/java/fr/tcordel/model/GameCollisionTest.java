package fr.tcordel.model;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class GameCollisionTest {

	@Test
	void test() {
		Game game = new Game();
		Drone drone = new Drone(5719,8414,1,new GamePlayer());
//		drone.move = drone.pos.add(RadarDirection.BL.getDirection()).round();
//		game.updateDrone(drone);

		Ugly ugly1 = new Ugly(6263, 8161, 2);
		ugly1.speed = new Vector(-490, 228);
//		Ugly ugly2 = new Ugly(8045, 8366, 3);
//		ugly2.speed = new Vector(-295, 452);

		game.uglies = List.of(ugly1);
		game.visibleUglies = List.of(ugly1);


//		System.err.println(ugly.pos);
//		System.err.println(ugly.speed);
//		System.err.println(drone.pos);
//		System.err.println(drone.move);
//		System.err.println(drone.speed);
//		System.err.println("a" + drone.pos.add(DOWN_LEFT));
//		System.err.println(drone.pos.add(drone.speed.round()));
//		Collision collision = game.getCollision(drone, ugly1);
		DownAndUp downAndUp = new DownAndUp(game);
//		boolean b = downAndUp.moveAndCheckNoCollision(drone, new Vector(5334 - drone.getX(), 7954 - drone.getY()), 0, true);
		boolean b = downAndUp.checkCollision(drone, new Vector(0, -1000));
		Assertions.assertThat(b)
			.isTrue();

		Assertions.assertThat(drone.move)
				.isEqualTo(new Vector(5076, 7648));
}

}