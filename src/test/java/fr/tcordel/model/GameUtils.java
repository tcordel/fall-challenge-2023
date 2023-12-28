package fr.tcordel.model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.tcordel.model.Game.DRONE_MOVE_SPEED;

public class GameUtils {

	static Game initGame(long seed) throws NoSuchAlgorithmException {

		Game game = new Game();
		int leagueLevel = 1;
		if (leagueLevel == 1) {
			Game.ENABLE_UGLIES = false;
			Game.FISH_WILL_FLEE = false;
			Game.DRONES_PER_PLAYER = 1;
			Game.SIMPLE_SCANS = true;
			Game.FISH_WILL_MOVE = true;
		} else if (leagueLevel == 2) {
			Game.ENABLE_UGLIES = false;
			Game.FISH_WILL_FLEE = false;
			Game.DRONES_PER_PLAYER = 1;
		} else if (leagueLevel == 3) {
			Game.ENABLE_UGLIES = false;
		} else {

		}

		game.random = SecureRandom.getInstance("SHA1PRNG");
		game.random.setSeed(seed);
		GamePlayer me = new GamePlayer();
		me.setIIndex(0);
		GamePlayer foe = new GamePlayer();
		foe.setIIndex(1);
		game.gamePlayers = List.of(me, foe);
		game.init();
		return game;
	}

	@Test
	@DisplayName("")
	void test() {
		System.err.println(RadarDirection.TL.getDirection().normalize().mult(DRONE_MOVE_SPEED).round());
	}
}
