package fr.tcordel.model;

import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameEstimatorTest {

	private GameEstimator gameEstimator;

	@BeforeEach
	void reset() {
		gameEstimator = new GameEstimator();
	}
	@Test
	void test() {
		int localPrize = gameEstimator.computeScanScore(Set.of(new Scan(FishType.FISH, 0), new Scan(FishType.FISH, 1), new Scan(FishType.FISH, 2), new Scan(FishType.FISH, 3)), 1);
		int oppLazyCommitScore = gameEstimator.computeFullEndGameScore(new GamePlayer(0));
		int myEndgameScore = gameEstimator.computeFullEndGameScore(new GamePlayer(1));

		Assertions.assertThat(localPrize)
			.isEqualTo(24);

		Assertions.assertThat(oppLazyCommitScore)
			.isEqualTo(84);

		Assertions.assertThat(myEndgameScore)
			.isEqualTo(60);
	}

	@Test
	void testCounterAttack() {
		gameEstimator.commit(Set.of(new Scan(FishType.FISH, 0), new Scan(FishType.FISH, 2), new Scan(FishType.JELLY, 1)), Set.of());
		gameEstimator.commit(Set.of(), Player.ALL_SCANS.stream().filter(s -> !s.equals(new Scan(FishType.CRAB, 0))).collect(Collectors.toSet()));
		int myScore = gameEstimator.computeFullEndGameScore(new GamePlayer(GamePlayer.ME));
		int foeScore = gameEstimator.computeFullEndGameScore(new GamePlayer(GamePlayer.FOE));

		Assertions.assertThat(myScore)
			.isEqualTo(72);

		Assertions.assertThat(foeScore)
			.isEqualTo(91);
		assert false;
	}

	@Test
	void testMissingCounterAttack() {

		int localPrize = gameEstimator.computeScanScore(
			Set.of(
				new Scan(FishType.JELLY, 0), new Scan(FishType.JELLY, 1), new Scan(FishType.JELLY, 3),
				new Scan(FishType.FISH, 1), new Scan(FishType.FISH, 3),
				new Scan(FishType.CRAB, 0), new Scan(FishType.CRAB, 3)

			),
			GamePlayer.FOE);

		gameEstimator.computeScanScore(
			Set.of(
				new Scan(FishType.JELLY, 0), new Scan(FishType.JELLY, 1), new Scan(FishType.JELLY, 2), new Scan(FishType.JELLY, 3),
				new Scan(FishType.FISH, 0), new Scan(FishType.FISH, 2), new Scan(FishType.FISH, 3),
				new Scan(FishType.CRAB, 1), new Scan(FishType.CRAB, 2), new Scan(FishType.CRAB, 3)

			),
			GamePlayer.ME);

		Scan missing = new Scan(FishType.FISH, 1);
		Player.ALL_AVAILABLE_SCANS.remove(missing);
		GamePlayer gamePlayer = new GamePlayer(GamePlayer.FOE);
		gamePlayer.scans.add(missing);
		int foeScore = gameEstimator.computeFullEndGameScore(gamePlayer);
		int myScore = gameEstimator.computeFullEndGameScore(new GamePlayer(GamePlayer.ME));


		Assertions.assertThat(foeScore)
			.isEqualTo(78);

		Assertions.assertThat(myScore)
			.isEqualTo(57);


	}

	@Test
	void testCommit() {

		gameEstimator.commit(Set.of(),
			Set.of(new Scan(FishType.JELLY, 2),new Scan(FishType.JELLY, 0),new Scan(FishType.CRAB, 0),new Scan(FishType.FISH, 2),new Scan(FishType.FISH, 0)));

		Assertions.assertThat(gameEstimator.getScore(GamePlayer.ME))
			.isEqualTo(0);
		Assertions.assertThat(gameEstimator.getScore(GamePlayer.FOE))
			.isEqualTo(24);
		gameEstimator.commit(Set.of(new Scan(FishType.JELLY, 2),new Scan(FishType.JELLY, 3),new Scan(FishType.JELLY, 1),new Scan(FishType.CRAB, 0),new Scan(FishType.CRAB, 1),new Scan(FishType.CRAB, 2),new Scan(FishType.CRAB, 3),new Scan(FishType.FISH, 3),new Scan(FishType.FISH, 2),new Scan(FishType.FISH, 1),new Scan(FishType.FISH, 0)),
			Set.of(new Scan(FishType.JELLY, 3),new Scan(FishType.JELLY, 1),new Scan(FishType.CRAB, 1),new Scan(FishType.CRAB, 3),new Scan(FishType.FISH, 3),new Scan(FishType.FISH, 1)));

		Assertions.assertThat(gameEstimator.getScore(GamePlayer.ME))
			.isEqualTo(50);
		Assertions.assertThat(gameEstimator.getScore(GamePlayer.FOE))
			.isEqualTo(54);
	}

	@Test
	void testCommit2() {

		gameEstimator.commit(Set.of(new Scan(FishType.JELLY, 2),new Scan(FishType.JELLY, 3),new Scan(FishType.JELLY, 0),new Scan(FishType.JELLY, 1),new Scan(FishType.CRAB, 2),new Scan(FishType.CRAB, 3),new Scan(FishType.FISH, 3),new Scan(FishType.FISH, 2),new Scan(FishType.FISH, 1),new Scan(FishType.FISH, 0)),
			Set.of(new Scan(FishType.JELLY, 2),new Scan(FishType.JELLY, 3),new Scan(FishType.JELLY, 0),new Scan(FishType.JELLY, 1),new Scan(FishType.CRAB, 2),new Scan(FishType.CRAB, 3),new Scan(FishType.FISH, 3),new Scan(FishType.FISH, 2),new Scan(FishType.FISH, 1),new Scan(FishType.FISH, 0)));

		Assertions.assertThat(gameEstimator.getScore(GamePlayer.ME))
			.isEqualTo(32);
		Assertions.assertThat(gameEstimator.getScore(GamePlayer.FOE))
			.isEqualTo(32);
	}
}