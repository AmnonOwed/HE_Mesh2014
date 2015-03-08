/*
 * 
 */
package wblut.hemesh;

/**
 * 
 */
public class HET_ProgressReporter extends Thread {
    
    /**
     * 
     */
    int waitingTime;
    
    /**
     * 
     */
    HET_ProgressTracker tracker;
    
    /**
     * 
     */
    String status, prevstatus;

    /**
     * 
     *
     * @param millis 
     */
    public HET_ProgressReporter(final int millis) {
	super();
	waitingTime = millis;
	tracker = HET_ProgressTracker.instance();
	prevstatus = "";
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#start()
     */
    @Override
    public void start() {
	super.start();
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
	while (!Thread.interrupted()) {
	    try {
		Thread.sleep(waitingTime);
		status = tracker.getStatus();
		if (!status.equals(prevstatus)) {
		    System.out.println(status);
		}
		prevstatus = status;
	    } catch (final InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
}
