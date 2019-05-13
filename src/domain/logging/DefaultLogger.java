/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.logging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author thomas
 */
public class DefaultLogger {
    private final static Logger LOGGER = Logger.getLogger(domain.logging.DefaultLogger.class.getName());

    /**
     *
     * @param production set location for log file depending on whether program is deployed to production server or if running in development mode.
     * @return the Logger to use from anywhere in the application.
     */
    public static Logger getLogger(boolean production, boolean withStackTrace){
        LOGGER.addHandler(new ConsoleHandler());
        try {
            if(production){
                FileHandler handler = new FileHandler(domain.configuration.Conf.LOG_FILE_PATH);
//          handler.setFormatter(new SimpleFormatter());
                if(withStackTrace)
                    handler.setFormatter(new withStackTraceFormatter());
                else
                    handler.setFormatter(new VerySimpleFormatter());
                LOGGER.addHandler(handler);
            } else {
                FileHandler handler = new FileHandler(domain.configuration.Conf.LOG_FILE_PATH_DEVELOP); // see: http://tutorials.jenkov.com/java-logging/handlers.html
                if(withStackTrace)
                    handler.setFormatter(new withStackTraceFormatter());
                else
                    handler.setFormatter(new VerySimpleFormatter());
                LOGGER.addHandler(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return LOGGER;
    }

    private static class VerySimpleFormatter extends Formatter {

        private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

        @Override
        public String format(final LogRecord record) {
            return String.format(
                    "%1$s %2$-7s %3$s\n",
                    new SimpleDateFormat(PATTERN).format(
                            new Date(record.getMillis())),
                    record.getLevel().getName(),
                    formatMessage(record));
        }
    }

    private static class withStackTraceFormatter extends Formatter {

        private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

        @Override
        public String format(final LogRecord record) {
            StackTraceElement[] traces = record.getThrown().getStackTrace();
            StringBuilder sb = new  StringBuilder("Stacktrace: \n--------------------------------------------------------\n");
            for (StackTraceElement trace : traces) {
                sb.append("Line number: "+trace.getLineNumber()+"in Method: "+trace.getMethodName()+" in class "+trace.getClassName()+" in file: "+trace.getFileName()+"\n");
            }
            return String.format(
                    "%1$s %2$-7s %3$s %4$s\n",
                    new SimpleDateFormat(PATTERN).format(new Date(record.getMillis())),
                    record.getLevel().getName(),
                    formatMessage(record),
                    sb.toString()
            );
        }
    }
}