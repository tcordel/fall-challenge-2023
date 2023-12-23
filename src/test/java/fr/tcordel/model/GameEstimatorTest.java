package fr.tcordel.model;

import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameEstimatorTest {

	private final GameEstimator gameEstimator = new GameEstimator();

	@BeforeEach
	void reset() {
		gameEstimator.reset();
	}
	@Test
	void test() {
		int localPrize = gameEstimator.computeScanScore(Set.of(new Scan(FishType.FISH, 0), new Scan(FishType.FISH, 1), new Scan(FishType.FISH, 2), new Scan(FishType.FISH, 3)), 1);
		int oppLazyCommitScore = gameEstimator.computeFullEndGameScore(0);
		int myEndgameScore = gameEstimator.computeFullEndGameScore(1);

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
		gameEstimator.commit(Set.of(), gameEstimator.allScans.stream().filter(s -> !s.equals(new Scan(FishType.CRAB, 0))).collect(Collectors.toSet()));
		int myScore = gameEstimator.computeFullEndGameScore(GamePlayer.ME);
		int foeScore = gameEstimator.computeFullEndGameScore(GamePlayer.FOE);

		Assertions.assertThat(myScore)
			.isEqualTo(72);

		Assertions.assertThat(foeScore)
			.isEqualTo(91);
		assert false;
	}
}