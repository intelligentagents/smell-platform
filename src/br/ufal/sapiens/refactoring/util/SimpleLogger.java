package br.ufal.sapiens.refactoring.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SimpleLogger {

	private static final Logger LOGGER = Logger.getLogger(SimpleLogger.class.getName());

    static {
        Formatter formatter = new Formatter() {
        	public String format(LogRecord record){
    		    return record.getMessage() + "\r\n";
    		}
        };
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        LOGGER.addHandler(consoleHandler);
    }
    
    public static Logger getLogger() {
    	LOGGER.setLevel(Level.OFF);
    	return LOGGER;
    }
    
    public static void log(String message) {
        getLogger().info(message);
    }

}

