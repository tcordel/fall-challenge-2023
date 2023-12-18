package fr.tcordel.bridge;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlayer {

	protected int index;
	private List<String> inputs = new ArrayList();
	private List<String> outputs;
	private boolean timeout;
	private int score;
	private boolean hasBeenExecuted;
	private boolean hasNeverBeenExecuted = true;

	public AbstractPlayer() {
	}

	public final String getNicknameToken() {
		return "$" + this.index;
	}

	public final String getAvatarToken() {
		return "$" + this.index;
	}

	int getIndex() {
		return this.index;
	}

	int getScore() {
		return this.score;
	}

	void setScore(int score) {
		this.score = score;
	}

//	public final void sendInputLine(String line) {
    //		if (this.hasBeenExecuted) {
    //			throw new RuntimeException("Impossible to send new inputs after calling execute");
    //		} else if (((GameManager)this.gameManagerProvider.get()).getOuputsRead()) {
    //			throw new RuntimeException("Sending input data to a player after reading any output is forbidden.");
    //		} else {
    //			this.inputs.add(line);
    //		}
    //	}
    //
    //	public final void execute() {
    //		((GameManager)this.gameManagerProvider.get()).execute(this);
    //		this.hasBeenExecuted = true;
    //		this.hasNeverBeenExecuted = false;
    //	}
    //
    //	public final List<String> getOutputs() throws TimeoutException {
    //		((GameManager)this.gameManagerProvider.get()).setOuputsRead(true);
    //		if (!this.hasBeenExecuted) {
    //			throw new RuntimeException("Can't get outputs without executing it!");
    //		} else if (this.timeout) {
    //			throw new TimeoutException();
    //		} else {
    //			return this.outputs;
    //		}
    //	}

	public abstract int getExpectedOutputLines();

	final void setIndex(int index) {
		this.index = index;
	}

	final List<String> getInputs() {
		return this.inputs;
	}

	final void resetInputs() {
		this.inputs = new ArrayList();
	}

	final void resetOutputs() {
		this.outputs = null;
	}

	final void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}

	final void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	final boolean hasTimedOut() {
		return this.timeout;
	}

	final boolean hasBeenExecuted() {
		return this.hasBeenExecuted;
	}

	final void setHasBeenExecuted(boolean hasBeenExecuted) {
		this.hasBeenExecuted = hasBeenExecuted;
	}

	final boolean hasNeverBeenExecuted() {
		return this.hasNeverBeenExecuted;
	}

	public static class TimeoutException extends Exception {

		private static final long serialVersionUID = 42L;

		public TimeoutException() {
		}
	}
}
