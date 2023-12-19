package fr.tcordel.model;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

public class GamePerformanceTest {

	@Test
	void test() throws NoSuchAlgorithmException {
		Game.DRONES_PER_PLAYER = 4;
		Game.COLORS_PER_FISH = 12;
		Game.turnSavedFish = new int[][] {{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
			{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};
		Game game = GameUtils.initGame(-8103073722892937000L);

		for (int i = 0; i < Game.MAX_TURNS*1000; i++) {
			game.performGameUpdate(i);
		}
	}
}
