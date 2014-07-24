package wblut.core;

import javolution.context.LogContext;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class WB_Logger {

	public static WB_Logger log = new WB_Logger();
	private static Logger logger;

	protected WB_Logger() {
		logger = Logger.getLogger("wblut");
		BasicConfigurator.configure();
		// Basically disable Javolution logging.
		final LogContext ctx = LogContext.enter();
		ctx.setLevel(LogContext.Level.FATAL);
	}

	public static WB_Logger instance() {
		return log;
	}

	static public void setLevel(final Level level) {
		logger.setLevel(level);
	}

	static public void setDebug() {
		logger.setLevel(Level.DEBUG);
	}

	static public void setInfo() {
		logger.setLevel(Level.INFO);
	}

	static public void setTrace() {
		logger.setLevel(Level.TRACE);
	}

	static public void setWarning() {
		logger.setLevel(Level.WARN);
	}

	static public void setError() {
		logger.setLevel(Level.ERROR);
	}

	static public void setFatal() {
		logger.setLevel(Level.FATAL);
	}

	static public void setOff() {
		logger.setLevel(Level.OFF);
	}

}
