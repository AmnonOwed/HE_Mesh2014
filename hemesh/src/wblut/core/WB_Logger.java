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

	static public WB_Logger setLevel(final Level level) {
		logger.setLevel(level);
		return log;
	}

	static public WB_Logger setDebug() {
		logger.setLevel(Level.DEBUG);
		return log;
	}

	static public WB_Logger setInfo() {
		logger.setLevel(Level.INFO);
		return log;
	}

	static public WB_Logger setTrace() {
		logger.setLevel(Level.TRACE);
		return log;
	}

	static public WB_Logger setWarning() {
		logger.setLevel(Level.WARN);
		return log;
	}

	static public WB_Logger setError() {
		logger.setLevel(Level.ERROR);
		return log;
	}

	static public WB_Logger setFatal() {
		logger.setLevel(Level.FATAL);
		return log;
	}

	static public WB_Logger setOff() {
		logger.setLevel(Level.OFF);
		return log;
	}

}
