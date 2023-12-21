package fr.tcordel.model;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class GameEstimatorTest {

	private final GameEstimator gameEstimator = new GameEstimator();
	@Test
	void test() {
		int localPrize = gameEstimator.computeScanScore(Set.of(new Scan(FishType.FISH, 0), new Scan(FishType.FISH, 1), new Scan(FishType.FISH, 2), new Scan(FishType.FISH, 3)), 1);
		int oppLazyCommitScore = gameEstimator.computeEndGameScore(0);
		int myEndgameScore = gameEstimator.computeEndGameScore(1);

		Assertions.assertThat(localPrize)
			.isEqualTo(24);

		Assertions.assertThat(oppLazyCommitScore)
			.isEqualTo(84);

		Assertions.assertThat(myEndgameScore)
			.isEqualTo(60);
	}
}