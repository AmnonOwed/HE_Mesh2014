package wblut.hemesh;

public class HET_ProgressTracker {
	protected volatile int counter;
	protected volatile int counterlimit;
	protected volatile String status;

	protected HET_ProgressTracker() {
		reset();
	}

	public void reset() {

		counter = 0;
		counterlimit = 0;
		status = "";

	}

	private static final HET_ProgressTracker tracker = new HET_ProgressTracker();

	public static HET_ProgressTracker instance() {
		return tracker;
	}

	public void incrementCounter() {
		counter++;
	}

	public void incrementCounter(final int inc) {
		counter += inc;
	}

	public void resetCounter() {
		counter = 0;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(final int c) {
		counter = c;

	}

	public void setCounterLimit(final int limit) {
		counterlimit = limit;

	}

	public void resetCounterLimit() {
		counterlimit = 0;
	}

	public int getCounterLimit() {
		return counterlimit;

	}

	public String getCounterString() {
		if (counterlimit == 0) {
			return "";
		}
		return "(" + counter + " of " + counterlimit + ")";
	}

	public String getStatus() {
		return status + getCounterString();
	}

	public void setStatus(final String status) {
		resetCounter();
		resetCounterLimit();
		this.status = status;
	}

	public void setStatus(final String status, final int limit) {
		resetCounter();
		setCounterLimit(limit);
		this.status = status;
	}

}
