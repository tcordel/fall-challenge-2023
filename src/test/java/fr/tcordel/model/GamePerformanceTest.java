package fr.tcordel.model;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

public class GamePerformanceTest {

	@Test
	void test() throws NoSuchAlgorithmException {
		Game game = GameUtils.initGame(-8103073722892937000L);

		for (int i = 0; i < Game.MAX_TURNS; i++) {
			game.performGameUpdate(i);
		}
	}
}
