
package fr.tcordel.bridge;

public abstract class AbstractMultiplayerPlayer extends AbstractPlayer {

	private boolean active = true;

	public AbstractMultiplayerPlayer() {
	}

	public final int getColorToken() {
		return -(this.index + 1);
	}

	public final boolean isActive() {
		return this.active;
	}

	public final int getIndex() {
		return super.getIndex();
	}

	public final int getScore() {
		return super.getScore();
	}

	public final void setScore(int score) {
		super.setScore(score);
	}

	public final void deactivate() {
		this.deactivate((String)null);
	}

	public final void deactivate(String reason) {
		this.active = false;
		//        if (reason != null) {
		//            ((GameManager)this.gameManagerProvider.get()).addTooltip(new Tooltip(this.index, reason));
		//        }
	}
}
