/*
 * 
 */
package wblut.hemesh;

/**
 * 
 */
public class HET_ProgressTracker {
    
    /**
     * 
     */
    protected volatile int counter;
    
    /**
     * 
     */
    protected volatile int counterlimit;
    
    /**
     * 
     */
    protected volatile String status;
    
    /**
     * 
     */
    protected volatile int currentPriority;

    /**
     * 
     */
    protected HET_ProgressTracker() {
	reset();
    }

    /**
     * 
     */
    protected void reset() {
	counter = 0;
	counterlimit = 0;
	status = "";
	currentPriority = 0;
    }

    /**
     * 
     */
    private static final HET_ProgressTracker tracker = new HET_ProgressTracker();

    /**
     * 
     *
     * @return 
     */
    public static HET_ProgressTracker instance() {
	return tracker;
    }

    /**
     * 
     */
    public void incrementCounter() {
	counter++;
    }

    /**
     * 
     *
     * @param inc 
     */
    public void incrementCounter(final int inc) {
	counter += inc;
    }

    /**
     * 
     */
    protected void resetCounter() {
	counter = 0;
    }

    /**
     * 
     *
     * @return 
     */
    public int getCounter() {
	return counter;
    }

    /**
     * 
     *
     * @param c 
     */
    protected void setCounter(final int c) {
	counter = c;
    }

    /**
     * 
     *
     * @param limit 
     */
    protected void setCounterLimit(final int limit) {
	counterlimit = limit;
    }

    /**
     * 
     */
    protected void resetCounterLimit() {
	counterlimit = 0;
    }

    /**
     * 
     *
     * @return 
     */
    public int getCounterLimit() {
	return counterlimit;
    }

    /**
     * 
     *
     * @return 
     */
    public String getCounterString() {
	if (counterlimit == 0) {
	    return "";
	}
	return "(" + counter + " of " + counterlimit + ")";
    }

    /**
     * 
     *
     * @return 
     */
    public String getStatus() {
	return status + getCounterString();
    }

    /**
     * 
     *
     * @param status 
     * @param p 
     */
    public void setStatus(final String status, final int p) {
	if (p >= currentPriority) {
	    resetCounter();
	    resetCounterLimit();
	    this.status = status;
	}
    }

    /**
     * 
     *
     * @param status 
     * @param limit 
     * @param p 
     */
    public void setStatus(final String status, final int limit, final int p) {
	if (p >= currentPriority) {
	    resetCounter();
	    setCounterLimit(limit);
	    this.status = status;
	}
    }

    /**
     * 
     *
     * @param status 
     */
    public void setDefaultStatus(final String status) {
	setStatus(status, 0);
    }

    /**
     * 
     *
     * @param status 
     * @param limit 
     */
    public void setDefaultStatus(final String status, final int limit) {
	setStatus(status, limit, 0);
    }

    /**
     * 
     *
     * @param p 
     */
    public void setPriority(final int p) {
	currentPriority = p;
    }
}
