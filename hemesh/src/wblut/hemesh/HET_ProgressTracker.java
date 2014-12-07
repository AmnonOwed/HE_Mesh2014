package wblut.hemesh;

public class HET_ProgressTracker {
    protected volatile int counter;
    protected volatile int counterlimit;
    protected volatile String status;
    protected volatile int currentPriority;

    protected HET_ProgressTracker() {
	reset();
    }

    protected void reset() {
	counter = 0;
	counterlimit = 0;
	status = "";
	currentPriority = 0;
    }

    private static final HET_ProgressTracker tracker = new HET_ProgressTracker();

    public static HET_ProgressTracker instance() {
	return tracker;
    }

    protected void incrementCounter() {
	counter++;
    }

    protected void incrementCounter(final int inc) {
	counter += inc;
    }

    protected void resetCounter() {
	counter = 0;
    }

    public int getCounter() {
	return counter;
    }

    protected void setCounter(final int c) {
	counter = c;
    }

    protected void setCounterLimit(final int limit) {
	counterlimit = limit;
    }

    protected void resetCounterLimit() {
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

    public void setStatus(final String status, final int p) {
	if (p >= currentPriority) {
	    resetCounter();
	    resetCounterLimit();
	    this.status = status;
	}
    }

    public void setStatus(final String status, final int limit, final int p) {
	if (p >= currentPriority) {
	    resetCounter();
	    setCounterLimit(limit);
	    this.status = status;
	}
    }

    public void setDefaultStatus(final String status) {
	setStatus(status, 0);
    }

    public void setDefaultStatus(final String status, final int limit) {
	setStatus(status, limit, 0);
    }

    public void setPriority(final int p) {
	currentPriority = p;
    }
}
