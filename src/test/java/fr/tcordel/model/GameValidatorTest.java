package fr.tcordel.model;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameValidatorTest {

	double[][] myDrone = new double[][] {
		{3333.0,9999.0}
	};

	double[][] foeDrone = new double[][] {
		{6666.0,9999.0}
	};
	double[][] fishExpected = new double[][] {
		{2457.0, 3294.0},
		{7542.0, 3294.0},
		{1401.0, 5221.0},
		{8598.0, 5221.0},
		{854.0, 9648.0},
		{9145.0, 9648.0},
		{9730.0, 3687.0},
		{269.0, 3687.0},
		{2909.0, 6760.0},
		{7090.0, 6760.0},
		{5318.0, 9395.0},
		{4681.0, 9395.0},
	};

	@Test
	void testNoRegression() throws NoSuchAlgorithmException {
		Game game = GameUtils.initGame(-8103073722892937000L);

		for (int i = 0; i < Game.MAX_TURNS; i++) {
			game.performGameUpdate(i);
		}

		for (int i = 0; i < game.fishes.size(); i++) {
			Fish fish = game.fishes.get(i);
//			System.err.println("{" + fish.getX() + "," + fish.getY() + "},");
						assertThat(fish.getX())
							.isEqualTo(fishExpected[i][0]);
						assertThat(fish.getY())
							.isEqualTo(fishExpected[i][1]);
		}

		for (int i = 0; i < game.gamePlayers.get(0).drones.size(); i++) {
			Drone drone = game.gamePlayers.get(0).drones.get(i);
//						System.err.println("{" + drone.getX() + "," + drone.getY() + "},");
			assertThat(drone.getX())
				.isEqualTo(myDrone[i][0]);
			assertThat(drone.getY())
				.isEqualTo(myDrone[i][1]);
		}

		for (int i = 0; i < game.gamePlayers.get(1).drones.size(); i++) {
			Drone drone = game.gamePlayers.get(1).drones.get(i);
//			System.err.println("{" + drone.getX() + "," + drone.getY() + "},");
						assertThat(drone.getX())
							.isEqualTo(foeDrone[i][0]);
						assertThat(drone.getY())
							.isEqualTo(foeDrone[i][1]);
		}
	}
}
